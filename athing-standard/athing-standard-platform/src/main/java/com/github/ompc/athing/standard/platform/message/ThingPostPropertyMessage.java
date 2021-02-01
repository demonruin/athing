package com.github.ompc.athing.standard.platform.message;

import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.platform.domain.ThingPropertySnapshot;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.github.ompc.athing.standard.platform.message.ThingMessage.Type.THING_POP_PROPERTIES;

/**
 * 设备上报属性消息
 */
public class ThingPostPropertyMessage extends ThingPostMessage {

    private final Map<String, ThingPropertySnapshot> propertySnapshotMap;

    /**
     * 设备上报属性消息
     *
     * @param productId           产品ID
     * @param thingId             设备ID
     * @param timestamp           消息时间戳
     * @param reqId               请求ID
     * @param propertySnapshotMap 设备上报属性快照集合
     */
    public ThingPostPropertyMessage(
            String productId, String thingId, long timestamp,
            String reqId,
            Map<String, ThingPropertySnapshot> propertySnapshotMap) {
        super(THING_POP_PROPERTIES, productId, thingId, timestamp, reqId);
        this.propertySnapshotMap = Collections.unmodifiableMap(propertySnapshotMap);
    }


    /**
     * 列出事件中的属性ID
     *
     * @return 事件中的属性ID集合
     */
    public Set<String> getPropertyIds() {
        return propertySnapshotMap.keySet();
    }

    /**
     * 获取属性值
     *
     * @param identifier 标识
     * @return 属性值
     */
    public ThingPropertySnapshot getPropertySnapshot(Identifier identifier) {
        return getPropertySnapshot(identifier.getIdentity());
    }

    /**
     * 获取属性值
     *
     * @param identity 标识值
     * @return 属性值
     */
    public ThingPropertySnapshot getPropertySnapshot(String identity) {
        return propertySnapshotMap.get(identity);
    }

}
