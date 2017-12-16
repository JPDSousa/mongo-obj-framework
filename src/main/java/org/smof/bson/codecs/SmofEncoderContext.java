package org.smof.bson.codecs;

import java.util.List;
import java.util.concurrent.Callable;

import org.bson.BsonElement;
import org.smof.field.SmofField;

@SuppressWarnings("javadoc")
public class SmofEncoderContext {
	
	private final List<Callable<BsonElement>> posInsertionHooks;
	private final SmofField field;
	
	public SmofEncoderContext(List<Callable<BsonElement>> posInsertionHooks, SmofField field) {
		super();
		this.posInsertionHooks = posInsertionHooks;
		this.field = field;
	}

	public SmofField getField() {
		return field;
	}
	
	public void addPosInsertionHook(Callable<BsonElement> hook) {
		posInsertionHooks.add(hook);
	}
	
	public List<Callable<BsonElement>> getPosInsertionHooks() {
		return posInsertionHooks;
	}
	
	

}
