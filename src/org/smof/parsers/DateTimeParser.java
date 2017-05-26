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
package org.smof.parsers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.bson.BsonDateTime;
import org.bson.BsonValue;
import org.joda.time.DateTime;
import org.smof.collection.SmofDispatcher;
import org.smof.field.SmofField;

class DateTimeParser extends AbstractBsonParser {
	
	private static final ZoneId ZONE = ZoneId.systemDefault();
	private static final Class<?>[] VALID_TYPES = {Instant.class, LocalDate.class, LocalDateTime.class, 
			ZonedDateTime.class, org.joda.time.Instant.class, DateTime.class, org.joda.time.LocalDate.class, org.joda.time.LocalDateTime.class};
	
	DateTimeParser(SmofParser parser, SmofDispatcher dispatcher) {
		super(dispatcher, parser, VALID_TYPES);
	}

	@Override
	protected BsonValue serializeToBson(Object value, SmofField field) {
		final Class<?> type = value.getClass();
		if(isJavaInstant(type)) {
			return new BsonDateTime(fromJavaInstant((Instant) value));
		}
		else if(isJavaLocalDateTime(type)) {
			return new BsonDateTime(fromJavaLocalDateTime((LocalDateTime) value));
		}
		else if(isJavaLocalDate(type)) {
			return new BsonDateTime(fromJavaLocalDate((LocalDate) value));
		}
		else if(isZoneDateTime(type)) {
			return new BsonDateTime(fromJavaZoneDateTime((ZonedDateTime) value)); 
		}
		else if(isJodaDateTime(type)) {
			return new BsonDateTime(fromJodaDateTime((DateTime) value));
		}
		else if(isJodaLocalDate(type)) {
			return new BsonDateTime(fromJodaLocalDate((org.joda.time.LocalDate) value));
		}
		else if(isJodaLocalDateTime(type)) {
			return new BsonDateTime(fromJodaLocalDateTime((org.joda.time.LocalDateTime) value));
		}
		else if(isJodaInstant(type)) {
			return new BsonDateTime(fromJodaInstant((org.joda.time.Instant) value));
		}
		return null;
	}

	private long fromJodaInstant(org.joda.time.Instant value) {
		return value.getMillis();
	}

	private boolean isJodaInstant(Class<?> type) {
		return type.equals(org.joda.time.Instant.class);
	}

	private long fromJodaLocalDateTime(org.joda.time.LocalDateTime value) {
		return fromJodaDateTime(value.toDateTime());
	}

	private boolean isJodaLocalDateTime(Class<?> type) {
		return type.equals(org.joda.time.LocalDateTime.class);
	}

	private long fromJodaLocalDate(org.joda.time.LocalDate value) {
		return fromJodaDateTime(value.toDateTimeAtStartOfDay());
	}

	private boolean isJodaLocalDate(Class<?> type) {
		return type.equals(org.joda.time.LocalDate.class);
	}

	private long fromJodaDateTime(DateTime value) {
		return value.getMillis();
	}

	private boolean isJodaDateTime(Class<?> type) {
		return type.equals(DateTime.class);
	}

	private long fromJavaZoneDateTime(ZonedDateTime value) {
		return fromJavaInstant(value.toInstant());
	}

	private boolean isZoneDateTime(Class<?> type) {
		return type.equals(ZonedDateTime.class);
	}
	
	private long fromJavaLocalDate(LocalDate value) {
		return fromJavaZoneDateTime(value.atStartOfDay(ZONE));
	}

	private boolean isJavaLocalDate(Class<?> type) {
		return type.equals(LocalDate.class);
	}
	
	private long fromJavaLocalDateTime(LocalDateTime value) {
		return fromJavaZoneDateTime(value.atZone(ZONE));
	}

	private boolean isJavaLocalDateTime(Class<?> type) {
		return type.equals(LocalDateTime.class);
	}
	
	private long fromJavaInstant(Instant value) {
		return value.toEpochMilli();
	}

	private boolean isJavaInstant(Class<?> type) {
		return type.equals(Instant.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fromBson(BsonValue value, Class<T> type, SmofField fieldOpts) {
		final long date = value.asDateTime().getValue();
		final Object retValue;
		if(isJavaInstant(type)) {
			retValue = toJavaInstant(date);
		}
		else if(isJavaLocalDateTime(type)) {
			retValue = toJavaLocalDateTime(date);
		}
		else if(isJavaLocalDate(type)) {
			retValue = toJavaLocalDate(date);
		}
		else if(isZoneDateTime(type)) {
			retValue = toJavaZoneDateTime(date);
		}
		else if(isJodaInstant(type)) {
			retValue = toJodaIntant(date);
		}
		else if(isJodaDateTime(type)) {
			retValue = toJodaDateTime(date);
		}
		else if(isJodaLocalDate(type)) {
			retValue = toJodaLocalDate(date);
		}
		else if(isJodaLocalDateTime(type)) {
			retValue = toJodaLocalDateTime(date);
		}
		else {
			retValue = null;
		}
		return (T) retValue;
	}
	
	private org.joda.time.LocalDateTime toJodaLocalDateTime(long date) {
		return toJodaDateTime(date).toLocalDateTime();
	}

	private org.joda.time.LocalDate toJodaLocalDate(long date) {
		return toJodaDateTime(date).toLocalDate();
	}

	private DateTime toJodaDateTime(long date) {
		return toJodaIntant(date).toDateTime();
	}

	private org.joda.time.Instant toJodaIntant(long date) {
		return  new org.joda.time.Instant(date);
	}

	private Instant toJavaInstant(long value) {
		return Instant.ofEpochMilli(value);
	}
	
	private ZonedDateTime toJavaZoneDateTime(long value) {
		return toJavaInstant(value).atZone(ZONE);
	}
	
	private LocalDateTime toJavaLocalDateTime(long value) {
		return toJavaZoneDateTime(value).toLocalDateTime();
	}
	
	private LocalDate toJavaLocalDate(long value) {
		return toJavaZoneDateTime(value).toLocalDate();
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return super.isValidBson(value) || value.isDateTime();
	}

}
