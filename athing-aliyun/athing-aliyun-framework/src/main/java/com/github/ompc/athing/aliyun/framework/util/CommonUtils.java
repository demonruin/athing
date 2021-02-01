package com.github.ompc.athing.aliyun.framework.util;

import java.util.Collection;

/**
 * 通用工具类
 */
public class CommonUtils {

    /**
     * 判断对象是否在数组中
     *
     * @param target 目标对象
     * @param array  目标数组
     * @return TRUE | FALSE
     */
    public static boolean isIn(Object target, Object... array) {
        if (null == array) {
            return false;
        }
        for (Object e : array) {
            if (target.equals(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个集合是否为空
     *
     * @param collection 集合
     * @return TRUE | FALSE
     */
    public static boolean isEmptyCollection(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    /**
     * 判断一个字符串是否为空
     *
     * @param string 字符串
     * @return TRUE | FALSE
     */
    public static boolean isEmptyString(String string) {
        return null == string || string.isEmpty();
    }

}
