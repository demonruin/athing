package com.github.ompc.athing.aliyun.thing.executor;

import com.github.ompc.athing.standard.thing.ThingOpCb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备操作请求应答
 */
public class ThingOpPingPong {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, ThingOpCb<?>> reqIdOpCbMap = new ConcurrentHashMap<>();

    /**
     * ping
     *
     * @param reqId 请求ID
     * @param opCb  执行回调
     */
    public void ping(String reqId, ThingOpCb<?> opCb) {
        logger.debug("ping: {} --> SERVER", reqId);
        reqIdOpCbMap.put(reqId, opCb);
    }

    /**
     * pong
     *
     * @param reqId 请求ID
     * @param <T>   执行回调类型
     * @return 执行回调
     */
    @SuppressWarnings("unchecked")
    public <T> ThingOpCb<T> pong(String reqId) {
        logger.debug("pong: {} <-- SERVER", reqId);
        return (ThingOpCb<T>) reqIdOpCbMap.remove(reqId);
    }

    /**
     * ping in block
     *
     * @param reqId 请求ID
     * @param opCb  执行回调
     * @param block 执行块
     * @throws Exception 执行异常
     */
    public void pingInBlock(String reqId, ThingOpCb<?> opCb, PingBlock block) throws Exception {
        ping(reqId, opCb);
        try {
            block.block();
        } catch (Exception cause) {
            pong(reqId);
            throw cause;
        }
    }

    /**
     * Ping块，块执行成功后ping才能生效
     */
    public interface PingBlock {

        /**
         * 执行块
         *
         * @throws Exception 执行异常
         *                   ping不生效
         */
        void block() throws Exception;

    }

}
