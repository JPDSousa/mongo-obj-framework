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
package org.smof.collection;

import java.util.Collection;

import org.smof.element.Element;

@SuppressWarnings("javadoc")
public interface SmofUpdate<T extends Element> {

    SmofUpdate<T> setUpsert(boolean upsert);

    SmofUpdateQuery<T> where();

    SmofUpdate<T> set(String fieldName, Object value);
    SmofUpdate<T> unset(String fieldName);
    
    SmofUpdate<T> currentDate(String fieldName);
    
    SmofUpdate<T> increase(String fieldName, Number value);
    SmofUpdate<T> decrease(String fieldName, Number value);
    SmofUpdate<T> multiply(String fieldName, Number value);
    SmofUpdate<T> divide(String fieldName, Number value);
    
    SmofUpdate<T> minimum(String fieldName, Number value);
    SmofUpdate<T> maximum(String fieldName, Number value);
    
    SmofUpdate<T> addToSet(String fieldName, Object value);
    SmofUpdate<T> addToSet(String fieldName, Collection<?> values);
    SmofUpdate<T> pop(String fieldName, boolean removeFirst);
    
    SmofUpdate<T> push(String fieldName, Object value);
    SmofUpdate<T> push(String fieldName, int index, Object value);
    SmofUpdate<T> pushAll(String fieldName, Collection<?> values);
    SmofUpdate<T> pushAll(String fieldName, int index, Collection<?> values);
    
    SmofUpdate<T> pull(String fieldName, Object value);
    SmofUpdate<T> pullAll(String fieldName, Collection<?> values);

}
