package io.cfp.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Aspect handling {@link Log} annotation, see it's javadoc for using.
 */
@Aspect
@Component
public class MDCAspect {

    @Around("@annotation(Log) || execution(* *(.., @Log (*), ..))")
    private Object executeMethod(ProceedingJoinPoint pjp) throws Throwable {
        List<OldMDC> toRemove = new LinkedList<>();

        //handling annotation on method
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Log methodLog = method.getAnnotation(Log.class);

        if (methodLog != null) {
            addToMDC(toRemove, methodLog, null, false);
        }

		//handling annotation on parameters
        Annotation[][] annotations = method.getParameterAnnotations();
        Object[] args = pjp.getArgs();
        int paramI = 0;
        for (Annotation[] annotation : annotations) {
            for (Annotation ann : annotation) {
                if (ann instanceof Log) {
                    Log log = (Log) ann;
                    addToMDC(toRemove, log, args[paramI], true);
                }
            }
            paramI++;
        }

        try {
            return pjp.proceed();
        } finally {

            //let's cleanup
            for (OldMDC oldMDC : toRemove) {
                if (oldMDC.value == null) {
                    MDC.remove(oldMDC.key);
                } else {
                    MDC.put(oldMDC.key, oldMDC.value);
                }
            }
        }
    }

    /**
     * Add a value into the MDC and saving the current one
     * @param toRemove Backup list of the current MDC value in order to be reset after method call
     * @param log Parameter or method annotation
     * @param newValue New value to save into the MDC
     * @param forceNull if newValue is null, true will delete current MDC value, false will keep the current value
     */
    private void addToMDC(List<OldMDC> toRemove, Log log, Object newValue, boolean forceNull) {
        String key = log.value();
        String oldValue = MDC.get(key);

        //saving current values for cleanup
        if (oldValue == null) {
            //no current value, we'll remove the key from MDC on exit
            toRemove.add(new OldMDC(key));

        } else if (!oldValue.equals(newValue)) {
            //current value exists, we'll reset it on exit
            toRemove.add(new OldMDC(key, oldValue));
        }
        //oldValue == newValue here so no cleanup on exit is required


        //adding value into the MDC
        if (newValue != null && !newValue.equals(oldValue)) {
            MDC.put(key, newValue.toString());

        } else if (newValue == null && forceNull) {
            MDC.remove(key);
        }
    }

    /**
	 * Object saving current MDC value before going into method
     */
    private static class OldMDC {
        private String key;
        private String value;

        OldMDC(String key) {
            this.key = key;
        }

        OldMDC(String key, String value) {
            this.key = key;
            this.value = value;
        }
	}
}
