package org.smof.bson.codecs.string;

class StringCodec extends AbstractStringCodec<String> {

	@Override
	public Class<String> getEncoderClass() {
		return String.class;
	}

	@Override
	protected String decode(String value) {
		return value;
	}

}
