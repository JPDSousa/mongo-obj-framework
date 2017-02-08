package org.smof.element;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("javadoc")
public class ElementTypeFactory implements TypeAdapterFactory {

	private static ElementTypeFactory singleton;

	public static ElementTypeFactory getDefault() {
		if(singleton == null) {
			singleton = new ElementTypeFactory();
		}

		return singleton;
	}

	private ElementTypeFactory() {}

	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Gson arg0, TypeToken<T> arg1) {
		if(!Element.class.isAssignableFrom(arg1.getRawType())) {
			return null;
		}
		return (TypeAdapter<T>) new ElementAdapter();
	}

}
