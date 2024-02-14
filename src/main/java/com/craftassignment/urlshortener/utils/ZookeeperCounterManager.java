package com.craftassignment.urlshortener.utils;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ZookeeperCounterManager implements DisposableBean {
    private static final String ZK_HOST = "localhost:2181";
    private static final String COUNTER_PATH = "/counter";
    private static final long COUNTER_RANGE_START = 1;
    private static final long COUNTER_RANGE_END = 10;
    private static final int SESSION_TIMEOUT = 60000;

    private final AtomicLong currentCounter = new AtomicLong();
    private ZooKeeper zooKeeper;

    @Autowired
    public ZookeeperCounterManager() {
        connect();
        initializeCounter();
    }

    private void connect() {
        try {
            final CountDownLatch connectedSignal = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(ZK_HOST, SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                        connectedSignal.countDown();
                    }
                }
            });
            connectedSignal.await();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to connect to ZooKeeper", e);
        }
    }

    private void initializeCounter() {
        try {
            Stat stat = zooKeeper.exists(COUNTER_PATH, false);
            if (stat == null) {
                zooKeeper.create(COUNTER_PATH, Long.toString(COUNTER_RANGE_START).getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                currentCounter.set(COUNTER_RANGE_START);
            } else {
                byte[] data = zooKeeper.getData(COUNTER_PATH, false, stat);
                long counter = data != null ? Long.parseLong(new String(data)) : COUNTER_RANGE_START;
                currentCounter.set(counter);
            }
        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException("Failed to initialize counter", e);
        }
    }

    public long getNextCounter() {
        long counter = currentCounter.getAndIncrement();
        if (counter >= COUNTER_RANGE_END) {
            currentCounter.set(COUNTER_RANGE_START);
            counter = COUNTER_RANGE_START;
        }
        try {
            zooKeeper.setData(COUNTER_PATH, Long.toString(counter).getBytes(), -1);
        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException("Failed to update counter", e);
        }
        return counter;
    }

    public void destroy() {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
