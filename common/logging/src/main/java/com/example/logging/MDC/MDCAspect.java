package com.example.logging.MDC;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MDCAspect {

    private static final String SERVICE_METHOD_KEY = "serviceMethod";

    @Pointcut("execution(* com.example..*Controller.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.example..*Service.*(..))")
    public void serviceMethods() {}

    @Before("controllerMethods()")
    public void setControllerMDC(JoinPoint joinPoint) {
        MDC.put("controllerMethod", joinPoint.getSignature().toShortString());
    }

    @Before("serviceMethods()")
    public void setServiceMDC(JoinPoint joinPoint) {
        MDC.put(SERVICE_METHOD_KEY, joinPoint.getSignature().toShortString());
    }

    @After("controllerMethods() || serviceMethods()")
    public void clearMDC() {
        MDC.remove("controllerMethod");
        MDC.remove(SERVICE_METHOD_KEY);
    }
}
