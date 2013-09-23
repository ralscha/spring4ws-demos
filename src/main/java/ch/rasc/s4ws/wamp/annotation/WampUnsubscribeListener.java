package ch.rasc.s4ws.wamp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WampUnsubscribeListener {

	/**
	 * TopicURI(s) for the subscription.
	 */
	String[] value() default {};

}
