package org.smof.bson.codecs.bool;

class IntegerCodec extends AbstractBooleanCodec<Integer> {

	@Override
	public Class<Integer> getEncoderClass() {
		return Integer.class;
	}

	@Override
	protected boolean encode(Integer value) {
		return value != 0;
	}

	@Override
	protected Integer decode(boolean value) {
		return value ? 1 : 0;
	}

}
