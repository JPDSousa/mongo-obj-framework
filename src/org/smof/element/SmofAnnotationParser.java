package org.smof.element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofField;
import org.smof.annnotations.SmofParam;
import org.smof.exception.InvalidSmofTypeException;

@SuppressWarnings("javadoc")
public class SmofAnnotationParser<T> {

	@SuppressWarnings("unchecked")
	private static <T> Builder<T> getConstructor(Class<T> type) throws InvalidSmofTypeException {
		Constructor<T> cons = null;
		SmofBuilder annot = null;

		for(Constructor<?> constructor : type.getDeclaredConstructors()) {
			annot = constructor.getAnnotation(SmofBuilder.class);
			if(annot != null) {
				cons = (Constructor<T>) constructor;
				break;
			}
		}
		return new Builder<>(cons, null, annot);
	}

	private static <T> Builder<T> getFactoryMethod(Class<T> type, Object object) throws InvalidSmofTypeException {
		Method meth = null;
		SmofBuilder annot = null;

		for(Method method : object.getClass().getMethods()) {
			annot = method.getAnnotation(SmofBuilder.class);
			if(annot != null) {
				meth = method;
				break;
			}
		}
		if(meth != null && !meth.getReturnType().equals(type)) {
			throw new InvalidSmofTypeException("Return type from factory method is incompatible with the specified type.");
		}
		return new Builder<T>(null, Pair.of(object, meth), annot);
	}

	private final Class<T> type;

	private final Map<String, SmofField> fields;
	private final Builder<T> builder;

	SmofAnnotationParser(Class<T> type) throws InvalidSmofTypeException {
		this(type, getConstructor(type));
	}

	SmofAnnotationParser(Class<T> type, Object factory) throws InvalidSmofTypeException {
		this(type, getFactoryMethod(type, factory));
	}

	private SmofAnnotationParser(Class<T> type, Builder<T> builder) throws InvalidSmofTypeException {
		final List<String> constrFields;
		this.type = type;
		this.builder = builder;
		this.fields = new LinkedHashMap<>();
		constrFields = builder.paramAnnots.stream()
				.map(p -> p.getLeft().name())
				.collect(Collectors.toList());
		fillFields(constrFields);
	}

	public Class<T> getType() {
		return type;
	}

	private void fillFields(List<String> fields) throws InvalidSmofTypeException {
		SmofField current;
		for(Field field : type.getDeclaredFields()) {
			for(SmofField.FieldType fieldType : SmofField.FieldType.values()) {
				if(field.isAnnotationPresent(fieldType.getAnnotClass())) {
					current = new SmofField(field, fieldType, fields);
					this.fields.put(current.getName(), current);
					break;
				}
			}
		}
	}

	public Collection<SmofField> getAllFields() {
		return fields.values();
	}
	
	public Set<SmofField> getNonBuilderFields() {
		return getAllFields().stream()
				.filter(f -> !f.isBuilderField())
				.collect(Collectors.toSet());
	}

	public Set<SmofField> getExternalFields() {
		return getAllFields().stream()
				.filter(f -> f.isExternal())
				.collect(Collectors.toSet());
	}

	public T createSmofObject(Document document) {
		return builder.build(document);
	}

	private static class Builder<T> {
		private final Constructor<T> constructor;
		private final Pair<Object, Method> method;
		private final SmofBuilder annot;
		private final List<Pair<SmofParam, Parameter>> paramAnnots;

		@SuppressWarnings("null")
		private Builder(Constructor<T> constructor, Pair<Object, Method> method, SmofBuilder annot) throws InvalidSmofTypeException {
			if(!(constructor == null ^ method == null)) {
				throw new InvalidSmofTypeException("Either specify a factory method or a constructor.");
			}
			this.method = method;
			this.constructor = constructor;
			this.annot = annot;
			if (constructor != null) {
				this.paramAnnots = getParamAnnotations(constructor.getParameters());
			}
			else {
				this.paramAnnots = getParamAnnotations(method.getRight().getParameters());
			}
		}

		private static List<Pair<SmofParam, Parameter>> getParamAnnotations(Parameter[] params) throws InvalidSmofTypeException {
			final List<Pair<SmofParam, Parameter>> annots = new ArrayList<>();
			SmofParam annot;

			for(Parameter param : params) {
				annot = param.getAnnotation(SmofParam.class);
				if(annot != null) {
					if(!param.getType().isPrimitive()) {
						annots.add(Pair.of(annot, param));	
					}
					else {
						throw new InvalidSmofTypeException("No primitive types allowed on SmofBuilders.");
					}
				}
				else {
					throw new InvalidSmofTypeException("All parameters must have a SmofParam annotation.");
				}
			}
			return annots;
		}

		@SuppressWarnings("unchecked")
		private T build(Document document) {
			final List<Object> params = new ArrayList<>(paramAnnots.size());
			final T element;

			for(Pair<SmofParam, Parameter> param : paramAnnots) {
				params.add(document.get(param.getLeft().name(), param.getRight().getType()));
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
	}
}
