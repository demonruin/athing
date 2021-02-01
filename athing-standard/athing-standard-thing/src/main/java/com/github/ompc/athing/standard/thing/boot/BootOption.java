package com.github.ompc.athing.standard.thing.boot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

/**
 * 启动选项
 */
public class BootOption {

    private final Map<String, String> optionMap = new HashMap<>();

    /**
     * 启动选项
     */
    public BootOption() {

    }

    /**
     * 启动选项
     *
     * @param arguments 启动参数
     */
    public BootOption(String arguments) {
        parse(arguments);
    }

    // 解析启动参数为启动选项
    private void parse(String arguments) {
        optionMap.putAll(
                Arrays.stream(arguments.split("&"))
                        .filter(Objects::nonNull)
                        .map(pair -> pair.split("="))
                        .collect(toMap(
                                segment -> segment[0],
                                segment -> segment[1],
                                (a, b) -> b)
                        )
        );
    }

    /**
     * 转换为启动参数
     *
     * @return 启动参数
     */
    public String getBootArguments() {
        final StringBuilder argSB = new StringBuilder("1=1");
        optionMap.forEach((key, value) ->
                argSB.append("&").append(key).append("=").append(value));
        return argSB.toString();
    }

    /**
     * 添加选项
     *
     * @param key   KEY
     * @param value 值
     * @return this
     */
    public BootOption option(String key, String value) {
        optionMap.put(key, value);
        return this;
    }

    /**
     * 判断选项是否存在
     *
     * @param key KEY
     * @return TRUE | FALSE
     */
    public boolean hasOption(String key) {
        return optionMap.containsKey(key);
    }

    /**
     * 判断选项是否存在，如存在则做出动作
     *
     * @param key    KEY
     * @param action 动作
     * @return this
     */
    public BootOption hasOption(String key, OptionAction action) {
        if (hasOption(key)) {
            action.action();
        }
        return this;
    }

    /**
     * 判断选项是否存在，如不存在则做出动作
     *
     * @param key    KEY
     * @param action 动作
     * @return this
     */
    public BootOption hasNoOption(String key, OptionAction action) {
        if (!hasOption(key)) {
            action.action();
        }
        return this;
    }

    // 检查KEY是否存在
    private void checkRequire(String key) {
        if (!optionMap.containsKey(key)) {
            throw new IllegalArgumentException(String.format("%s is required!", key));
        }
    }

    /**
     * 获取字符串
     *
     * @param key KEY
     * @return 字符串
     */
    public String get(String key) {
        return optionMap.get(key);
    }

    // ---------- 以下为选项各种获取动作 ----------

    /**
     * 获取字符串
     *
     * @param key KEY
     * @param def 默认值
     * @return 字符串
     */
    public String get(String key, String def) {
        return optionMap.getOrDefault(key, def);
    }

    /**
     * 要求字符串
     *
     * @param key KEY
     * @return 字符串
     */
    public String require(String key) {
        checkRequire(key);
        return get(key);
    }

    /**
     * 获取整数
     *
     * @param key KEY
     * @return 整数
     */
    public Integer getInt(String key) {
        return Integer.parseInt(get(key, "0"));
    }

    /**
     * 获取整数
     *
     * @param key KEY
     * @param def 默认值
     * @return 整数
     */
    public int getInt(String key, int def) {
        return Integer.parseInt(get(key, String.valueOf(def)));
    }

    /**
     * 要求整数
     *
     * @param key KEY
     * @return 整数
     */
    public int requireInt(String key) {
        return Integer.parseInt(require(key));
    }

    /**
     * 获取长整数
     *
     * @param key KEY
     * @return 长整数
     */
    public Long getLong(String key) {
        return Long.parseLong(get(key, "0"));
    }

    /**
     * 获取长整数
     *
     * @param key KEY
     * @param def 默认值
     * @return 长整数
     */
    public long getLong(String key, long def) {
        return Long.parseLong(get(key, String.valueOf(def)));
    }

    /**
     * 要求长整数
     *
     * @param key KEY
     * @return 长整数
     */
    public long requireLong(String key) {
        return Long.parseLong(require(key));
    }

    /**
     * 获取浮点数
     *
     * @param key KEY
     * @return 浮点数
     */
    public Float getFloat(String key) {
        return Float.parseFloat(get(key, "0"));
    }

    /**
     * 获取浮点数
     *
     * @param key KEY
     * @param def 默认值
     * @return 浮点数
     */
    public float getFloat(String key, float def) {
        return Float.parseFloat(get(key, String.valueOf(def)));
    }

    /**
     * 要求浮点数
     *
     * @param key KEY
     * @return 浮点数
     */
    public float requireFloat(String key) {
        return Float.parseFloat(require(key));
    }

    /**
     * 获取布尔值
     *
     * @param key KEY
     * @return 布尔值
     */
    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key, "true"));
    }

    /**
     * 获取布尔值
     *
     * @param key KEY
     * @param def 默认值
     * @return 布尔值
     */
    public boolean getBoolean(String key, boolean def) {
        return Boolean.parseBoolean(get(key, String.valueOf(def)));
    }

    /**
     * 要求布尔值
     *
     * @param key KEY
     * @return 布尔值
     */
    public boolean requireBoolean(String key) {
        return Boolean.parseBoolean(require(key));
    }

    @Override
    public String toString() {
        return getBootArguments();
    }

    /**
     * 动作
     */
    public interface OptionAction {

        /**
         * 执行
         */
        void action();

    }

}
