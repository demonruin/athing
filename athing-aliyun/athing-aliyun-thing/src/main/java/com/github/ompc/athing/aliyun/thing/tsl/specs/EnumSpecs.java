package com.github.ompc.athing.aliyun.thing.tsl.specs;

import com.github.ompc.athing.aliyun.thing.tsl.schema.TslDataType;

import java.util.LinkedHashMap;

public class EnumSpecs extends LinkedHashMap<Integer, String> implements TslSpecs {
    @Override
    public TslDataType.Type getType() {
        return TslDataType.Type.ENUM;
    }
}
