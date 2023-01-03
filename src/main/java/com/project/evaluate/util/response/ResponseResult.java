package com.project.evaluate.util.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 02:00
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {

    //    状态码
    private int code;

    //    状态信息
    private String msg;

    //    状态标志
    private int status;
    //    枚举类
    private ResultCode resultCode;

    //    返回的数据对象data：使用泛型
    private T data;

    // 手动设置返回vo
    public ResponseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.status = resultCode.getStatus();
    }

    // 默认返回成功状态码，数据对象
    public ResponseResult(T data) {
        this.code = ResultCode.SUCCESS.getCode();
        this.msg = ResultCode.SUCCESS.getMsg();
        this.data = data;
    }

    // 返回指定状态码，数据对象
    public ResponseResult(StatusCode statusCode, T data) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
        this.data = data;
    }

    // 只返回状态码
    public ResponseResult(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
        this.data = null;
    }

    //    返回枚举类
    public ResponseResult(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.status = resultCode.getStatus();
        this.data = data;
    }

    //    错误处理
/*    public static <T> ResponseResult error(AppException appException) {
        ResponseResult responseResult = new ResponseResult(appException.getCode(), appException.getMsg(), null);
        return responseResult;
    }*/

    public static <T> ResponseResult error(int code, String msg) {
        ResponseResult responseResult = new ResponseResult(code, msg, null);
        return responseResult;
    }

    public static <T> ResponseResult<T> success() {
        ResponseResult responseResult = new ResponseResult(ResultCode.SUCCESS);
        return responseResult;
    }
}
