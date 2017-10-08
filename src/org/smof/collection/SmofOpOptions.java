package org.smof.collection;

import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;

public interface SmofOpOptions {
    FindOneAndReplaceOptions toFindOneAndReplace();

    UpdateOptions toUpdateOptions();

    FindOneAndUpdateOptions toFindOneAndUpdateOptions();

    void upsert(boolean upsert);

    void validateDocuments(boolean validateDocuments);

    void setReturnDocument(ReturnDocument doc);

    void bypassCache(boolean bypassCache);

    boolean isBypassCache();

    @Override
    String toString();
}
