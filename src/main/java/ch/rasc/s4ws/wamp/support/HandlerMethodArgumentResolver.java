package ch.rasc.s4ws.wamp.support;

import org.springframework.core.MethodParameter;

import ch.rasc.s4ws.wamp.message.BaseMessage;

public interface HandlerMethodArgumentResolver {

	boolean supportsParameter(MethodParameter parameter);

	Object resolveArgument(MethodParameter parameter, BaseMessage message) throws Exception;

}