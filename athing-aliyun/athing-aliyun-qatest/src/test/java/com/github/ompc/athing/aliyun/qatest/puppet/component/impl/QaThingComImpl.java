package com.github.ompc.athing.aliyun.qatest.puppet.component.impl;

import com.github.ompc.athing.aliyun.qatest.puppet.component.EchoThingCom;
import com.github.ompc.athing.aliyun.qatest.puppet.component.LightThingCom;

public class QaThingComImpl implements LightThingCom, EchoThingCom {

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
