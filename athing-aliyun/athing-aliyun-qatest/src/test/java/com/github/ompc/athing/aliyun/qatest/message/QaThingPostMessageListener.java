package com.github.ompc.athing.aliyun.qatest.message;

import com.github.ompc.athing.standard.platform.message.ThingMessage;
import com.github.ompc.athing.standard.platform.message.ThingMessageListener;
import com.github.ompc.athing.standard.platform.message.ThingPostMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class QaThingPostMessageListener implements ThingMessageListener {

    private final ConcurrentHashMap<String, Waiter> reqWaiterMap = new ConcurrentHashMap<>();

    @Override
    public void onMessage(ThingMessage message) {

        if (!(message instanceof ThingPostMessage)) {
            return;
        }
        final ThingPostMessage postMsg = (ThingPostMessage) message;
        final Waiter existed, current = new Waiter(postMsg);
        if ((existed = reqWaiterMap.putIfAbsent(postMsg.getReqId(), current)) != null) {
            existed.message = postMsg;
            existed.latch.countDown();
        }

    }

    @SuppressWarnings("unchecked")
    public <T extends ThingPostMessage> T waitingForPostMessageByReqId(String reqId) throws InterruptedException {
        final Waiter existed, current = new Waiter();
        final Waiter waiter = (existed = reqWaiterMap.putIfAbsent(reqId, current)) != null
                ? existed
                : current;
        waiter.latch.await();
        return (T) waiter.message;
    }

    private static class Waiter {

        private final CountDownLatch latch = new CountDownLatch(1);
        private ThingPostMessage message;

        public Waiter() {
        }

        public Waiter(ThingPostMessage message) {
            this.message = message;
            this.latch.countDown();
        }
    }

}
