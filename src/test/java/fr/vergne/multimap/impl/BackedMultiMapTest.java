package fr.vergne.multimap.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import fr.vergne.heterogeneousmap.HeterogeneousMap;
import fr.vergne.heterogeneousmap.HeterogeneousMap.Key;
import fr.vergne.multimap.MultiMap;
import fr.vergne.multimap.MultiMapTest;

@RunWith(JUnitPlatform.class)
public class BackedMultiMapTest implements MultiMapTest<String, Integer> {

	private final Key<Integer> lastKeyIndex = new Key<>(Integer.class);
	private final Key<Integer> lastValue = new Key<>(Integer.class);

	@Override
	public MultiMap<String, Integer> createMultiMap(HeterogeneousMap context) {
		Map<String, Collection<Integer>> map = new HashMap<>();
		Supplier<Collection<Integer>> collectionSupplier = () -> new HashSet<>();
		return new BackedMultiMap<>(map, collectionSupplier);
	}

	@Override
	public String createValidKey(MultiMap<String, Integer> map, HeterogeneousMap context) {
		if (!context.containsKey(lastKeyIndex)) {
			context.put(lastKeyIndex, 0);
		} else {
			context.put(lastKeyIndex, context.get(lastKeyIndex) + 1);
		}
		return "key " + context.get(lastKeyIndex);
	}

	@Override
	public Integer createValidValue(MultiMap<String, Integer> map, String key, HeterogeneousMap context) {
		if (!context.containsKey(lastValue)) {
			context.put(lastValue, 0);
		} else {
			context.put(lastValue, context.get(lastValue) + 1);
		}
		return context.get(lastValue);
	}

	@Test
	public void testBackedMapReturnedThroughToMap() {
		Map<String, Collection<Integer>> backedMap = new HashMap<>();
		Supplier<Collection<Integer>> collectionSupplier = () -> new HashSet<>();
		BackedMultiMap<String, Integer> map = new BackedMultiMap<>(backedMap, collectionSupplier);
		assertTrue(backedMap == map.toMap());
	}

	@Test
	public void testListBasedBackedMapMaintainsDuplicates() {
		BackedMultiMap<String, Integer> map = BackedMultiMap.onLists();

		map.add("key", 0);
		map.add("key", 1);
		map.add("key", 2);
		map.add("key", 1);
		assertTrue(new LinkedList<>(map.getAll("key")).equals(Arrays.asList(0, 1, 2, 1)));
	}

	@Test
	public void testSetBasedBackedMapDoesNotMaintainDuplicates() {
		BackedMultiMap<String, Integer> map = BackedMultiMap.onSets();

		map.add("key", 0);
		map.add("key", 1);
		map.add("key", 2);
		map.add("key", 1);
		assertTrue(new LinkedList<>(map.getAll("key")).equals(Arrays.asList(0, 1, 2)));
	}
}
