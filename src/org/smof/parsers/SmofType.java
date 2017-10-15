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

import java.lang.annotation.Annotation;

import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofBoolean;
import org.smof.annnotations.SmofByte;
import org.smof.annnotations.SmofDate;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofObject;
import org.smof.annnotations.SmofObjectId;
import org.smof.annnotations.SmofString;

@SuppressWarnings("javadoc")
public enum SmofType {

	STRING(SmofString.class),
	NUMBER(SmofNumber.class),
	DATETIME(SmofDate.class),
	OBJECT(SmofObject.class),
	OBJECT_ID(SmofObjectId.class),
	ARRAY(SmofArray.class),
	BYTE(SmofByte.class),
	BOOLEAN(SmofBoolean.class);

	private final Class<? extends Annotation> annotClass;

	SmofType(Class<? extends Annotation> annotClass) {
		this.annotClass = annotClass;
	}

	public Class<? extends Annotation> getAnnotClass() {
		return annotClass;
	}
}
