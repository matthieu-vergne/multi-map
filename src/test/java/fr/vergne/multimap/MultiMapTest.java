package fr.vergne.multimap;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.vergne.heterogeneousmap.HeterogeneousMap;

public interface MultiMapTest<Key, Value> {

	public MultiMap<Key, Value> createMultiMap(HeterogeneousMap context);

	public Key createValidKey(MultiMap<Key, Value> map, HeterogeneousMap context);

	public Value createValidValue(MultiMap<Key, Value> map, Key key, HeterogeneousMap context);

	@Test
	default void testCreateNotNullMap() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);
		assertNotNull(map);
	}

	@Test
	default void testCreateDifferentValidKeys() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);
		/*
		 * We use a LinkedList instead of a HashSet because we need to test directly
		 * whether or not the items are equal. Asking whether an item is in the set
		 * might not be reliable, because it relies on hashcode(), which might not be
		 * properly overridden. Instead, we use a list that we can browse quickly and do
		 * the check manually.
		 */
		List<Key> set = new LinkedList<>();
		for (int i = 0; i < 1000; i++) {
			Key key = createValidKey(map, context);
			for (Key storedKey : set) {
				if (storedKey.equals(key)) {
					fail("Generated equal keys: " + storedKey + " equals " + key);
				} else {
					continue;
				}
			}
			set.add(key);
		}
	}

	@Test
	default void testAddedValuesRetrievedThroughGet() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value value1 = createValidValue(map, key, context);
		Value value2 = createValidValue(map, key, context);
		Value value3 = createValidValue(map, key, context);

		map.add(key, value1);
		assertTrue(map.getAll(key).contains(value1));

		map.add(key, value3);
		assertTrue(map.getAll(key).contains(value1));
		assertTrue(map.getAll(key).contains(value3));

		map.add(key, value2);
		assertTrue(map.getAll(key).contains(value1));
		assertTrue(map.getAll(key).contains(value2));
		assertTrue(map.getAll(key).contains(value3));
	}

	@Test
	default void testMassivelyAddedValuesRetrievedThroughGet() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value value1 = createValidValue(map, key, context);
		Value value2 = createValidValue(map, key, context);
		Value value3 = createValidValue(map, key, context);

		map.addAll(key, Arrays.asList(value1, value2, value3));
		assertTrue(map.getAll(key).contains(value1));
		assertTrue(map.getAll(key).contains(value2));
		assertTrue(map.getAll(key).contains(value3));
	}

	@Test
	default void testRemovedValuesNotRetrievedThroughGet() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value value1 = createValidValue(map, key, context);
		Value value2 = createValidValue(map, key, context);
		Value value3 = createValidValue(map, key, context);
		map.addAll(key, Arrays.asList(value1, value2, value3));

		map.remove(key, value1);
		assertFalse(map.getAll(key).contains(value1));

		map.remove(key, value3);
		assertFalse(map.getAll(key).contains(value1));
		assertFalse(map.getAll(key).contains(value3));

		map.remove(key, value2);
		assertFalse(map.getAll(key).contains(value1));
		assertFalse(map.getAll(key).contains(value2));
		assertFalse(map.getAll(key).contains(value3));
	}

	@Test
	default void testMassivelyRemovedValuesNotRetrievedThroughGet() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value value1 = createValidValue(map, key, context);
		Value value2 = createValidValue(map, key, context);
		Value value3 = createValidValue(map, key, context);
		map.addAll(key, Arrays.asList(value1, value2, value3));

		map.removeAll(key, Arrays.asList(value1, value3));
		assertFalse(map.getAll(key).contains(value1));
		assertFalse(map.getAll(key).contains(value3));
	}

	@Test
	default void testSizeIncreasesWithNewKeys() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		assertEquals(0, map.size());
		map.add(createValidKey(map, context), createValidValue(map, createValidKey(map, context), context));
		assertEquals(1, map.size());
		map.add(createValidKey(map, context), createValidValue(map, createValidKey(map, context), context));
		assertEquals(2, map.size());
		map.add(createValidKey(map, context), createValidValue(map, createValidKey(map, context), context));
		assertEquals(3, map.size());
	}

	@Test
	default void testSizeDoesNotIncreaseWithExistingKeys() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key1 = createValidKey(map, context);
		Key key2 = createValidKey(map, context);
		Key key3 = createValidKey(map, context);
		map.add(key1, createValidValue(map, key1, context));
		map.add(key2, createValidValue(map, key2, context));
		map.add(key3, createValidValue(map, key3, context));

		assertEquals(3, map.size());
		map.add(key1, createValidValue(map, key1, context));
		assertEquals(3, map.size());
		map.add(key2, createValidValue(map, key2, context));
		assertEquals(3, map.size());
		map.add(key3, createValidValue(map, key3, context));
		assertEquals(3, map.size());
	}

	@Test
	default void testSizeDecreasesWithRemainingKeys() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key1 = createValidKey(map, context);
		Key key2 = createValidKey(map, context);
		Key key3 = createValidKey(map, context);
		map.add(key1, createValidValue(map, key1, context));
		map.add(key2, createValidValue(map, key2, context));
		map.add(key3, createValidValue(map, key3, context));

		assertEquals(3, map.size());
		map.remove(key2);
		assertEquals(2, map.size());
		map.remove(key1);
		assertEquals(1, map.size());
		map.remove(key3);
		assertEquals(0, map.size());
	}

	@Test
	default void testSizeDoesNotDecreaseWithUnknownKeys() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key1 = createValidKey(map, context);
		Key key2 = createValidKey(map, context);
		Key key3 = createValidKey(map, context);
		map.add(key1, createValidValue(map, key1, context));
		map.add(key2, createValidValue(map, key2, context));
		map.add(key3, createValidValue(map, key3, context));

		assertEquals(3, map.size());
		map.remove(createValidKey(map, context));
		assertEquals(3, map.size());
		map.remove(createValidKey(map, context));
		assertEquals(3, map.size());
		map.remove(createValidKey(map, context));
		assertEquals(3, map.size());
	}

	@Test
	default void testClearRemovesAllKeys() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key1 = createValidKey(map, context);
		map.add(key1, createValidValue(map, key1, context));
		map.add(key1, createValidValue(map, key1, context));
		map.add(key1, createValidValue(map, key1, context));
		Key key2 = createValidKey(map, context);
		map.add(key2, createValidValue(map, key2, context));
		map.add(key2, createValidValue(map, key2, context));
		map.add(key2, createValidValue(map, key2, context));
		Key key3 = createValidKey(map, context);
		map.add(key3, createValidValue(map, key3, context));
		map.add(key3, createValidValue(map, key3, context));
		map.add(key3, createValidValue(map, key3, context));

		map.clear();
		assertEquals(0, map.size());
	}

	@Test
	default void testContainsAddedKeys() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key1 = createValidKey(map, context);
		Key key2 = createValidKey(map, context);
		Key key3 = createValidKey(map, context);

		map.add(key1, createValidValue(map, key1, context));
		assertTrue(map.containsKey(key1));

		map.add(key3, createValidValue(map, key3, context));
		assertTrue(map.containsKey(key1));
		assertTrue(map.containsKey(key3));

		map.add(key2, createValidValue(map, key2, context));
		assertTrue(map.containsKey(key1));
		assertTrue(map.containsKey(key2));
		assertTrue(map.containsKey(key3));
	}

	@Test
	default void testDoesNotContainRemovedKeys() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key1 = createValidKey(map, context);
		Key key2 = createValidKey(map, context);
		Key key3 = createValidKey(map, context);
		map.add(key1, createValidValue(map, key1, context));
		map.add(key3, createValidValue(map, key3, context));
		map.add(key2, createValidValue(map, key2, context));

		map.remove(key2);
		assertFalse(map.containsKey(key2));

		map.remove(key1);
		assertFalse(map.containsKey(key1));
		assertFalse(map.containsKey(key2));

		map.remove(key3);
		assertFalse(map.containsKey(key1));
		assertFalse(map.containsKey(key2));
		assertFalse(map.containsKey(key3));
	}

	@Test
	default void testContainsAddedCouples() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key1 = createValidKey(map, context);
		Value v11 = createValidValue(map, key1, context);
		Value v12 = createValidValue(map, key1, context);
		Value v13 = createValidValue(map, key1, context);
		map.addAll(key1, Arrays.asList(v11, v12, v13));

		Key key2 = createValidKey(map, context);
		Value v21 = createValidValue(map, key2, context);
		Value v22 = createValidValue(map, key2, context);
		Value v23 = createValidValue(map, key2, context);
		map.addAll(key2, Arrays.asList(v21, v22, v23));

		Key key3 = createValidKey(map, context);
		Value v31 = createValidValue(map, key3, context);
		Value v32 = createValidValue(map, key3, context);
		Value v33 = createValidValue(map, key3, context);
		map.addAll(key3, Arrays.asList(v31, v32, v33));

		assertTrue(map.containsCouple(key1, v11));
		assertTrue(map.containsCouple(key1, v12));
		assertTrue(map.containsCouple(key1, v13));
		assertTrue(map.containsCouple(key2, v21));
		assertTrue(map.containsCouple(key2, v22));
		assertTrue(map.containsCouple(key2, v23));
		assertTrue(map.containsCouple(key3, v31));
		assertTrue(map.containsCouple(key3, v32));
		assertTrue(map.containsCouple(key3, v33));
	}

	@Test
	default void testDoesNotContainCoupleWithUnknownKey() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value value = createValidValue(map, key, context);

		assertFalse(map.containsCouple(key, value));
	}

	@Test
	default void testDoesNotContainCoupleWithUnknownValue() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		map.add(key, createValidValue(map, key, context));
		Value value = createValidValue(map, key, context);

		assertFalse(map.containsCouple(key, value));
	}

	@Test
	default void testDoesNotContainCoupleWithRemovedValue() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value value = createValidValue(map, key, context);
		map.add(key, value);
		map.remove(key, value);

		assertFalse(map.containsCouple(key, value));
	}

	@Test
	default void testDoesNotContainCoupleWithRemovedKey() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value value = createValidValue(map, key, context);
		map.add(key, value);
		map.remove(key);

		assertFalse(map.containsCouple(key, value));
	}

	@Test
	default void testContainsCollectionIfFitsExactly() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value v21 = createValidValue(map, key, context);
		Value v22 = createValidValue(map, key, context);
		Value v23 = createValidValue(map, key, context);
		map.addAll(key, Arrays.asList(v21, v22, v23));

		Collection<Value> reference = map.getAll(key);
		map.remove(key);
		map.addAll(key, Arrays.asList(v21, v22, v23));

		assertTrue(map.containsKeyMappedTo(reference));
	}

	@Test
	default void testContainsCollectionIndependentlyOfCollectionType() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value v21 = createValidValue(map, key, context);
		Value v22 = createValidValue(map, key, context);
		Value v23 = createValidValue(map, key, context);
		map.addAll(key, Arrays.asList(v21, v22, v23));

		assertTrue(map.containsKeyMappedTo(Arrays.asList(v21, v22, v23)));
		assertTrue(map.containsKeyMappedTo(new HashSet<>(Arrays.asList(v21, v22, v23))));
	}

	@Test
	default void testDoesNotContainIncompleteCollection() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value v1 = createValidValue(map, key, context);
		Value v2 = createValidValue(map, key, context);
		Value v3 = createValidValue(map, key, context);
		map.addAll(key, Arrays.asList(v1, v2, v3));

		assertFalse(map.containsKeyMappedTo(Arrays.asList(v1, v2)));
	}

	@Test
	default void testDoesNotContainBiggerCollection() {
		HeterogeneousMap context = new HeterogeneousMap();
		MultiMap<Key, Value> map = createMultiMap(context);

		Key key = createValidKey(map, context);
		Value v1 = createValidValue(map, key, context);
		Value v2 = createValidValue(map, key, context);
		Value v3 = createValidValue(map, key, context);
		map.addAll(key, Arrays.asList(v1, v2, v3));
		Value v4 = createValidValue(map, key, context);

		assertFalse(map.containsKeyMappedTo(Arrays.asList(v1, v2, v3, v4)));
	}
}
