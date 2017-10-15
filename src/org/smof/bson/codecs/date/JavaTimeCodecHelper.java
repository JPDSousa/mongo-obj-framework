package org.smof.bson.codecs.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

final class JavaTimeCodecHelper {

	protected static final ZoneId ZONE = ZoneId.systemDefault();

	protected static long fromZonedDateTime(ZonedDateTime value) {
		return fromInstant(value.toInstant());
	}
	
	protected static long fromLocalDate(LocalDate value) {
		return fromZonedDateTime(value.atStartOfDay(ZONE));
	}
	
	protected static long fromLocalDateTime(LocalDateTime value) {
		return fromZonedDateTime(value.atZone(ZONE));
	}
	
	protected static long fromInstant(Instant value) {
		return value.toEpochMilli();
	}
	
	protected static Instant toInstant(long value) {
		return Instant.ofEpochMilli(value);
	}
	
	protected static ZonedDateTime toZonedDateTime(long value) {
		return toInstant(value).atZone(ZONE);
	}
	
	protected static LocalDateTime toLocalDateTime(long value) {
		return toZonedDateTime(value).toLocalDateTime();
	}
	
	protected static LocalDate toLocalDate(long value) {
		return toZonedDateTime(value).toLocalDate();
	}
}
