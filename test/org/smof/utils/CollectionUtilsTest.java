package org.smof.utils;

import org.apache.commons.collections4.Bag;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by austry on 07/10/2017.
 * https://github.com/austry
 */
@SuppressWarnings("javadoc")
public class CollectionUtilsTest {

  @Test
  public void create_ShouldCreateLinkedHashSet() throws Exception {
    Collection objects = CollectionUtils.create((Class) Set.class);
    assertThat(objects, instanceOf(LinkedHashSet.class));
  }

  @Test
  public void create_ShouldCreateArrayDeque() throws Exception {
    Collection objects = CollectionUtils.create((Class) Queue.class);
    assertThat(objects, instanceOf(ArrayDeque.class));
  }

  @Test
  public void create_ShouldCreateHashSet() throws Exception {
    Collection objects = CollectionUtils.create((Class) HashSet.class);
    assertThat(objects, instanceOf(HashSet.class));
  }

  @Test
  public void create_ShouldReturnNullOnUnsupportedCollections() throws Exception {
    Collection objects = CollectionUtils.create((Class) Bag.class);
    assertNull(objects);
  }
}
