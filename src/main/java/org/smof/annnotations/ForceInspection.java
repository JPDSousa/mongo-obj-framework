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
package org.smof.annnotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.smof.collection.Smof;
import org.smof.element.Element;

/**
 * @author Joao
 * 
 * Smof annotation. This annotation is used on java types that are mapped into collections, and therefore implement {@link Element}. 
 * Since smof works with lazy loading, all the sub-classes included in this annotation will be loaded as soon as 
 * {@link Smof#loadCollection(String, Class)} (or variants) or {@link Smof#createCollection(String, Class)} (or variants) are called.
 * Intuitively, all classes mentioned in {@link ForceInspection#value()} must sub-type the type that is marked with this annotation.
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ForceInspection {

	/**
	 * @return list of sub-types that are to be loaded as soon as the type is mapped to a collection
	 */
	Class<?>[] value();
}
