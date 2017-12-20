package org.smof.bson.codecs;

import java.util.List;
import java.util.function.Consumer;

import org.smof.collection.SmofUpdate;
import org.smof.element.Element;
import org.smof.field.SmofField;

import com.google.common.collect.Lists;

@SuppressWarnings("javadoc")
public class SmofEncoderContext {
	
	public static SmofEncoderContext create(SmofField field) {
		return new SmofEncoderContext(Lists.newArrayList(), field);
	}
	
	public static SmofEncoderContext create(List<Consumer<SmofUpdate<Element>>> posInsertionHooks, SmofField field) {
		return new SmofEncoderContext(posInsertionHooks, field);
	}
	
	private final List<Consumer<SmofUpdate<Element>>> posInsertionHooks;
	private final SmofField field;
	
	private SmofEncoderContext(List<Consumer<SmofUpdate<Element>>> posInsertionHooks, SmofField field) {
		super();
		this.posInsertionHooks = posInsertionHooks;
		this.field = field;
	}

	public SmofField getField() {
		return field;
	}
	
	public void addPosInsertionHook(Consumer<SmofUpdate<Element>> hook) {
		posInsertionHooks.add(hook);
	}
	
	public List<Consumer<SmofUpdate<Element>>> getPosInsertionHooks() {
		return posInsertionHooks;
	}
	
	

}
