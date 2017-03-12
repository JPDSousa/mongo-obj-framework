package org.smof.collection;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.conversions.Bson;

import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;

@SuppressWarnings("javadoc")
public class SmofUpdateOptions {
	
	public static SmofUpdateOptions create() {
		return new SmofUpdateOptions();
	}
	
	private boolean upsert;
	private boolean validateDocuments;
	private Pair<Long, TimeUnit> maxTime;
	private Bson projection;
	private Bson sort;
	private ReturnDocument ret;
	private boolean bypassCache;
	
	private SmofUpdateOptions() {
		upsert = false;
		validateDocuments = true;
		ret = ReturnDocument.AFTER;
	}
	
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
	
	public UpdateOptions toUpdateOptions() {
		final UpdateOptions options = new UpdateOptions();
		options.bypassDocumentValidation(!validateDocuments);
		options.upsert(upsert);
		return options;
	}
	
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
	
	public void upsert(boolean upsert) {
		this.upsert = upsert;
	}
	
	public void validateDocuments(boolean validateDocuments) {
		this.validateDocuments = validateDocuments;
	}
	
	public void setReturnDocument(ReturnDocument doc) {
		this.ret = doc;
	}
	
	public void bypassCache(boolean bypassCache) {
		this.bypassCache = bypassCache;
	}
	
	public boolean isBypassCache() {
		return bypassCache;
	}
}
