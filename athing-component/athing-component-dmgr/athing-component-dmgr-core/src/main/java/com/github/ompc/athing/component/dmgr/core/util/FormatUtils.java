package com.github.ompc.athing.component.dmgr.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数字操作工具
 */
public class FormatUtils {

    public static float rate(float rate) {
        return BigDecimal.valueOf(rate)
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
    }

    public static float rate(double rate) {
        return BigDecimal.valueOf(rate)
                .setScale(2, RoundingMode.HALF_UP)
                .floatValue();
    }

    public static int toInt(float value) {
        return BigDecimal.valueOf(value)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

}
