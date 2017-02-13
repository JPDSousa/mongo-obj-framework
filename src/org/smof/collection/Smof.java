package org.smof.collection;

import java.lang.reflect.Field;
import java.util.Set;

import org.smof.element.Element;
import org.smof.element.ElementFactory;
import org.smof.element.ElementFactoryPool;
import org.smof.element.ElementParser;
import org.smof.element.ElementTypeFactory;
import org.smof.query.SmofQuery;
import org.smof.query.SmofResults;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;

@SuppressWarnings("javadoc")
public class Smof {
	
	private static Smof singleton;
	
	public static Smof create(MongoDatabase database) {
		if(database != null) {
			singleton = new Smof(database);	
		}
		return singleton;
	}
	
	private final MongoDatabase database;
	private final SmofCollectionsPool collections;
	private final Gson gson;
	private final ElementParser elementParser;
	private final ElementFactoryPool factories;
	
	private Smof(MongoDatabase database) {
		final GsonBuilder jsonBuilder = new GsonBuilder();
		this.database = database;
		this.collections = new SmofCollectionsPool();
		this.elementParser = ElementParser.getDefault();
		this.factories = new ElementFactoryPool();
		ElementTypeFactory.init(factories);
		jsonBuilder.registerTypeAdapterFactory(ElementTypeFactory.getDefault());
		gson = jsonBuilder.create();
	}
	
//	private void testConnection() {
//		Debugger.message(DebugLevel.DATABASE, "Connecting to database...");
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					client.getAddress();
//					Debugger.message(DebugLevel.DATABASE, "Connection successful");
//				} catch (MongoTimeoutException e) {
//					Debugger.exception(getClass(), new DBException(e));;
//				}
//			}
//		}).start();;
//	}
	
	public <T extends Element> void loadCollection(String collectionName, ElementFactory<T> factory, Class<T> elClass) {
		factories.put(elClass, factory);
		collections.put(elClass, new SmofCollectionImpl<T>(collectionName, gson, database.getCollection(collectionName), elClass));
	}
	
	public <T extends Element> void createCollection(String collectionName, ElementFactory<T> factory, Class<T> elClass) {
		database.createCollection(collectionName);
		loadCollection(collectionName, factory, elClass);
	}
	
	public boolean dropCollection(String collectionName) {
		boolean success = false;
		for(SmofCollection<?> collection : collections) {
			if(collectionName.equals(collection.getCollectionName())) {
				collection.getMongoCollection().drop();
				success = true;
			}
		}
		return success;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Element> void insert(T element) {
		Set<Field> fields = elementParser.getExternalFields(element.getClass());
		
		if(collections.contains(element.getClass())) {
			for(Field field : fields) {
				try {
					insert(Element.class.cast(field.get(element)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			((SmofCollection<T>) collections.getCollection(element.getClass())).insert(element);	
		}
	}
	
	@SuppressWarnings("cast")
	public <T extends Element> SmofResults<T> find(SmofQuery<T> query) {
		return ((SmofCollection<T>) collections.getCollection(query.getElementClass())).find(query);
	}

}
