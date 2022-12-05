package com.project.evaluate.util.response;

import lombok.Getter;

@Getter
public enum ResultCode {
    //    状态码如下：
//    1000   接口调用成功
//    1001   参数无效
//    1002   参数缺失
//    1003   访问过于频繁
//    1004   接口调用次数已达到设定的上限
//    2001   用户未登录，无权限 / 当前令牌已过期
//    2002   登录超时
//    2003   IP已被禁止访问
//    3001   服务器错误
//    3002   数据库访问异常
//    3003   IO操作异常
//    3004   系统异常
//    3005   请求超时
    SUCCESS(1000, "接口调用成功", 1),
    INVALID_PARAMETER(1001, "参数无效", 0),
    MISSING_PATAMETER(1002, "参数缺失", 0),
    FREQUENT_VISITS(1003, "访问频繁", 0),
    NUMBER_INTERFACE_CALLS_OVER(1004, "接口调用次数已达到设定的上限", 0),
    TOKEN_EXPIRATION(2001, "用户未登录、无权限或当前令牌过期", 0),
    LOGIN_TIMEOUT(2002, "登录超时", 0),
    LOGIN_ERROR(2003, "账号或密码错误", 0),

    IP_BLOCKED(2003, "IP被禁止访问", 0),

    SERVER_ERROR(3001, "服务器错误", 0),

    DATABASE_ERROR(3002, "数据库错误", 0),

    IO_OPERATION_ERROR(3003, "IO操作错误", 0),

    SYSTEM_ERROR(3004, "系统错误", 0),

    REQUEST_TIMEOUT(3005, "请求超时", 0);

    //    构造函数及其变量
    private final int code;
    private final String msg;

    private final int status;

    ResultCode(int code, String msg, int status) {
        this.code = code;
        this.msg = msg;
        this.status = status;
    }
}
