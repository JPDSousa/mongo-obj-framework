package org.smof.parsers;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.smof.collection.SmofDispatcher;
import org.smof.element.AbstractElement;
import org.smof.element.Element;
import org.smof.exception.SmofException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

@SuppressWarnings("javadoc")
public class LazyLoader {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	static LazyLoader create(SmofDispatcher dispatcher) {
		return new LazyLoader(dispatcher); 
	}

	private final Map<Class<?>, Class<?>> lazyTypes;
	private final SmofDispatcher dispatcher;
	private final ByteBuddy byteBuddy;

	private LazyLoader(SmofDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		this.lazyTypes = new LinkedHashMap<>();
		this.byteBuddy = new ByteBuddy();
	}

	public <T extends Element> T createLazyInstance(Class<T> type, ObjectId id) {
		final Class<? extends T> lazyType = getLazyType(type);
		return createInstance(lazyType, id);
	}

	private <T> T createInstance(final Class<T> lazyType, ObjectId id){
		try {
			return lazyType.getConstructor(SmofDispatcher.class).newInstance(dispatcher, id);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			handleError(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Element> Class<? extends T> getLazyType(Class<T> type) {
		if(lazyTypes.containsKey(type)) {
			return (Class<? extends T>) lazyTypes.get(type);
		}
		return createLazyType(type);
	}

	private <T extends Element> Class<? extends T> createLazyType(Class<T> type) {
		if(type.isInterface()) {
			return createLazyClass(type);
		}
		return createLazySubClass(type);
	}

	@SuppressWarnings("unchecked")
	private <T extends Element> Class<? extends T> createLazyClass(Class<T> type) {
		Class<? extends Element> lazyType = byteBuddy
				.subclass(AbstractElement.class)
				.implement(type)
				.defineField("delegate", type, Visibility.PRIVATE)
				.defineField("dispatcher", SmofDispatcher.class, Visibility.PRIVATE)
				.defineField("loaded", Boolean.class, Visibility.PRIVATE)
				.defineConstructor(Visibility.PUBLIC)
				.withParameter(SmofDispatcher.class)
				.withParameter(ObjectId.class)
				.intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(ConstructorInterceptor.class)))
				.method(ElementMatchers.any())
				.intercept(createLazyHandler())
				.make()
				.load(type.getClassLoader())
				.getLoaded();
		lazyTypes.put(type, lazyType);
		return (Class<? extends T>) lazyType;
	}

	private <T extends Element> Class<? extends T> createLazySubClass(Class<T> type) {
		Class<? extends T> lazyType = byteBuddy
				.subclass(type)
				.defineField("delegate", type, Visibility.PRIVATE)
				.defineField("dispatcher", SmofDispatcher.class, Visibility.PRIVATE)
				.defineField("loaded", Boolean.class, Visibility.PRIVATE)
				.defineConstructor(Visibility.PUBLIC)
				.withParameter(SmofDispatcher.class)
				.withParameter(ObjectId.class)
				.intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(ConstructorInterceptor.class)))
				.method(ElementMatchers.any())
				.intercept(createLazyHandler())
				.make()
				.load(type.getClassLoader())
				.getLoaded();
		lazyTypes.put(type, lazyType);
		return lazyType;
	}

	private Implementation createLazyHandler() {
		return MethodDelegation.to(LazyHandler.class).andThen(MethodCall.invokeSelf().onField("delegate"));
	}

	private static class ConstructorInterceptor {
		@RuntimeType
		private static void intercept(@This Element self, 
				@AllArguments Object[] args, 
				@FieldProxy("dispatcher") FieldSetter accessor) {
			accessor.setValue(args[0]);
			self.setId((ObjectId) args[1]);
		}
	}

	private interface FieldSetter {
		void setValue(Object value);
	}

	private interface FieldGetter {
		Object getValue();
	}

	private static class LazyHandler {
		@SuppressWarnings({ "unused", "unchecked" })
		private static void intercept(@This Object instance, 
				@FieldProxy("delegate") FieldSetter delegate,
				@FieldProxy("loaded") FieldGetter loaded,
				@FieldProxy("dispatcher") FieldGetter dispatcher) {
			if(!((Boolean)loaded.getValue())) {
				final ObjectId id = ((Element) instance).getId();
				delegate.setValue(((SmofDispatcher)dispatcher).findById(id, (Class<Element>) instance.getClass()));
			}
		}
	}
}
