package com.github.ompc.athing.aliyun.thing.tsl.specs;

import com.github.ompc.athing.aliyun.thing.tsl.schema.TslDataType;

public class DateSpecs implements TslSpecs {


    @Override
    public TslDataType.Type getType() {
        return TslDataType.Type.DATE;
    }
}
