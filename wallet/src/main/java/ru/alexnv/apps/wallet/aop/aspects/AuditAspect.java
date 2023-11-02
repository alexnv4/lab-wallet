/**
 * 
 */
package ru.alexnv.apps.wallet.aop.aspects;

/**
 * Класс AspectJ для аудита
 */
//@Aspect
public class AuditAspect {
//	@Pointcut("within(@ru.alexnv.apps.wallet.aop.annotations.Auditable *) && execution(* *(..))")
//    public void annotatedByAuditable() {
//
//    }

//	@Around("execution( * get* (..)) && target(ru.alexnv.apps.wallet.service.PlayerService.authorize) ")
//    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//		Method m = proceedingJoinPoint.getSignature().getDeclaringType().getMethod("authorize", PlayerService.class);
//		Field f = m.getDeclaringClass().getDeclaredField(m.getName());
//		f.setAccessible(true);
//		//Object result = proceedingJoinPoint.proceed();
//		System.out.println("Field " + f.getName() + " has been accessed! " + "result: " );
//		return null;
//    }
}
