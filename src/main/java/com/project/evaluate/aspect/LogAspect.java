package com.project.evaluate.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.dao.SyslogDao;
import com.project.evaluate.entity.Syslog;
import com.project.evaluate.util.IPUtil;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.redis.RedisCache;
import io.jsonwebtoken.lang.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/23 01:27
 */
@Component
@Aspect
public class LogAspect {

    @Resource
    private SyslogDao syslogDao;

    @Resource
    private RedisCache redisCache;

    /**
     * select: userID-select-moduleName-single/page-condition:ID....-result:1\2\4\5\6\7
     * <p>
     * update: userID-update-moduleName- ... change_to ...
     * <p>
     * insert: userID-insert-moduleName- ...
     * <p>
     * delete: userID-delete-moduleName- ...
     */


    @Pointcut(value = "@annotation(com.project.evaluate.annotation.DataLog)")
    public void controllerAspect() {
    }

    @Around(value = "controllerAspect() && @annotation(dataLog)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, DataLog dataLog) throws Throwable {
        Syslog syslog = new Syslog();

        Object proceed = proceedingJoinPoint.proceed();

//        参数赋值
        syslog.setModule(dataLog.modelName());
        syslog.setAction(dataLog.operationType());
        syslog.setLogTime(new Date());

//        获取request中的token，并获取其中的userID
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        String userID = null;
        if (Strings.hasText(token)) {
            userID = (String) JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject()).get("userID");
            syslog.setOperator(userID);
        }


//        获取请求参数
        Object[] args = proceedingJoinPoint.getArgs();
        String[] names = ((CodeSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        JSONObject paramJSON = new JSONObject();
        for (int i = 0; i < names.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof MultipartFile) {
                //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                continue;
            }
            paramJSON.put(names[i], args[i]);
        }
//        System.out.println(paramJSON);
//        获取响应参数
        JSONObject resultJSON = JSONObject.parseObject(JSON.toJSONString(proceed));
        syslog.setStatus((Integer) resultJSON.get("status"));
        JSONObject result = new JSONObject();
        JSONObject data = null;
        if (resultJSON.containsKey("data")) {
            data = resultJSON.getJSONObject("data");
        }
//        TODO 根据操作类型不同，取出对应参数
        switch (dataLog.operationType()) {
            case "select":

                break;
            case "update":
                break;
            case "delete":
                break;
            case "insert":

                break;
            case "login":
                paramJSON.remove("password");
                syslog.setOperator((String) paramJSON.get("userID"));
                if (!Objects.isNull(data)) {
                    result.put("msg", data.get("msg"));
                }
                break;
            default:
                break;
        }
        syslog.setConditions(paramJSON.toJSONString());
        syslog.setResult(result.toJSONString());
        syslog.setIP(IPUtil.getIPAddress(request));
        this.syslogDao.insertSyslog(syslog);
        return proceed;
    }
}
