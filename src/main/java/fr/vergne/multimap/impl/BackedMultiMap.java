package fr.vergne.multimap.impl;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import fr.vergne.multimap.MultiMap;

/**
 * A {@link BackedMultiMap} is a {@link MultiMap} which implements its fetures
 * above a {@link Map} instance. In other words, any modification made to the
 * {@link Map} is reflected on the {@link BackedMultiMap} and vice-versa.
 * 
 * @author Matthieu Vergne <vergne@fbk.eu>
 * 
 * @param <Key>
 * @param <Value>
 */
public class BackedMultiMap<Key, Value> implements MultiMap<Key, Value> {

	private final Map<Key, Collection<Value>> map;
	private final Supplier<Collection<Value>> collectionSupplier;

	public BackedMultiMap(Map<Key, Collection<Value>> map, Supplier<Collection<Value>> collectionSupplier) {
		this.map = map;
		this.collectionSupplier = collectionSupplier;
	}

	@Override
	public boolean add(Key key, Value value) {
		Collection<Value> set = getContainerFor(key);
		return set.add(value);
	}

	@Override
	public boolean addAll(Key key, Collection<Value> values) {
		Collection<Value> set = getContainerFor(key);
		return set.addAll(values);
	}

	@Override
	public boolean remove(Key key, Value value) {
		Collection<Value> set = getContainerFor(key);
		return set.remove(value);
	}

	@Override
	public boolean removeAll(Key key, Collection<Value> values) {
		Collection<Value> set = getContainerFor(key);
		return set.removeAll(values);
	}

	private Collection<Value> getContainerFor(Key key) {
		if (!containsKey(key)) {
			map.put(key, collectionSupplier.get());
		} else {
			// use the already present collection
		}
		return map.get(key);
	}

	@Override
	public boolean containsCouple(Key key, Value value) {
		return containsKey(key) && map.get(key).contains(value);
	}

	@Override
	public Iterator<Entry<Key, Value>> iterator() {
		return new Iterator<Entry<Key, Value>>() {

			private Iterator<Key> keysIterator = keySet().iterator();
			private Iterator<Value> valuesIterator;
			private Key key;
			private Value value;

			@Override
			public boolean hasNext() {
				return keysIterator.hasNext() || valuesIterator != null && valuesIterator.hasNext();
			}

			@Override
			public Entry<Key, Value> next() {
				while (valuesIterator == null || !valuesIterator.hasNext()) {
					key = keysIterator.next();
					valuesIterator = BackedMultiMap.this.map.get(key).iterator();
				}
				value = valuesIterator.next();
				return new AbstractMap.SimpleImmutableEntry<Key, Value>(key, value);
			}

			@Override
			public void remove() {
				BackedMultiMap.this.remove(key, value);
			}

		};
	}

	@Override
	public Collection<Value> replaceAll(Key key, Collection<Value> collection) {
		Collection<Value> actualCollection = collectionSupplier.get();
		actualCollection.addAll(collection);
		return map.put(key, actualCollection);
	}

	@Override
	public Collection<Value> getAll(Object key) {
		return map.get(key);
	}

	@Override
	public Set<Key> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<Collection<Value>> collections() {
		return map.values();
	}

	@Override
	public Set<Entry<Key, Collection<Value>>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Collection<Value> remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsKeyMappedTo(Collection<Value> collection) {
		Collection<Value> typedCollection = collectionSupplier.get();
		typedCollection.addAll(collection);
		return map.containsValue(typedCollection);
	}

	@Override
	public int size() {
		return map.size();
	}

	/**
	 * @return the {@link Map} backed by this {@link BackedMultiMap}
	 */
	@Override
	public Map<Key, Collection<Value>> toMap() {
		return map;
	}

	public static <Key, Value> BackedMultiMap<Key, Value> onLists() {
		return new BackedMultiMap<>(new HashMap<>(), () -> new LinkedList<>());
	}

	public static <Key, Value> BackedMultiMap<Key, Value> onSets() {
		return new BackedMultiMap<>(new HashMap<>(), () -> new HashSet<>());
	}
}
