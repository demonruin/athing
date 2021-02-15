package com.github.ompc.athing.aliyun.qatest.puppet.component.impl;

import com.github.ompc.athing.aliyun.qatest.puppet.component.FamilyThingCom;
import com.github.ompc.athing.aliyun.qatest.puppet.component.FatherThingCom;
import com.github.ompc.athing.aliyun.qatest.puppet.component.MotherThingCom;
import com.github.ompc.athing.aliyun.qatest.puppet.component.PersonThingCom;
import com.github.ompc.athing.standard.thing.boot.ThDepend;

public class FamilyThingComImpl implements FamilyThingCom {

    @ThDepend
    private FatherThingCom father;

    @ThDepend
    private MotherThingCom mother;

    @ThDepend
    private PersonThingCom[] members;

    @Override
    public FatherThingCom getFather() {
        return father;
    }

    @Override
    public MotherThingCom getMother() {
        return mother;
    }

    @Override
    public PersonThingCom[] getMembers() {
        return members;
    }

}
