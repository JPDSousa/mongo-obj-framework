/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.smof.parsers.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.smof.annnotations.SmofParam;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.SmofException;
import org.smof.field.ParameterField;
import org.smof.field.PrimaryField;

class TypeBuilderImpl<T> implements TypeBuilder<T> {

	private static final long serialVersionUID = 1L;

	private static void handleError(Throwable cause) {
		throw new SmofException(cause);
	}

	private final Class<T> type;
	private final Constructor<T> constructor;
	private final Pair<Object, Method> method;
	private final List<ParameterField> params;

	TypeBuilderImpl(Class<T> type, Constructor<T> constructor) {
		this.type = type;
		this.method = null;
		this.constructor = constructor;
		this.params = getParamAnnotations(constructor.getParameters());
	}

	TypeBuilderImpl(Class<T> type, Method method, Object instance) {
		this.type = type;
		this.constructor = null;
		this.method = Pair.of(instance, method);
		this.params = getParamAnnotations(method.getParameters());
	}

	TypeBuilderImpl(Class<T> type, Method method) {
		this.type = type;
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
			checkNotPrimitiveOrArray(param.getType());
			annots.add(new ParameterField(param, annot));
		}
		return annots;
	}

	private void checkSmofParam(SmofParam annot) {
		if(annot == null) {
			handleError(new InvalidSmofTypeException("All parameters must have a SmofParam annotation: " + type.getName()));
		}
	}

	private void checkNotPrimitiveOrArray(Class<?> type) {
		if(type.isPrimitive() || type.isArray()) {
			handleError(new InvalidSmofTypeException("No primitive types allowed on SmofBuilders."));
		}
	}

	@Override
	public List<ParameterField> getParams() {
		return params;
	}

	@Override
	@SuppressWarnings("unchecked") 
	public T build(Document document) {
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
			handleError(e);
			return null;
		}
	}

	@Override
	public void setTypes(TypeParser<?> parser) {
		final Map<String, PrimaryField> fields = parser.getFieldsAsMap();
		for(ParameterField param : params) {
			handleField(fields, param);
		}
	}

	private void handleField(final Map<String, PrimaryField> fields, ParameterField param) {
		final String name = param.getName();
		if(fields.containsKey(name)) {
			final PrimaryField field = fields.get(name);
			param.setPrimaryField(field);
			field.setBuilder(true);
		}
	}
}
