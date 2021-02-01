package com.github.ompc.athing.aliyun.qatest.message;

import com.github.ompc.athing.standard.platform.message.ThingMessage;
import com.github.ompc.athing.standard.platform.message.ThingMessageListener;

public class QaThingMessageGroupListener implements ThingMessageListener {

    private final ThingMessageListener[] group;

    public QaThingMessageGroupListener(ThingMessageListener[] group) {
        this.group = group;
    }

    @Override
    public void onMessage(ThingMessage message) throws Exception {
        for (ThingMessageListener listener : group) {
            listener.onMessage(message);
        }
    }

}
