package org.smof.parsers.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.smof.annnotations.SmofBuilder;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.SmofException;

@SuppressWarnings("javadoc")
public class TypeBuilderFactory {

	private static TypeBuilderFactory singleton;
	
	public static TypeBuilderFactory getDefault() {
		if(singleton == null) {
			singleton = new TypeBuilderFactory();
		}
		return singleton;
	}
	
	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}
	
	private TypeBuilderFactory() {}
	
	public <T> TypeBuilder<T> create(Class<T> type) {
		final Constructor<T> constructor = getConstructor(type);
		final Method method;
		if(constructor != null) {
			return new TypeBuilderImpl<>(type, constructor);
		}
		method = getStaticMethod(type);
		if(method != null) {
			return new TypeBuilderImpl<>(type, method);
		}
		return null;
	}

	public <T> TypeBuilder<T> create(Class<T> type, Object factory) {
		if(factory == null) {
			return create(type);
		}
		final Method method = getFactoryMethod(type, factory);
		if(method != null && Modifier.isStatic(method.getModifiers())) {
			return new TypeBuilderImpl<>(type, method);
		}
		else if(method != null) {
			return new TypeBuilderImpl<>(type, method, factory);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> Constructor<T> getConstructor(Class<T> type) {
		Constructor<T> cons = null;
		SmofBuilder annot;

		for(Constructor<?> constructor : type.getDeclaredConstructors()) {
			annot = constructor.getAnnotation(SmofBuilder.class);
			if(annot != null) {
				cons = (Constructor<T>) constructor;
				break;
			}
		}
		return cons;
	}

	private static Method getFactoryMethod(Class<?> type, Object object) {
		Method method = getBuilderMethod(object.getClass());
		if(method != null && !type.isAssignableFrom(method.getReturnType())) {
			handleError(new InvalidSmofTypeException("Return type from factory method is incompatible with the specified type."));
		}
		return method;
	}

	private static Method getStaticMethod(Class<?> type) {
		Method method = getBuilderMethod(type);
		if(method != null && !type.isAssignableFrom(method.getReturnType())) {
			handleError(new InvalidSmofTypeException("Return type from factory method is incompatible with the specified type."));
		}
		return method;
	}

	private static Method getBuilderMethod(Class<?> type) {
		SmofBuilder annot;

		for(Method method : type.getDeclaredMethods()) {
			annot = method.getAnnotation(SmofBuilder.class);
			if(annot != null) {
				return method;
			}
		}
		return null;
	}
}
