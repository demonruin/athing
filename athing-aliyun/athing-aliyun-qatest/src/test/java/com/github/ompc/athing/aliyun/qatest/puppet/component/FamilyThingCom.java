package com.github.ompc.athing.aliyun.qatest.puppet.component;

import com.github.ompc.athing.standard.component.ThingCom;

public interface FamilyThingCom extends ThingCom {

    FatherThingCom getFather();

    MotherThingCom getMother();

    PersonThingCom[] getMembers();

}
