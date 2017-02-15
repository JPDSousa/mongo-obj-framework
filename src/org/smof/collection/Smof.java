package org.smof.collection;

import java.util.Set;

import org.bson.BsonDocument;
import org.smof.element.Element;
import org.smof.element.SmofFactory;
import org.smof.element.SmofAdapter;
import org.smof.element.SmofAdapterPool;
import org.smof.element.SmofAnnotationParser;
import org.smof.element.field.SmofField;
import org.smof.exception.InvalidSmofTypeException;
import org.smof.exception.SmofException;
import org.smof.query.SmofQuery;
import org.smof.query.SmofResults;

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
	private final SmofAdapterPool adapters;
	
	private Smof(MongoDatabase database) {
		this.database = database;
		this.collections = new SmofCollectionsPool();
		this.adapters = new SmofAdapterPool();
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
	
	public <T extends Element> void loadCollection(String collectionName, SmofFactory<T> factory, Class<T> elClass) throws SmofException {
		SmofAdapter<T> adapter;
		try {
			adapter = new SmofAdapter<>(factory, new SmofAnnotationParser<>(elClass), adapters);
			adapters.put(adapter);
			collections.put(elClass, new SmofCollectionImpl<T>(collectionName, database.getCollection(collectionName, BsonDocument.class), adapter));
		} catch (InvalidSmofTypeException e) {
			throw new SmofException(e);
		}
	}
	
	public <T> void registerSmofFactory(Class<T> type, SmofFactory<T> factory) throws SmofException {
		try {
			adapters.put(new SmofAdapter<>(factory, new SmofAnnotationParser<>(type), adapters));
		} catch (InvalidSmofTypeException e) {
			throw new SmofException(e);
		}
	}
	
	public <T extends Element> void createCollection(String collectionName, SmofFactory<T> factory, Class<T> elClass) throws SmofException {
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
		Set<SmofField> fields = collections.getCollection(element.getClass()).getParser().getSmofMetadata().getExternalFields();
				
		if(collections.contains(element.getClass())) {
			for(SmofField field : fields) {
				try {
					insert(Element.class.cast(field.getField().get(element)));
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
