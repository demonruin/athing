package com.github.ompc.athing.aliyun.thing.executor.impl;

import com.github.ompc.athing.standard.thing.ThingOpCb;

public class ThingOpReplyImpl<E> implements ThingOpCb.OpReply<E> {

    private final boolean isReplySuccess;
    private final String replyCode;
    private final String replyMessage;
    private final E replyData;

    public ThingOpReplyImpl(boolean isReplySuccess, String replyCode, String replyMessage, E replyData) {
        this.isReplySuccess = isReplySuccess;
        this.replyCode = replyCode;
        this.replyMessage = replyMessage;
        this.replyData = replyData;
    }

    /**
     * 构建设备平台成功应答
     *
     * @param reply Alink协议的应答
     * @param data  应答数据
     * @param <T>   应答类型
     * @return 设备平台应答
     */
    public static <T> ThingOpCb.OpReply<T> success(AlinkReplyImpl<?> reply, T data) {
        return new ThingOpReplyImpl<>(
                true,
                String.valueOf(reply.getCode()),
                reply.getMessage(),
                data
        );
    }

    /**
     * 构建设备平台失败应答
     *
     * @param reply Alink协议的应答
     * @param <T>   应答类型
     * @return 设备平台应答
     */
    public static <T> ThingOpCb.OpReply<T> failure(AlinkReplyImpl<?> reply) {
        return new ThingOpReplyImpl<>(
                false,
                String.valueOf(reply.getCode()),
                reply.getMessage(),
                null
        );
    }

    /**
     * 构建设备平台成功应答
     *
     * @param reply Alink协议的应答
     * @param <T>   应答类型
     * @return 设备平台应答
     */
    public static <T> ThingOpCb.OpReply<T> empty(AlinkReplyImpl<?> reply) {
        return new ThingOpReplyImpl<>(
                reply.isOk(),
                String.valueOf(reply.getCode()),
                reply.getMessage(),
                null
        );
    }

    @Override
    public boolean isSuccess() {
        return isReplySuccess;
    }

    @Override
    public String getCode() {
        return replyCode;
    }

    @Override
    public String getMessage() {
        return replyMessage;
    }

    @Override
    public E getData() {
        return replyData;
    }
}
