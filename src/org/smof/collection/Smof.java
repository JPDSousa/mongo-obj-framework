package org.smof.collection;

import java.util.Set;

import org.bson.BsonDocument;
import org.smof.element.Element;
import org.smof.element.SmofAdapter;
import org.smof.element.SmofAdapterPool;
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

	public <T extends Element> void loadCollection(String collectionName, Class<T> elClass) throws SmofException {
		try {
			SmofAdapter<T> adapter = adapters.put(elClass);
			loadCollection(collectionName, elClass, adapter);
		} catch (InvalidSmofTypeException e) {
			throw new SmofException(e);
		}
	}
	
	public <T extends Element> void loadCollection(String collectionName, Class<T> elClass, Object factory) throws SmofException {
		try {
			SmofAdapter<T> adapter = adapters.put(elClass, factory);
			loadCollection(collectionName, elClass, adapter);
		} catch (InvalidSmofTypeException e) {
			throw new SmofException(e);
		}
	}

	private <T extends Element> void loadCollection(String collectionName, Class<T> elClass, SmofAdapter<T> adapter) {
		collections.put(elClass, new SmofCollectionImpl<T>(collectionName, database.getCollection(collectionName, BsonDocument.class), adapter));
	}

	public <T> void registerSmofFactory(Class<T> type) throws SmofException {
		try {
			adapters.put(type);
		} catch (InvalidSmofTypeException e) {
			throw new SmofException(e);
		}
	}
	
	public <T> void registerSmofFactory(Class<T> type, Object factory) throws SmofException {
		try {
			adapters.put(type, factory);
		} catch (InvalidSmofTypeException e) {
			throw new SmofException(e);
		}
	}

	public <T extends Element> void createCollection(String collectionName, Class<T> elClass) throws SmofException {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass);
	}
	
	public <T extends Element> void createCollection(String collectionName, Class<T> elClass, Object factory) throws SmofException {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass, factory);
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
