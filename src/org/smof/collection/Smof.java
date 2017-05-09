package org.smof.collection;

import java.io.Closeable;

import org.bson.BsonDocument;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofObject;
import org.smof.element.Element;
import org.smof.parsers.SmofParser;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@SuppressWarnings("javadoc")
public class Smof implements Closeable {

	/**
	 * Creates a new smof connection.
	 * 
	 * @param database mongo database
	 * @return a new smof connection
	 * @deprecated use {@link Smof#create(String, int, String)} instead
	 */
	@Deprecated
	public static Smof create(MongoDatabase database) {
		return new Smof(null, database);
	}
	
	/**
	 * Creates a new smof connection. Use this method to initialize the api.
	 * 
	 * @param host mongoDB host
	 * @param port mongoDB port
	 * @param databaseName mongoDB database name
	 * @return a new smof connection
	 */
	public static Smof create(String host, int port, String databaseName) {
		final MongoClient client = new MongoClient(host, port);
		final MongoDatabase database = client.getDatabase(databaseName);
		return new Smof(client, database);
	}

	private final MongoDatabase database;
	private final CollectionsPool collections;
	private final SmofParser parser;
	private final SmofDispatcher dispatcher;
	private final MongoClient client;

	/**
	 * Private constructor. This constructor is only accessed through the {@link #create(String, int, String)}
	 * method (and additionally through the deprecated {@link #create(MongoDatabase)}), which initializes
	 * the object
	 * 
	 * @param client mongo client
	 * @param database mongo database
	 */
	private Smof(MongoClient client, MongoDatabase database) {
		this.client = client;
		this.database = database;
		this.collections = new CollectionsPool();
		this.dispatcher = new SmofDispatcher(collections);
		this.parser = new SmofParser(dispatcher);
	}

	/**
	 * Loads a new collection into the API. If the database has no collection with the name passed
	 * as parameter, one will be created. The element class (elClass) is the type mapped to this collection.
	 * All sub-types will be implicitly mapped to this class as well.  
	 * 
	 * Same as using {@link #loadCollection(String, Class, CollectionOptions)} with empty {@link CollectionOptions}.
	 * 
	 * @param collectionName collection name on mongoDB
	 * @param elClass element class
	 */
	public <T extends Element> void loadCollection(String collectionName, Class<T> elClass) {
		loadCollection(collectionName, elClass, CollectionOptions.create());
	}
	
	/**
	 * Loads a new collection into the API. If the database has no collection with the name passed
	 * as parameter, one will be created. The element class (elClass) is the type mapped to this collection.
	 * All sub-types will be implicitly mapped to this class as well. Use collection options to specify additional
	 * properties.
	 * 
	 * @param collectionName collection name on mongoDB
	 * @param elClass element class
	 * @param options collection options
	 */
	public <T extends Element> void loadCollection(String collectionName, Class<T> elClass, CollectionOptions<T> options) {
		parser.registerType(elClass);
		loadCollection(collectionName, elClass, parser, options);
	}

	/**
	 * Loads a new collection into the API. If the database has no collection with the name passed
	 * as parameter, one will be created. The element class (elClass) is the type mapped to this collection.
	 * All sub-types will be implicitly mapped to this class as well. This method is useful when the element class'
	 * {@link SmofBuilder} is located in another type (e.g. factory pattern). The factory parameter can be either an
	 * instance of a class or even the type itself, in case the {@link SmofBuilder} is referencing a static method.
	 * 
	 * Same as calling {@link #loadCollection(String, Class, Object, CollectionOptions)} with empty {@link CollectionOptions}.
	 * 
	 * @param collectionName collection name on mongoDB
	 * @param elClass element class
	 * @param factory third-party object with the element class' {@link SmofBuilder}.
	 */
	public <T extends Element> void loadCollection(String collectionName, Class<T> elClass, Object factory) {
		loadCollection(collectionName, elClass, factory, CollectionOptions.create());
	}
	
	/**
	 * Loads a new collection into the API. If the database has no collection with the name passed
	 * as parameter, one will be created. The element class (elClass) is the type mapped to this collection.
	 * All sub-types will be implicitly mapped to this class as well. This method is useful when the element class'
	 * {@link SmofBuilder} is located in another type (e.g. factory pattern). The factory parameter can be either an
	 * instance of a class or even the type itself, in case the {@link SmofBuilder} is referencing a static method.
	 * Use collection options to specify additional properties.
	 * 
	 * @param collectionName collection name on mongoDB
	 * @param elClass element class
	 * @param factory third-party object with the element class' {@link SmofBuilder}.
	 * @param options collection options
	 */
	public <T extends Element> void loadCollection(String collectionName, Class<T> elClass, Object factory, CollectionOptions<T> options) {
		parser.registerType(elClass, factory);
		loadCollection(collectionName, elClass, parser, options);
	}

	private <T extends Element> void loadCollection(String collectionName, Class<T> elClass, SmofParser parser, CollectionOptions<T> options) {
		final MongoCollection<BsonDocument> collection = database.getCollection(collectionName, BsonDocument.class);
		collections.put(elClass, new SmofCollectionImpl<T>(collectionName, collection, elClass, parser, options));
	}

	/**
	 * Registers an external (non-element) type in the API. This method is not strictly required, as all objects are lazy
	 * registered as main types get used. Mind that all objects tagged with the {@link SmofObject} annotation must
	 * have a {@link SmofBuilder} annotation.
	 * 
	 * @param type type to register
	 */
	public <T> void registerSmofObject(Class<T> type) {
		parser.registerType(type);
	}

	/**
	 * Registers an external (non-element) type in the API. This method is not strictly required, as all objects are lazy
	 * registered as main types get used. However, this method is required if the {@link SmofBuilder} is placed at another 
	 * type (e.g. factory pattern), as the lazy-register as no way of locating an external {@link SmofBuilder}. 
	 * Mind that all objects tagged with the {@link SmofObject} annotation must have a {@link SmofBuilder} annotation.
	 * 
	 * The factory parameter can either be a an object or a class reference, if the {@link SmofBuilder} is static.
	 * 
	 * @param type type to register
	 * @param factory object or class with the type's {@link SmofBuilder}
	 */
	public <T> void registerSmofFactory(Class<T> type, Object factory) {
		parser.registerType(type, factory);
	}

	public <T extends Element> void createCollection(String collectionName, Class<T> elClass) {
		createCollection(collectionName, elClass, CollectionOptions.create());
	}
	
	public <T extends Element> void createCollection(String collectionName, Class<T> elClass, CollectionOptions<T> options) {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass, options);
	}

	public <T extends Element> void createCollection(String collectionName, Class<T> elClass, Object factory) {
		createCollection(collectionName, elClass, factory, CollectionOptions.create());
	}
	
	public <T extends Element> void createCollection(String collectionName, Class<T> elClass, Object factory, CollectionOptions<T> options) {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass, factory, options);
	}

	public boolean dropCollection(String collectionName) {
		SmofCollection<?> toDrop = null;
		for(SmofCollection<?> collection : collections) {
			if(collectionName.equals(collection.getCollectionName())) {
				toDrop = collection;
				break;
			}
		}
		if(toDrop != null) {
			toDrop.getMongoCollection().drop();
			collections.remove(toDrop);
			return true;
		}
		return false;
	}

	public <T extends Element> void insert(T element) {
		dispatcher.insert(element);
		parser.reset();
	}
	
	public <T extends Element> SmofUpdate<T> update(Class<T> elementClass) {
		final SmofCollection<T> collection = collections.getCollection(elementClass);
		return collection.update();
	}

	public <T extends Element> SmofQuery<T> find(Class<T> elementClass) {
		final SmofCollection<T> collection = collections.getCollection(elementClass);
		return collection.query();
	}

	@Override
	public void close() {
		if(client != null) {
			client.close();
		}
	}

}
