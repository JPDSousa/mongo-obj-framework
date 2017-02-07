package org.smof.collection;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.smof.element.Element;
import org.smof.element.ElementParser;
import org.smof.element.ElementTypeFactory;
import org.smof.element.field.SmofObjectId;

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
	private final Map<Class<? extends Element>, SmofCollection<? extends Element>> collections;
	private final Gson gson;
	private final ElementParser elementParser;
	
	private Smof(MongoDatabase database) {
		final GsonBuilder jsonBuilder = new GsonBuilder();
		this.database = database;
		this.collections = new LinkedHashMap<>();
		this.elementParser = ElementParser.getDefault();
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
	
	public <T extends Element> void loadCollection(String collectionName, Class<T> elClass) {
		collections.put(elClass, new SmofCollectionImpl<T>(gson, database.getCollection(collectionName), elClass));
	}
	
	public <T extends Element> void createCollection(String collectionName, Class<T> elClass) {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass);
	}
	
	public boolean dropCollection(String collectionName) {
		boolean success = false;
		for(SmofCollection<?> collection : collections.values()) {
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
		
		if(collections.containsKey(element.getClass())) {
			for(Field field : fields) {
				try {
					insert(Element.class.cast(field.get(element)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			((SmofCollection<T>) collections.get(element.getClass())).insert(element);	
		}
	}

}
