package org.smof.collection;

import org.apache.commons.lang3.tuple.Pair;
import org.smof.element.Element;

import java.util.Stack;

public interface SmofInsertResult {
    boolean isSuccess();

    void setSuccess(boolean success);

    Stack<Pair<String, Element>> getPostInserts();

    void setPostInserts(Stack<Pair<String, Element>> postInserts);
}
