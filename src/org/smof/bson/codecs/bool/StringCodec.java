package org.smof.bson.codecs.bool;

class StringCodec extends AbstractBooleanCodec<String> {

	@Override
	public Class<String> getEncoderClass() {
		return String.class;
	}

	@Override
	protected boolean encode(String value) {
		return Boolean.parseBoolean(value);
	}

	@Override
	protected String decode(boolean value) {
		return Boolean.toString(value);
	}

}
