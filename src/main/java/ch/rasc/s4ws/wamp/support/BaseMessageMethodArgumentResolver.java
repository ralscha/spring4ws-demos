package ch.rasc.s4ws.wamp.support;

import org.springframework.core.MethodParameter;

import ch.rasc.s4ws.wamp.message.BaseMessage;

public class BaseMessageMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return BaseMessage.class.isAssignableFrom(paramType);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, BaseMessage message) throws Exception {
		return message;
	}

}