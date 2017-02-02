package org.smof.collection;

import org.smof.element.Element;
import org.smof.element.ElementTypeFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;

@SuppressWarnings("javadoc")
public class Smof {
	
	private final MongoDatabase database;
	private final Gson gson;
	
	private Smof(MongoDatabase database) {
		final GsonBuilder jsonBuilder = new GsonBuilder();
		this.database = database;
		jsonBuilder.registerTypeAdapterFactory(ElementTypeFactory.getDefault());
		gson = jsonBuilder.create();
	}
	
	public <T extends Element> SmofCollection<T> getCollection(String collectionName, Class<T> elClass) {
		return new SmofCollectionImpl<T>(null, database.getCollection(collectionName), elClass);
	}

}
