package com.chunchun.chunpicturebackend.common;

import com.chunchun.chunpicturebackend.exception.ErrorCode;

public class ResultUtils {
    /**
     *成功
     * (1) <T>：声明这是一个泛型方法  T 是类型参数。
     * (2)  指定方法的返回值是 BaseResponse 类，且这个类的泛型类型是 T
     * (3)  方法的参数 data 的类型是 T。
     * @param data 数据
     * @return 响应
     * @param <T> 数据类型
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     * ? 等价于 BaseResponse<Object> 但更明确表示“忽略泛型类型”。 错误时不需要 返回业务数据
     * @param errorCode 错误码
     * @return 响应
     */
    public static  BaseResponse<?> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param code 错误码
     * @param message 信息
     * @return 响应
     */
    public static BaseResponse<?> error(int code, String message){
        return new BaseResponse<>(code, null, message);
    }

    /**
     *
     * @param errorCode 错误码
     * @param message 信息
     * @return 响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message){
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
