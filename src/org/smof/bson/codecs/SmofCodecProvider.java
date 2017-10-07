package org.smof.bson.codecs;

import org.bson.codecs.configuration.CodecProvider;

@SuppressWarnings("javadoc")
public interface SmofCodecProvider extends CodecProvider {

	public boolean contains(Class<?> clazz);
	
}
