package com.github.ompc.athing.aliyun.qatest.puppet.component.impl;

import com.github.ompc.athing.aliyun.qatest.puppet.component.EchoThingCom;
import com.github.ompc.athing.aliyun.qatest.puppet.component.LightThingCom;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.boot.ThingComBoot;

import java.util.Properties;

public class QaThingComBoot implements ThingComBoot {

    @Override
    public Specifications getSpecifications() {
        return Properties::new;
    }

    @Override
    public ThingCom bootUp(Thing thing, String arguments) {
        return new QaThingCom();
    }

    private static class QaThingCom implements LightThingCom, EchoThingCom {

        private volatile int bright = 0;
        private volatile State state = State.OFF;

        @Override
        public Echo echoBySync(String words) {
            return new Echo(words);
        }

        @Override
        public Echo echoByAsync(Echo echo) {
            return echo;
        }

        @Override
        public long now() {
            return System.currentTimeMillis();
        }

        @Override
        public int getBright() {
            return bright;
        }

        @Override
        public void setBright(int bright) {
            this.bright = bright;
        }

        @Override
        public State getState() {
            return state;
        }

        @Override
        public void setState(State state) {
            final State pre = this.state;
            this.state = state;
        }

        @Override
        public void turnOn() {
            setState(State.ON);
        }

        @Override
        public void turnOff() {
            setState(State.OFF);
        }


    }
}
