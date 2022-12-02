package com.ideamart.app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggerAspect {
    @Before(value = "execution(* com.ideamart.app.*.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info(joinPoint.getSignature().toString() + "method execution start");
    }

    @After(value = "execution(* com.ideamart.app.*.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info(joinPoint.getSignature().toString() + "method execution start");
    }

    @AfterThrowing(value = "execution(* com.ideamart.app.*.*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error(joinPoint.getSignature().toString() + " has thrown " + ex.getClass() + " message: " + ex.getMessage());
    }
}
