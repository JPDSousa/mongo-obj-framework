package org.smof.index;

@SuppressWarnings("javadoc")
public enum IndexType {
	
//	HASHED(""),
	TEXT("text"),
	DESCENDING("-1"),
	ASCENDING("1");

	private final String mongoToken;
	
	private IndexType(String mongoToken) {
		this.mongoToken = mongoToken;
	}
	
	public static IndexType parse(String indexType) {
		for(IndexType type : values()) {
			if(type.mongoToken.equals(indexType)) {
				return type;
			}
		}
		return null;
	}

}
