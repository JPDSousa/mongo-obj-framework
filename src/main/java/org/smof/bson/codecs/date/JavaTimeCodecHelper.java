/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
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
