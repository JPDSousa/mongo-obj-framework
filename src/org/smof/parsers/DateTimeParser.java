package org.smof.parsers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.bson.BsonDateTime;
import org.bson.BsonValue;
import org.smof.annnotations.SmofField;

class DateTimeParser extends AbstractBsonParser {
	
	private static final ZoneId ZONE = ZoneId.systemDefault();
	private static final Class<?>[] VALID_TYPES = {Instant.class, LocalDate.class, LocalDateTime.class, 
			ZonedDateTime.class};
	
	DateTimeParser(SmofParser parser) {
		super(parser, VALID_TYPES);
	}

	@Override
	public BsonValue toBson(Object value, SmofField field) {
		final Class<?> type = value.getClass();
		if(isInstance(type)) {
			return new BsonDateTime(fromInstant((Instant) value));
		}
		else if(isJavaLocalDateTime(type)) {
			return new BsonDateTime(fromJavaLocalDateTime((LocalDateTime) value));
		}
		else if(isJavaLocalDate(type)) {
			return new BsonDateTime(fromJavaLocalDate((LocalDate) value));
		}
		else if(isZoneDateTime(type)) {
			return new BsonDateTime(fromZoneDateTime((ZonedDateTime) value)); 
		}
		return null;
	}

	private long fromZoneDateTime(ZonedDateTime value) {
		return fromInstant(value.toInstant());
	}

	private boolean isZoneDateTime(Class<?> type) {
		return type.equals(ZonedDateTime.class);
	}
	
	private long fromJavaLocalDate(LocalDate value) {
		return fromZoneDateTime(value.atStartOfDay(ZONE));
	}

	private boolean isJavaLocalDate(Class<?> type) {
		return type.equals(LocalDate.class);
	}
	
	private long fromJavaLocalDateTime(LocalDateTime value) {
		return fromZoneDateTime(value.atZone(ZONE));
	}

	private boolean isJavaLocalDateTime(Class<?> type) {
		return type.equals(LocalDateTime.class);
	}
	
	private long fromInstant(Instant value) {
		return value.toEpochMilli();
	}

	private boolean isInstance(Class<?> type) {
		return type.equals(Instant.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts) {
		final long date = value.asDateTime().getValue();
		final Object retValue;
		if(isInstance(type)) {
			retValue = toInstant(date);
		}
		else if(isJavaLocalDateTime(type)) {
			retValue = toJavaLocalDateTime(date);
		}
		else if(isJavaLocalDate(type)) {
			retValue = toJavaLocalDate(date);
		}
		else if(isZoneDateTime(type)) {
			retValue = toZoneDateTime(date);
		}
		else {
			retValue = null;
		}
		return (T) retValue;
	}
	
	private Instant toInstant(long value) {
		return Instant.ofEpochMilli(value);
	}
	
	private ZonedDateTime toZoneDateTime(long value) {
		return toInstant(value).atZone(ZONE);
	}
	
	private LocalDateTime toJavaLocalDateTime(long value) {
		return toZoneDateTime(value).toLocalDateTime();
	}
	
	private LocalDate toJavaLocalDate(long value) {
		return toZoneDateTime(value).toLocalDate();
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return value.isDateTime();
	}

}
