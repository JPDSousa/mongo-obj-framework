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
package org.smof.field;

import java.lang.reflect.Parameter;

import org.smof.annnotations.SmofParam;
import org.smof.parsers.SmofType;

@SuppressWarnings("javadoc")
public class ParameterField implements SmofField {
	
	private final Parameter parameter;
	private final SmofParam note;
	private PrimaryField primaryField;

	public ParameterField(Parameter parameter, SmofParam note) {
		this.parameter = parameter;
		this.note = note;
	}

	@Override
	public SmofType getType() {
		return primaryField == null ? null : primaryField.getType();
	}
	
	public PrimaryField getPrimaryField() {
		return primaryField;
	}
	
	public void setPrimaryField(PrimaryField field) {
		this.primaryField = field;
	}

	@Override
	public Class<?> getFieldClass() {
		return parameter.getType();
	}

	@Override
	public String getName() {
		return note.name();
	}

}
