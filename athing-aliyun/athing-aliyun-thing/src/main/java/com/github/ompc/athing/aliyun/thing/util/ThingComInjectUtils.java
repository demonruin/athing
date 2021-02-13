package com.github.ompc.athing.aliyun.thing.util;

import com.github.ompc.athing.standard.thing.boot.ThDepend;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ThingComInjectUtils {

    public static Set<Field> getThDependFields(Class<?> clazz) {
        final Set<Field> fields = new HashSet<>();
        recGetThComInjectFields(clazz, fields);
        return fields;
    }

    private static void recGetThComInjectFields(Class<?> clazz, Set<Field> fields) {
        if (null == clazz || clazz.equals(Object.class)) {
            return;
        }
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ThDepend.class))
                .forEach(fields::add);
        recGetThComInjectFields(clazz.getSuperclass(), fields);
    }

}
