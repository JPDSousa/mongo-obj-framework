package org.smof.parsers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofParam;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.SmofException;
import org.smof.field.ParameterField;
import org.smof.field.PrimaryField;

class TypeBuilder<T> {

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	static <T> TypeBuilder<T> create(Class<T> type) {
		final Constructor<T> constructor = getConstructor(type);
		final Method method;
		if(constructor != null) {
			return new TypeBuilder<>(constructor);
		}
		method = getStaticMethod(type);
		if(method != null) {
			return new TypeBuilder<>(method);
		}
		return null;
	}

	static <T> TypeBuilder<T> create(Class<T> type, Object factory) {
		if(factory == null) {
			return create(type);
		}
		final Method method = getFactoryMethod(type, factory);
		if(method != null && Modifier.isStatic(method.getModifiers())) {
			return new TypeBuilder<>(method);
		}
		else if(method != null) {
			return new TypeBuilder<>(method, factory);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> getConstructor(Class<T> type) {
		Constructor<T> cons = null;
		SmofBuilder annot = null;

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
		SmofBuilder annot = null;

		for(Method method : type.getDeclaredMethods()) {
			annot = method.getAnnotation(SmofBuilder.class);
			if(annot != null) {
				return method;
			}
		}
		return null;
	}

	private final Constructor<T> constructor;
	private final Pair<Object, Method> method;
	private final List<ParameterField> params;

	private TypeBuilder(Constructor<T> constructor) {
		this.method = null;
		this.constructor = constructor;
		this.params = getParamAnnotations(constructor.getParameters());
	}

	private TypeBuilder(Method method, Object instance) {
		this.constructor = null;
		this.method = Pair.of(instance, method);
		this.params = getParamAnnotations(method.getParameters());
	}

	private TypeBuilder(Method method) {
		this.constructor = null;
		this.method = Pair.of(null, method);
		this.params = getParamAnnotations(method.getParameters());
	}

	private List<ParameterField> getParamAnnotations(Parameter[] params) {
		final List<ParameterField> annots = new ArrayList<>();
		SmofParam annot;

		for(Parameter param : params) {
			annot = param.getAnnotation(SmofParam.class);
			checkSmofParam(annot);
			checkNotPrimitive(param.getType());
			annots.add(new ParameterField(param, annot));
		}
		return annots;
	}

	private void checkSmofParam(SmofParam annot) {
		if(annot == null) {
			handleError(new InvalidSmofTypeException("All parameters must have a SmofParam annotation."));
		}
	}

	private void checkNotPrimitive(Class<?> type) {
		if(type.isPrimitive()) {
			handleError(new InvalidSmofTypeException("No primitive types allowed on SmofBuilders."));
		}
	}
	
	List<String> getParamNames() {
		return getParams().stream()
				.map(p -> p.getName())
				.collect(Collectors.toList());
	}
	
	List<ParameterField> getParams() {
		return params;
	}

	@SuppressWarnings("unchecked") 
	T build(Document document) {
		final List<Object> params = new ArrayList<>(this.params.size());
		final T element;

		for(ParameterField param : this.params) {
			params.add(document.get(param.getName(), param.getFieldClass()));
		}
		try {
			if(constructor != null) {
				constructor.setAccessible(true);
				element = constructor.newInstance(params.toArray(new Object[params.size()]));
				constructor.setAccessible(false);
				return element;
			}
			method.getRight().setAccessible(true);
			element = (T) method.getRight().invoke(method.getLeft(), params.toArray(new Object[params.size()]));
			method.getRight().setAccessible(false);
			return element;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	void setTypes(TypeParser<?> parser) {
		final Map<String, PrimaryField> fields = parser.getFieldsAsMap();
		for(ParameterField param : params) {
			final String name = param.getName();
			final PrimaryField field = fields.get(name);
			param.setPrimaryField(field);
			field.setBuilder(true);
		}
	}
}