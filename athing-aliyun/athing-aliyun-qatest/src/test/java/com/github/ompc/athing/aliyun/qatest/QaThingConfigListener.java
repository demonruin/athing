package com.github.ompc.athing.aliyun.qatest;

import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.config.ThingConfig;
import com.github.ompc.athing.standard.thing.config.ThingConfigListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QaThingConfigListener implements ThingConfigListener {

    private final BlockingQueue<ThingConfig> queue = new LinkedBlockingQueue<>();

    @Override
    public void configThing(Thing thing, ThingConfig config) {
        queue.offer(config);
    }

    public ThingConfig waitingForReceiveThingConfig() throws InterruptedException {
        return queue.take();
    }

}
