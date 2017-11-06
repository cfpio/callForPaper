package io.cfp.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation add values into the SLF4J <a href="http://logback.qos.ch/manual/mdc.html">MDC</a>.
 *
 * It can be set on 2 places:
 * <ul>
 *     <li><b>On a method</b>: it saved the current state of the MDC for the key before entering the annotated method
 *     then replace the existing value when exiting. You can safely use {@link org.slf4j.MDC#put(String, String)} to put
 *     any value inside the method, the cleanup will be done on method exit.</li>
 *     <li><b>On a parameter</b>: it save the current value into the MDC and, like the method use, replace the existing
 *     value on method exit.</li>
 * </ul>
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /** Clé du MDC utilisée dans la méthode */
    String value();
}
