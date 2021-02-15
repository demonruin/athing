package com.github.ompc.athing.aliyun.framework.component;

import com.github.ompc.athing.aliyun.framework.component.meta.ThComMeta;
import com.github.ompc.athing.aliyun.framework.component.meta.ThComMetaFactory;
import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.component.ThingCom;

import java.util.Map;
import java.util.stream.Collectors;

import static com.github.ompc.athing.standard.component.util.ThComUtils.getThComInterfaces;

/**
 * 设备组件元数据工具类
 */
public class ThComMetaHelper {

    /**
     * 获取类型上声明的组件元数据
     *
     * @param clazz 类型
     * @return 组件元数据集合
     */
    public static Map<String, ThComMeta> getThComMetaMap(Class<? extends ThingCom> clazz) {
        return getThComInterfaces(clazz).stream()
                .map(ThComMetaFactory::make)
                .collect(Collectors.toMap(
                        ThComMeta::getThingComId,
                        meta -> meta,
                        (a, b) -> a
                ));
    }

    /**
     * 转换为小写下划线分割
     *
     * @param string 目标字符串
     * @return 结果字符串
     */
    public static String toLowerCaseUnderscore(String string) {
        final StringBuilder translation = new StringBuilder();
        int i = 0;

        for (int length = string.length(); i < length; ++i) {
            char character = string.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append("_");
            }
            translation.append(Character.toLowerCase(character));
        }

        return translation.toString();
    }

    /**
     * 获取默认的成员名
     *
     * @param identifier 标识
     * @return 默认的成员名
     */
    public static String getDefaultMemberName(Identifier identifier) {
        return identifier.getMemberId().replaceAll("_", "-");
    }

}
