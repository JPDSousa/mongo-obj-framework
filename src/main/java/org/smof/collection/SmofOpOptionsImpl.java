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
/*
 * ******************************************************************************
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
 *****************************************************************************
 */
package org.smof.collection;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.conversions.Bson;

import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;

@SuppressWarnings("javadoc")
public class SmofOpOptionsImpl implements SmofOpOptions {
	
	private boolean upsert;
	private boolean validateDocuments;
	private Pair<Long, TimeUnit> maxTime;
	private Bson projection;
	private Bson sort;
	private ReturnDocument ret;
	private boolean bypassCache;
	
	public SmofOpOptionsImpl() {
		upsert = false;
		validateDocuments = true;
		ret = ReturnDocument.AFTER;
		bypassCache = false;
	}
	
	@Override
    public FindOneAndReplaceOptions toFindOneAndReplace() {
		final FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
		options.bypassDocumentValidation(!validateDocuments);
		setMaxTime(options);
		setProjection(options);
		options.returnDocument(ret);
		setSort(options);
		options.upsert(upsert);
		return options;
	}

	private void setSort(final FindOneAndReplaceOptions options) {
		if(sort != null) {
			options.sort(sort);
		}
	}

	private void setProjection(final FindOneAndReplaceOptions options) {
		if(projection != null) {
			options.projection(projection);
		}
	}

	private void setMaxTime(final FindOneAndReplaceOptions options) {
		if(maxTime != null) {
			options.maxTime(maxTime.getLeft(), maxTime.getRight());
		}
	}
	
	@Override
    public UpdateOptions toUpdateOptions() {
		final UpdateOptions options = new UpdateOptions();
		options.bypassDocumentValidation(!validateDocuments);
		options.upsert(upsert);
		return options;
	}
	
	@Override
    public FindOneAndUpdateOptions toFindOneAndUpdateOptions() {
		final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
		options.bypassDocumentValidation(!validateDocuments);
		setMaxTime(options);
		setProjection(options);
		options.returnDocument(ret);
		setSort(options);
		options.upsert(upsert);
		return options;
	}

	private void setSort(final FindOneAndUpdateOptions options) {
		if(sort != null) {
			options.sort(sort);
		}
	}

	private void setProjection(final FindOneAndUpdateOptions options) {
		if(projection != null) {
			options.projection(projection);
		}
	}

	private void setMaxTime(final FindOneAndUpdateOptions options) {
		if(maxTime != null) {
			options.maxTime(maxTime.getLeft(), maxTime.getRight());
		}
	}
	
	@Override
    public void upsert(boolean upsert) {
		this.upsert = upsert;
	}
	
	@Override
    public void validateDocuments(boolean validateDocuments) {
		this.validateDocuments = validateDocuments;
	}
	
	@Override
    public void setReturnDocument(ReturnDocument doc) {
		this.ret = doc;
	}
	
	@Override
    public void bypassCache(boolean bypassCache) {
		this.bypassCache = bypassCache;
	}
	
	@Override
    public boolean isBypassCache() {
		return bypassCache;
	}

	@Override
	public String toString() {
		return "SmofOpOptions [upsert=" + upsert 
				+ ", validateDocuments=" + validateDocuments 
				+ ", maxTime=" + maxTime
				+ ", projection=" + projection 
				+ ", sort=" + sort 
				+ ", ret=" + ret 
				+ ", bypassCache=" + bypassCache
				+ "]";
	}
	
}
