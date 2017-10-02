package org.smof.codecs.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

class JavaTimeCodecHelper {

	static final ZoneId ZONE = ZoneId.systemDefault();

	static long fromZonedDateTime(ZonedDateTime value) {
		return fromInstant(value.toInstant());
	}
	
	static long fromLocalDate(LocalDate value) {
		return fromZonedDateTime(value.atStartOfDay(ZONE));
	}
	
	static long fromLocalDateTime(LocalDateTime value) {
		return fromZonedDateTime(value.atZone(ZONE));
	}
	
	static long fromInstant(Instant value) {
		return value.toEpochMilli();
	}
	
	static Instant toInstant(long value) {
		return Instant.ofEpochMilli(value);
	}
	
	static ZonedDateTime toZonedDateTime(long value) {
		return toInstant(value).atZone(ZONE);
	}
	
	static LocalDateTime toLocalDateTime(long value) {
		return toZonedDateTime(value).toLocalDateTime();
	}
	
	static LocalDate toLocalDate(long value) {
		return toZonedDateTime(value).toLocalDate();
	}
}
