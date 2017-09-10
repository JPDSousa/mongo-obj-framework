/*******************************************************************************
 * Copyright (C) 2017 Joao
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
package org.smof.collection;

import java.io.Closeable;

import org.bson.BsonDocument;
import org.smof.annnotations.SmofBuilder;
import org.smof.annnotations.SmofObject;
import org.smof.element.Element;
import org.smof.exception.NoSuchCollection;
import org.smof.gridfs.SmofGridStreamManager;
import org.smof.parsers.SmofParser;

import com.google.common.base.Preconditions;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

/**
 * Main object of the API. Represents the layer between the database an the application.
 * Use this object to manage collections, insertions, updates, etc. Use {@link #create(String, int, String)}
 * to create a new connection. (<b>Note:</b> One connection per database is advised.)
 * 
 * <p>This object implements {@link Closeable}, as the database client needs to be closed when
 * it outlives its usefulness.
 * 
 * @author Joao Sousa
 *
 */
public final class Smof implements Closeable {

	public static final String DEFAULT_BUCKET = "fs";
	
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
	private final SmofGridStreamManager gridStreamManager;

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
		this.gridStreamManager = SmofGridStreamManager.newStreamManager(collections);
		this.dispatcher = new SmofDispatcher(collections, gridStreamManager);
		this.parser = new SmofParser(dispatcher);
		loadBucket(DEFAULT_BUCKET);
	}

	public void loadBucket(String bucketName) {
		final GridFSBucket bucket = GridFSBuckets.create(database, bucketName);
		collections.put(bucketName, bucket);
	}
	
	public void dropBucket(String bucketName) {
		final GridFSBucket bucket = collections.getBucket(bucketName);
		bucket.drop();
		collections.removeBucket(bucketName);
	}
	
	public void dropAllBuckets() {
		for(String bucketName : collections.getAllBuckets()) {
			collections.getBucket(bucketName).drop();
		}
		collections.clearBuckets();
	}

	public SmofGridStreamManager getGridStreamManager() {
		return gridStreamManager;
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

	/**
	 * Creates a new mongo collection on the current database. Same as calling {@link #loadCollection(String, Class)} on 
	 * a non-existent collection. An exception will be thrown if the collection already exists. The element class (and all
	 * sub-types) will be mapped to the newly created collection.
	 * 
	 * Same as calling {@link #createCollection(String, Class, CollectionOptions)} with empty {@link CollectionOptions}.
	 * 
	 * @param collectionName collection name
	 * @param elClass element class 
	 */
	public <T extends Element> void createCollection(String collectionName, Class<T> elClass) {
		createCollection(collectionName, elClass, CollectionOptions.create());
	}
	
	/**
	 * Creates a new mongo collection on the current database. Same as calling {@link #loadCollection(String, Class, CollectionOptions)} on 
	 * a non-existent collection. An exception will be thrown if the collection already exists. The element class (and all
	 * sub-types) will be mapped to the newly created collection.
	 * 
	 * @param collectionName collection name
	 * @param elClass element class
	 * @param options collection options
	 */
	public <T extends Element> void createCollection(String collectionName, Class<T> elClass, CollectionOptions<T> options) {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass, options);
	}

	/**
	 * Creates a new mongo collection on the current database. Same as calling {@link #loadCollection(String, Class, Object)} on 
	 * a non-existent collection. An exception will be thrown if the collection already exists. The element class (and all
	 * sub-types) will be mapped to the newly created collection. This method is useful when the element class'
	 * {@link SmofBuilder} is located in another type (e.g. factory pattern). The factory parameter can be either an
	 * instance of a class or even the type itself, in case the {@link SmofBuilder} is referencing a static method.
	 * 
	 * Same as calling {@link #createCollection(String, Class, Object, CollectionOptions)} with empty {@link CollectionOptions}.
	 * 
	 * @param collectionName collection name
	 * @param elClass element class
	 * @param factory third-party object with element class' {@link SmofBuilder}
	 */
	public <T extends Element> void createCollection(String collectionName, Class<T> elClass, Object factory) {
		createCollection(collectionName, elClass, factory, CollectionOptions.create());
	}
	
	/**
	 * Creates a new mongo collection on the current database. Same as calling {@link #loadCollection(String, Class, Object)} on 
	 * a non-existent collection. An exception will be thrown if the collection already exists. The element class (and all
	 * sub-types) will be mapped to the newly created collection. This method is useful when the element class'
	 * {@link SmofBuilder} is located in another type (e.g. factory pattern). The factory parameter can be either an
	 * instance of a class or even the type itself, in case the {@link SmofBuilder} is referencing a static method.
	 * 
	 * @param collectionName collection name
	 * @param elClass element class
	 * @param factory third-party object with element class' {@link SmofBuilder}
	 * @param options additional collection options
	 */
	public <T extends Element> void createCollection(String collectionName, Class<T> elClass, Object factory, CollectionOptions<T> options) {
		database.createCollection(collectionName);
		loadCollection(collectionName, elClass, factory, options);
	}

	/**
	 * Drops a collection. Returns {@code true} if the collection is successfully dropped and {@code false} if it
	 * could not be found.
	 * 
	 * @param collectionName collection name
	 * @return {@code true} if the collection was dropped or {@code false} otherwise
	 */
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
	
	public void dropAllCollections() {
		for(SmofCollection<?> collection : collections) {
			collection.getMongoCollection().drop();
		}
		collections.clearCollections();
	}

	/**
	 * Inserts a new element in the database. The element's type must be mapped to an already
	 * existent collection, otherwise {@link NoSuchCollection} will be thrown. If the element
	 * already exists (i.e. a unique constraint is violated), mongoDB will throw an exception.
	 * 
	 * @param element element to insert
	 */
	@SuppressWarnings("unchecked")
	public <T extends Element> boolean insert(T element) {
		Preconditions.checkArgument(element != null, "Cannot insert a null element");
		final SmofCollection<T> collection = (SmofCollection<T>) collections.getCollection(element.getClass());
		final CollectionOptions<T> options = collection.getCollectionOptions();
		boolean success = false;
		try {
			success = dispatcher.insert(element);
		} catch(MongoWriteException e) {
			if(options.isThrowOnInsertDuplicate()) {
				parser.reset();
				throw e;
			}
		}
		parser.reset();
		return success;
	}
	
	/**
	 * Creates and returns a new {@link SmofUpdate} that allows the user to perform updates to
	 * one or multiple elements. If the element is not mapped to a collection, {@link NoSuchCollection}
	 * will be thrown.
	 * 
	 * The update process in Smof is inspired in the UPDATE..SET..WHERE SQL query:
	 * <ol>
	 * 	<li> Invoke this method to generate an update</li>
	 * 	<li> Use the {@link SmofUpdate} generated to set the updates</li>
	 * 	<li> From the {@link SmofUpdate}, use {@link SmofUpdate#where()} generate a {@link SmofUpdateQuery} 
	 * 	that will allow you to specify which elements the updated will be applied to.</li>
	 * </ol>
	 * <b>Note:</b> {@link SmofUpdate} also comes with methods that simplify this process for certain use
	 * cases. Check the documentation for further details.
	 * 
	 * @param elementClass element class
	 * @return a new {@link SmofUpdate} object
	 */
	public <T extends Element> SmofUpdate<T> update(Class<T> elementClass) {
		final SmofCollection<T> collection = collections.getCollection(elementClass);
		return collection.update();
	}

	/**
	 * Creates and returns a new {@link SmofQuery} that allows the user to perform a read operation.
	 * The element class passed as parameter must be previously registered to a mongo collection,
	 * otherwise {@link NoSuchCollection} is thrown.
	 * 
	 * @param elementClass element class
	 * @return a new {@link SmofQuery} object
	 */
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
