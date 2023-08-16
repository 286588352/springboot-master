package com.liyh.log;

import com.liyh.entity.ExceptionLog;
import com.liyh.entity.SqlLog;
import com.liyh.entity.UserLog;
import com.liyh.service.LogService;
import com.liyh.utils.SqlUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 控制器切面
 * @Author: liyh
 * @Date: 2020/9/17 14:08
 */

@Aspect
@Component
public class LogAspectj {
    @Autowired
    private LogService logService;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    private static Logger logger = LoggerFactory.getLogger(LogAspectj.class);

    /**
     * @Pointcut : 创建一个切点，方便同一方法的复用。
     * value属性就是AspectJ表达式，
     */
    @Pointcut("execution(* com.liyh.controller.*.*(..))")
    //@Pointcut("@annotation(com.liyh.log.LogAnno)")
    public void userLog() {
    }

    @Pointcut("execution(* com.liyh.mapper.*.*(..))")
    public void sqlLog() {
    }

    @Pointcut("execution(* com.liyh.controller.*.*(..))")
    public void exceptionLog() {
    }

    //前置通知
    //指定该方法是前置通知，并指定切入点
    @Before("userLog()")
    public void userLog(JoinPoint pj) {
        try {
            UserLog userLog = new UserLog();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String method = request.getMethod();
            Signature signature = pj.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method targetMethod = methodSignature.getMethod();
            if ("POST".equals(method) || "GET".equals(method)) {
                String ipAddress = getIpAddress(request);
                String requestId = (String) request.getAttribute("requestId");
                // 根据请求参数或请求头判断是否有“requestId”，有则使用，无则创建
                if (StringUtils.isEmpty(requestId)) {
                    requestId = "req_" +  System.currentTimeMillis();
                    request.setAttribute("requestId", requestId);
                }
                userLog.setRequestId(requestId);    //请求id
                userLog.setMethodName(targetMethod.getName());        //方法名
                userLog.setMethodClass(signature.getDeclaringTypeName()); //方法所在的类名
                userLog.setRequestUrl(request.getRequestURL().toString());//请求URI
                userLog.setRemoteIp(ipAddress); //操作IP地址
                System.out.println("userLog = " + userLog);
//                logService.saveUserLog(userLog);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    //环绕通知
    @Around("sqlLog()")
    public Object sqlLog(ProceedingJoinPoint pj) throws Throwable {
        // 发送异步日志事件
        long start = System.currentTimeMillis();
        SqlLog sqlLog = new SqlLog();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Signature signature = pj.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        String ipAddress = getIpAddress(request);
        String requestId = (String) request.getAttribute("requestId");
        // 根据请求参数或请求头判断是否有“requestId”，有则使用，无则创建
        if (StringUtils.isEmpty(requestId)) {
            requestId = "req_" + System.currentTimeMillis();
            request.setAttribute("requestId", requestId);
        }
        //执行方法
        Object object = pj.proceed();
        //获取sql
        String sql = SqlUtils.getMybatisSql(pj, sqlSessionFactory);
        //执行时长(毫秒)
        long loadTime = System.currentTimeMillis() - start;
        sqlLog.setRequestId(requestId);    //请求id
        sqlLog.setMethodName(targetMethod.getName());        //方法名
        sqlLog.setMethodClass(signature.getDeclaringTypeName()); //方法所在的类名
        sqlLog.setRequestUrl(request.getRequestURL().toString());//请求URI
        sqlLog.setRemoteIp(ipAddress); //操作IP地址
        sqlLog.setSql(sql);//sql
        sqlLog.setLoadTime(loadTime);//执行时间
        System.out.println("sqlLog = " + sqlLog);
//        logService.saveSqlLog(sqlLog);
        return object;
    }

    //异常通知 用于拦截异常日志
    @AfterThrowing(pointcut = "exceptionLog()", throwing = "e")
    public void exceptionLog(JoinPoint pj, Throwable e) {
        try {
            ExceptionLog exceptionLog = new ExceptionLog();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Signature signature = pj.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method targetMethod = methodSignature.getMethod();
            String ipAddress = getIpAddress(request);
            String requestId = (String) request.getAttribute("requestId");
            // 根据请求参数或请求头判断是否有“requestId”，有则使用，无则创建
            if (StringUtils.isEmpty(requestId)) {
                requestId ="req_" + System.currentTimeMillis();
                request.setAttribute("requestId", requestId);
            }
            exceptionLog.setRequestId(requestId);    //请求id
            exceptionLog.setMethodName(targetMethod.getName());        //方法名
            exceptionLog.setMethodClass(signature.getDeclaringTypeName()); //方法所在的类名
            exceptionLog.setRequestUrl(request.getRequestURL().toString());//请求URI
            exceptionLog.setMessage(e.getMessage()); //异常信息
            exceptionLog.setRemoteIp(ipAddress); //操作IP地址
            System.out.println("exceptionLog = " + exceptionLog);
//            logService.saveExceptionLog(exceptionLog);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 获取IP地址的方法
     * @param request 传一个request对象下来
     * @return
     */
    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
