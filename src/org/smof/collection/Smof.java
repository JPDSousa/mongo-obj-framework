package org.smof.collection;

import org.bson.BsonDocument;

import org.smof.element.Element;
import org.smof.exception.SmofException;
import org.smof.index.InternalIndex;
import org.smof.parsers.TypeParser;
import org.smof.parsers.SmofParser;

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
	private final CollectionsPool collections;
	private final SmofParser parser;
	private final SmofDispatcher dispatcher;

	private Smof(MongoDatabase database) {
		this.database = database;
		this.collections = new CollectionsPool();
		this.dispatcher = new SmofDispatcher(collections);
		this.parser = new SmofParser(dispatcher);
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
		parser.registerType(elClass);
		loadCollection(collectionName, elClass, parser);
	}

	public <T extends Element> void loadCollection(String collectionName, Class<T> elClass, Object factory) {
		parser.registerType(elClass, factory);
		loadCollection(collectionName, elClass, parser);
	}

	private <T extends Element> void loadCollection(String collectionName, Class<T> elClass, SmofParser parser) {
		collections.put(elClass, new SmofCollectionImpl<T>(collectionName, database.getCollection(collectionName, BsonDocument.class), elClass, parser));
	}

	public <T> void registerSmofObject(Class<T> type) throws SmofException {
		parser.registerType(type);
	}

	public <T> void registerSmofFactory(Class<T> type, Object factory) throws SmofException {
		parser.registerType(type, factory);
	}

	public <T extends Element> void createCollection(String collectionName, Class<T> elClass) throws SmofException {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass);
		createIndexes(elClass);
	}

	public <T extends Element> void createCollection(String collectionName, Class<T> elClass, Object factory) throws SmofException {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass, factory);
		createIndexes(elClass);
	}

	private <T extends Element> void createIndexes(Class<T> elClass) {
		final TypeParser<T> typeMetadata = parser.getTypeParser(elClass);
		final SmofCollection<T> collection = collections.getCollection(elClass);
		for(InternalIndex index : typeMetadata.getIndexes()) {
			collection.getMongoCollection().createIndex(index.getIndex());
		}
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

	public <T extends Element> void insert(T element) {
		dispatcher.insert(element);
	}

	@SuppressWarnings("cast")
	public <T extends Element> SmofQuery<T> find(Class<T> elementClass) {
		final SmofCollection<T> collection = (SmofCollection<T>) collections.getCollection(elementClass);
		return collection.query();
	}

}
