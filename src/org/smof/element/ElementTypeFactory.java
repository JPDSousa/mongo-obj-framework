package org.smof.element;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("javadoc")
public class ElementTypeFactory implements TypeAdapterFactory {

	private static ElementTypeFactory singleton;

	public static ElementTypeFactory getDefault() {
		return singleton;
	}
	
	public static void init(ElementFactoryPool factories) {
		if(singleton == null) {
			singleton = new ElementTypeFactory(factories);
		}
	}
	
	private final ElementFactoryPool factories;

	private ElementTypeFactory(ElementFactoryPool factories) {
		this.factories = factories;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Gson arg0, TypeToken<T> arg1) {
		if(!Element.class.isAssignableFrom(arg1.getRawType())) {
			return null;
		}
		return (TypeAdapter<T>) createAdapter(arg1.getRawType());
	}
	
	@SuppressWarnings("unchecked")
	private ElementAdapter<? extends Element> createAdapter(Class<?> rawType) {
		final Class<? extends Element> type = ((Class<? extends Element>) rawType);
		return new ElementAdapter<>(factories.get(type)); 
	}

}
