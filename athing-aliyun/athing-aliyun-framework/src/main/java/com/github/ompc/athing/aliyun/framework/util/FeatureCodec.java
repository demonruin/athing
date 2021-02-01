package com.github.ompc.athing.aliyun.framework.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.ompc.athing.aliyun.framework.Constants.FEATURE_KEY_FEATURE_ENABLE;
import static com.github.ompc.athing.aliyun.framework.Constants.FEATURE_VAL_FEATURE_ENABLE_TRUE;

/**
 * Feature编解码器
 */
public class FeatureCodec {

    private static final String FEATURE_ENABLE_PREFIX = String.format("%s=%s;", FEATURE_KEY_FEATURE_ENABLE, FEATURE_VAL_FEATURE_ENABLE_TRUE);

    /**
     * 将featureMap编码为feature字符串
     *
     * @param featureMap Map of feature
     * @return feature string
     */
    public static String encode(Map<String, String> featureMap) {
        final StringBuilder featureSB = new StringBuilder(FEATURE_ENABLE_PREFIX);
        if (null != featureMap) {
            featureMap.forEach((key, value) -> {
                // 过滤掉feature前缀KV
                if (key.equals(FEATURE_KEY_FEATURE_ENABLE)) {
                    return;
                }
                featureSB.append(key).append("=").append(value).append(";");
            });
        }
        return featureSB.toString();
    }

    /**
     * 将feature字符串解码为featureMap
     *
     * @param feature feature string
     * @return Map of feature
     */
    public static Map<String, String> decode(String feature) {
        final Map<String, String> featureMap = new LinkedHashMap<>();
        if (null != feature && feature.startsWith(FEATURE_ENABLE_PREFIX)) {
            Stream.of(feature.split(";"))
                    .filter(kvSeg -> !kvSeg.isEmpty())
                    .forEach(kvSeg -> {
                        final String[] kv = kvSeg.split("=");
                        if (kv.length == 2) {
                            featureMap.put(kv[0], kv[1]);
                        }
                    });
        }
        return featureMap;
    }

}
