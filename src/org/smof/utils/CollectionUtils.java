package org.smof.utils;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

@SuppressWarnings("javadoc")
public final class CollectionUtils {

	public static final <E> Collection<E> create(Class<Collection<E>> clazz) throws InstantiationException, IllegalAccessException {
		if(clazz.isInterface()) {
			if(List.class.isAssignableFrom(clazz)) {
				return Lists.newArrayList();
			}
			else if(Set.class.isAssignableFrom(clazz)) {
				return Sets.newLinkedHashSet();
			}
			else if(Queue.class.isAssignableFrom(clazz)) {
				return Queues.newArrayDeque();
			}
			return null;
		}
		return clazz.newInstance();
	}
	
}
