package ch.rasc.s4ws.wamp.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;

import ch.rasc.s4ws.wamp.message.BaseMessage;
import ch.rasc.s4ws.wamp.message.CallMessage;
import ch.rasc.s4ws.wamp.message.PublishMessage;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class BaseMessageBodyMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private final static ObjectMapper objectMapper = new ObjectMapper();

	// todo replace this with the already available conversionService
	private final DefaultConversionService defaultConversionService = new DefaultConversionService();

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return true;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, BaseMessage message) throws Exception {

		Object arg = null;

		Class<?> targetClass = parameter.getParameterType();

		if (message instanceof PublishMessage) {
			Object eventObject = ((PublishMessage) message).getEvent();
			return convertParameter(parameter, targetClass, eventObject);
		} else if (message instanceof CallMessage) {
			List<Object> arguments = ((CallMessage) message).getArguments();
			for (Object argument : arguments) {
				return convertParameter(parameter, targetClass, argument);
			}
		}

		return arg;
	}

	private Object convertParameter(MethodParameter parameter, Class<?> targetClass, Object argument) throws Exception {
		TypeDescriptor td = new TypeDescriptor(parameter);

		Class<?> sourceClass = argument.getClass();
		if (targetClass.isAssignableFrom(sourceClass)) {
			return convertListElements(td, argument);
		}

		if (defaultConversionService.canConvert(sourceClass, targetClass)) {
			try {
				return convertListElements(td, defaultConversionService.convert(argument, targetClass));
			} catch (Exception e) {

				TypeFactory typeFactory = objectMapper.getTypeFactory();
				if (td.isCollection()) {
					JavaType type = CollectionType.construct(td.getType(),
							typeFactory.constructType(td.getElementTypeDescriptor().getType()));
					return objectMapper.convertValue(argument, type);
				} else if (td.isArray()) {
					JavaType type = typeFactory.constructArrayType(td.getElementTypeDescriptor().getType());
					return objectMapper.convertValue(argument, type);
				}

				throw e;
			}
		}
		return objectMapper.convertValue(argument, targetClass);
	}

	private static Object convertListElements(TypeDescriptor td, Object convertedValue) {
		if (List.class.isAssignableFrom(convertedValue.getClass())) {
			if (td.isCollection() && td.getElementTypeDescriptor() != null) {
				Class<?> elementType = td.getElementTypeDescriptor().getType();

				Collection<Object> convertedList = new ArrayList<>();
				for (Object record : (List<Object>) convertedValue) {
					Object convertedObject = objectMapper.convertValue(record, elementType);
					convertedList.add(convertedObject);
				}
				return convertedList;
			}
		}
		return convertedValue;
	}

}