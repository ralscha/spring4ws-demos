package ch.rasc.s4ws.wamp.handler;

import static reactor.event.selector.Selectors.$;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.handler.HandlerMethodSelector;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils.MethodFilter;

import reactor.core.Reactor;
import reactor.event.Event;
import reactor.function.Consumer;
import ch.rasc.s4ws.wamp.annotation.WampCallListener;
import ch.rasc.s4ws.wamp.annotation.WampPublishListener;
import ch.rasc.s4ws.wamp.annotation.WampSubscribeListener;
import ch.rasc.s4ws.wamp.annotation.WampUnsubscribeListener;
import ch.rasc.s4ws.wamp.message.BaseMessage;
import ch.rasc.s4ws.wamp.message.CallErrorMessage;
import ch.rasc.s4ws.wamp.message.CallMessage;
import ch.rasc.s4ws.wamp.message.CallResultMessage;
import ch.rasc.s4ws.wamp.message.PublishMessage;
import ch.rasc.s4ws.wamp.message.SubscribeMessage;
import ch.rasc.s4ws.wamp.message.UnsubscribeMessage;
import ch.rasc.s4ws.wamp.message.WampMessageType;
import ch.rasc.s4ws.wamp.support.BaseMessageBodyMethodArgumentResolver;
import ch.rasc.s4ws.wamp.support.BaseMessageMethodArgumentResolver;
import ch.rasc.s4ws.wamp.support.HandlerMethodArgumentResolver;
import ch.rasc.s4ws.wamp.support.HandlerMethodArgumentResolverComposite;
import ch.rasc.s4ws.wamp.support.InvocableHandlerMethod;

public class AnnotationMethodHandler implements ApplicationContextAware, InitializingBean {

	private static final Log logger = LogFactory.getLog(AnnotationMethodHandler.class);

	// private MessageConverter<?> messageConverter;

	private ApplicationContext applicationContext;

	private final MultiValueMap<String, HandlerMethod> publishMethods = new LinkedMultiValueMap<>();

	private final MultiValueMap<String, HandlerMethod> subscribeMethods = new LinkedMultiValueMap<>();

	private final MultiValueMap<String, HandlerMethod> unsubscribeMethods = new LinkedMultiValueMap<>();

	private final MultiValueMap<String, HandlerMethod> callMethods = new LinkedMultiValueMap<>();

	// private final Map<Class<?>, ExceptionHandlerMethodResolver>
	// exceptionHandlerCache = new ConcurrentHashMap<>(64);

	private List<HandlerMethodArgumentResolver> customArgumentResolvers = new ArrayList<>();

	// private List<HandlerMethodReturnValueHandler> customReturnValueHandlers =
	// new ArrayList<>();

	private final HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();

	// private final HandlerMethodReturnValueHandlerComposite
	// returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();

	private final Reactor reactor;

	public AnnotationMethodHandler(Reactor reactor) {
		this.reactor = reactor;
	}

	// public void setMessageConverter(MessageConverter<?> converter) {
	// this.messageConverter = converter;
	// }

	public void setCustomArgumentResolvers(List<HandlerMethodArgumentResolver> customArgumentResolvers) {
		Assert.notNull(customArgumentResolvers, "The 'customArgumentResolvers' cannot be null.");
		this.customArgumentResolvers = customArgumentResolvers;
	}

	// public void
	// setCustomReturnValueHandlers(List<HandlerMethodReturnValueHandler>
	// customReturnValueHandlers) {
	// Assert.notNull(customReturnValueHandlers,
	// "The 'customReturnValueHandlers' cannot be null.");
	// this.customReturnValueHandlers = customReturnValueHandlers;
	// }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() {

		initHandlerMethods();

		this.argumentResolvers.addResolver(new BaseMessageMethodArgumentResolver());
		this.argumentResolvers.addResolvers(this.customArgumentResolvers);
		this.argumentResolvers.addResolver(new BaseMessageBodyMethodArgumentResolver());

		// // Annotation-based return value types
		// this.returnValueHandlers.addHandler(new
		// SendToMethodReturnValueHandler(this.brokerTemplate, true));
		// this.returnValueHandlers.addHandler(new
		// SubscriptionMethodReturnValueHandler(this.webSocketResponseTemplate));
		//
		// // custom return value types
		// this.returnValueHandlers.addHandlers(this.customReturnValueHandlers);
		//
		// // catch-all
		// this.returnValueHandlers.addHandler(new
		// SendToMethodReturnValueHandler(this.brokerTemplate, false));

		this.reactor.on($(WampMessageType.SUBSCRIBE), new Consumer<Event<SubscribeMessage>>() {
			@Override
			public void accept(Event<SubscribeMessage> event) {
				handlePubSubMessage(event.getData(), event.getData().getTopicURI(), subscribeMethods);
			}
		});

		this.reactor.on($(WampMessageType.UNSUBSCRIBE), new Consumer<Event<UnsubscribeMessage>>() {
			@Override
			public void accept(Event<UnsubscribeMessage> event) {
				handlePubSubMessage(event.getData(), event.getData().getTopicURI(), unsubscribeMethods);
			}
		});

		this.reactor.on($(WampMessageType.PUBLISH), new Consumer<Event<PublishMessage>>() {
			@Override
			public void accept(Event<PublishMessage> event) {
				handlePubSubMessage(event.getData(), event.getData().getTopicURI(), publishMethods);
			}
		});

		this.reactor.on($(WampMessageType.CALL), new Consumer<Event<CallMessage>>() {
			@Override
			public void accept(Event<CallMessage> event) {
				handleCallMessage(event.getData());
			}
		});
	}

	protected final void initHandlerMethods() {
		String[] beanNames = this.applicationContext.getBeanNamesForType(Object.class);
		for (String beanName : beanNames) {
			detectHandlerMethods(beanName);
		}
	}

	protected final void detectHandlerMethods(Object handler) {

		Class<?> handlerType = (handler instanceof String) ? this.applicationContext.getType((String) handler)
				: handler.getClass();

		handlerType = ClassUtils.getUserClass(handlerType);

		initHandlerMethods(handler, handlerType, WampCallListener.class, this.callMethods);
		initHandlerMethods(handler, handlerType, WampPublishListener.class, this.publishMethods);
		initHandlerMethods(handler, handlerType, WampSubscribeListener.class, this.subscribeMethods);
		initHandlerMethods(handler, handlerType, WampUnsubscribeListener.class, this.unsubscribeMethods);
	}

	private <A extends Annotation> void initHandlerMethods(Object handler, Class<?> handlerType,
			final Class<A> annotationType, MultiValueMap<String, HandlerMethod> handlerMethods) {

		Set<Method> methods = HandlerMethodSelector.selectMethods(handlerType, new MethodFilter() {
			@Override
			public boolean matches(Method method) {
				return AnnotationUtils.findAnnotation(method, annotationType) != null;
			}
		});

		for (Method method : methods) {
			A annotation = AnnotationUtils.findAnnotation(method, annotationType);
			String[] destinations = (String[]) AnnotationUtils.getValue(annotation);
			HandlerMethod newHandlerMethod = createHandlerMethod(handler, method);
			for (String destination : destinations) {
				handlerMethods.add(destination, newHandlerMethod);
				if (logger.isInfoEnabled()) {
					logger.info("Mapped \"@" + annotationType.getSimpleName() + " " + destination + "\" onto "
							+ newHandlerMethod);
				}
			}
		}
	}

	private HandlerMethod createHandlerMethod(Object handler, Method method) {
		HandlerMethod handlerMethod;
		if (handler instanceof String) {
			String beanName = (String) handler;
			handlerMethod = new HandlerMethod(beanName, this.applicationContext, method);
		} else {
			handlerMethod = new HandlerMethod(handler, method);
		}
		return handlerMethod;
	}

	private void handleCallMessage(CallMessage callMessage) {
		List<HandlerMethod> matches = getHandlerMethod(callMessage.getProcURI(), callMethods);
		if (matches == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("No matching method, destination " + callMessage.getProcURI());
			}
			return;
		}

		for (HandlerMethod match : matches) {
			HandlerMethod handlerMethod = match.createWithResolvedBean();

			InvocableHandlerMethod invocableHandlerMethod = new InvocableHandlerMethod(handlerMethod);
			invocableHandlerMethod.setMessageMethodArgumentResolvers(this.argumentResolvers);

			try {
				Object returnValue = invocableHandlerMethod.invoke(callMessage);
				CallResultMessage callResultMessage = new CallResultMessage(callMessage, returnValue);
				reactor.notify(WampMessageType.INTERNAL_OUTBOUND, Event.wrap(callResultMessage));
			} catch (Exception ex) {
				CallErrorMessage callErrorMessage = new CallErrorMessage(callMessage, "", ex.getMessage());
				reactor.notify(WampMessageType.INTERNAL_OUTBOUND, Event.wrap(callErrorMessage));
				logger.error("Error while processing message " + callMessage, ex);
				// invokeExceptionHandler(message, handlerMethod, ex);
			} catch (Throwable ex) {
				CallErrorMessage callErrorMessage = new CallErrorMessage(callMessage, "", ex.getMessage());
				reactor.notify(WampMessageType.INTERNAL_OUTBOUND, Event.wrap(callErrorMessage));
				logger.error("Error while processing message " + callErrorMessage, ex);
			}
		}
	}

	private void handlePubSubMessage(BaseMessage message, String destination,
			MultiValueMap<String, HandlerMethod> handlerMethods) {
		Assert.notNull(destination, "destination is required");

		List<HandlerMethod> matches = getHandlerMethod(destination, handlerMethods);
		if (matches == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("No matching method, destination " + destination);
			}
			return;
		}

		for (HandlerMethod match : matches) {
			HandlerMethod handlerMethod = match.createWithResolvedBean();

			InvocableHandlerMethod invocableHandlerMethod = new InvocableHandlerMethod(handlerMethod);
			invocableHandlerMethod.setMessageMethodArgumentResolvers(this.argumentResolvers);

			try {
				invocableHandlerMethod.invoke(message);
			} catch (Exception ex) {
				logger.error("Error while processing message " + message, ex);
				// invokeExceptionHandler(message, handlerMethod, ex);
			} catch (Throwable ex) {
				logger.error("Error while processing message " + message, ex);
			}
		}
	}

	// private void invokeExceptionHandler(Message<?> message, HandlerMethod
	// handlerMethod, Exception ex) {
	//
	// InvocableHandlerMethod exceptionHandlerMethod;
	// Class<?> beanType = handlerMethod.getBeanType();
	// ExceptionHandlerMethodResolver resolver =
	// this.exceptionHandlerCache.get(beanType);
	// if (resolver == null) {
	// resolver = new ExceptionHandlerMethodResolver(beanType);
	// this.exceptionHandlerCache.put(beanType, resolver);
	// }
	//
	// Method method = resolver.resolveMethod(ex);
	// if (method == null) {
	// logger.error("Unhandled exception", ex);
	// return;
	// }
	//
	// exceptionHandlerMethod = new
	// InvocableHandlerMethod(handlerMethod.getBean(), method);
	// exceptionHandlerMethod.setMessageMethodArgumentResolvers(this.argumentResolvers);
	//
	// try {
	// Object returnValue = exceptionHandlerMethod.invoke(message, ex);
	//
	// MethodParameter returnType = exceptionHandlerMethod.getReturnType();
	// if (void.class.equals(returnType.getParameterType())) {
	// return;
	// }
	// this.returnValueHandlers.handleReturnValue(returnValue, returnType,
	// message);
	// } catch (Throwable t) {
	// logger.error("Error while handling exception", t);
	// return;
	// }
	// }

	protected List<HandlerMethod> getHandlerMethod(String destination,
			MultiValueMap<String, HandlerMethod> handlerMethods) {
		for (String mappingDestination : handlerMethods.keySet()) {
			if (destination.equals(mappingDestination)) {
				return handlerMethods.get(mappingDestination);
			}
		}
		return null;
	}

}
