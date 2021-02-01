package com.github.ompc.athing.standard.thing;

/**
 * 设备操作回调
 *
 * @param <T> 回调结果类型
 */
public interface ThingOpCb<T> {

    /**
     * 操作回调
     *
     * @param id    请求ID
     * @param reply 调用应答，操作成功时非空
     */
    void callback(String id, OpReply<T> reply);

    /**
     * 操作应答
     */
    interface OpReply<T> {

        /**
         * 是否应答成功
         *
         * @return TRUE|FALSE
         */
        boolean isSuccess();

        /**
         * 获取操作应答码
         *
         * @return 平台应答码
         */
        String getCode();

        /**
         * 获取操作应答信息
         *
         * @return 平台应答信息
         */
        String getMessage();

        /**
         * 获取平台应答数据
         *
         * @return 平台应答数据
         */
        T getData();

    }

}
