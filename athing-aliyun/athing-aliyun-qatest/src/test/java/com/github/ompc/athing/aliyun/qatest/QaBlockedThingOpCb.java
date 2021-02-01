package com.github.ompc.athing.aliyun.qatest;

import com.github.ompc.athing.standard.thing.ThingOpCb;

import java.util.concurrent.CountDownLatch;

public class QaBlockedThingOpCb<T> implements ThingOpCb<T> {

    private final CountDownLatch latch = new CountDownLatch(1);
    private volatile String id;
    private volatile OpReply<T> reply;

    @Override
    public void callback(String id, OpReply<T> reply) {
        this.id = id;
        this.reply = reply;
        latch.countDown();
    }

    public QaBlockedThingOpCb<T> waitForCompleted() throws InterruptedException {
        latch.await();
        return this;
    }

    public String getId() {
        return id;
    }

    public OpReply<T> getReply() {
        return reply;
    }

}
