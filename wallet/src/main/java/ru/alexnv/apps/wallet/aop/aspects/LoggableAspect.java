/**
 * 
 */
package ru.alexnv.apps.wallet.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Класс AspectJ для логирования
 */
@Aspect
public class LoggableAspect {
    /**
     * Срез AspectJ
     */
    @Pointcut("within(@ru.alexnv.apps.wallet.aop.annotations.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {

    }

    /**
     * Логирование, вычисляет время выполнения метода.
     * 
     * @param proceedingJoinPoint
     * @return объект
     * @throws Throwable
     */
    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Calling method " + proceedingJoinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println("Execution of method " + proceedingJoinPoint.getSignature() +
                " finished. Execution time is " + (endTime - startTime) + " ms");
        return result;
    }

}
