package fr.vergne.multimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <p>
 * A {@link MultiMap} allows to map a key to several values, as opposed to a
 * {@link Map} which maps a key to a single value. Functionally speaking, a
 * {@link MultiMap} is equivalent to a {@link Map} which maps keys to
 * {@link Collection}s, but with additional features to interact directly with
 * the values within these {@link Collection}s.
 * </p>
 * 
 * @author Matthieu Vergne <vergne@fbk.eu>
 * 
 * @param <Key>
 * @param <Value>
 */
public interface MultiMap<Key, Value> extends Iterable<Entry<Key, Value>> {
	/**
	 * Map a key to the provided value. If there is already values mapped to this
	 * key, the new value is added (the old ones are kept, not replaced).
	 * 
	 * @param key
	 *            the key to map
	 * @param value
	 *            the value to map to that key
	 * @return <code>true</code> if the mapping has been changed, <code>false</code>
	 *         otherwise
	 */
	public boolean add(Key key, Value value);

	/**
	 * Map a key to all the provided values. If there is already values mapped to
	 * this key, the new values are added (the old ones are kept, not replaced).
	 * 
	 * @param key
	 *            the key to map
	 * @param values
	 *            the values to map to that key
	 * @return <code>true</code> if the mapping has been changed, <code>false</code>
	 *         otherwise
	 */
	public boolean addAll(Key key, Collection<Value> values);

	/**
	 * Add set of values to their corresponding keys. All the values already mapped
	 * to these keys are kept, and the new ones are added.
	 * 
	 * @param map
	 *            the various values to map to their various keys
	 */
	default void addAll(Map<? extends Key, ? extends Collection<Value>> map) {
		for (Entry<? extends Key, ? extends Collection<Value>> entry : map.entrySet()) {
			addAll(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Remove all the current values mapped to a given key and replace them by the
	 * ones provided in argument.
	 * 
	 * @param key
	 *            the key to change
	 * @param collection
	 *            the new {@link Collection} of values to map to the key
	 * @return the previous {@link Collection} of values mapped to the key,
	 *         <code>null</code> if the key was not mapped to any value
	 */
	public Collection<Value> replaceAll(Key key, Collection<Value> collection);

	/**
	 * Map {@link Collection}s of values to their corresponding keys. All the values
	 * already mapped to these keys are forgotten.
	 * 
	 * @param map
	 *            the various values to map to their various keys
	 */
	default void replaceAll(Map<? extends Key, ? extends Collection<Value>> map) {
		for (Entry<? extends Key, ? extends Collection<Value>> entry : map.entrySet()) {
			replaceAll(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 
	 * @param key
	 *            the key to retrieve
	 * @return all the values mapped to this key
	 */
	public Collection<Value> getAll(Object key);

	/**
	 * 
	 * @return all the keys stored in this {@link MultiMap}
	 */
	public Set<Key> keySet();

	/**
	 * 
	 * @return all the {@link Collection}s stored in this {@link MultiMap}
	 */
	public Collection<Collection<Value>> collections();

	/**
	 * 
	 * @return all the mappings stored in this {@link MultiMap}
	 */
	public Set<Entry<Key, Collection<Value>>> entrySet();

	/**
	 * Unmap a specific value from a key.
	 * 
	 * @param key
	 *            the key to map
	 * @param value
	 *            the value to remove from that key
	 * @return <code>true</code> if the mapping has been changed, <code>false</code>
	 *         otherwise
	 */
	public boolean remove(Key key, Value value);

	/**
	 * Unmap all the values mapped to a given key.
	 * 
	 * @param key
	 *            the key to remove
	 * @return the values previously mapped to this key, <code>null</code> if the
	 *         key was not mapped to any value.
	 */
	public Collection<Value> remove(Object key);

	/**
	 * Unmap a collection of values from a key.
	 * 
	 * @param key
	 *            the key to map
	 * @param values
	 *            the values to remove from that key
	 * @return <code>true</code> if the mapping has been changed, <code>false</code>
	 *         otherwise
	 */
	public boolean removeAll(Key key, Collection<Value> values);

	/**
	 * Remove all the keys of this {@link MultiMap}
	 */
	public void clear();

	/**
	 * 
	 * @param key
	 *            the key to check
	 * @return <code>true</code> if the key is mapped to some values,
	 *         <code>false</code> otherwise
	 */
	public boolean containsKey(Object key);

	/**
	 * 
	 * @param key
	 *            the key to check
	 * @param value
	 *            the value to check
	 * @return <code>true</code> if the key is known and the value is actually
	 *         mapped to it
	 */
	public boolean containsCouple(Key key, Value value);

	/**
	 * <p>
	 * This method allows to know whether a key is mapped to a collection of values
	 * <i>equivalent</i> to the one provided. Equivalent here does not mean equal,
	 * but whether we could identify a key in such a state that it would be
	 * equivalent to:
	 * </p>
	 * <ol>
	 * <li>create the key from scratch</li>
	 * <li>add all the values of the collection to that key</li>
	 * </ol>
	 * <p>
	 * In other words, this method is independent from the type of collection.
	 * </p>
	 * <p>
	 * In particular, a {@link MultiMap} which uses {@link Set} instances to store
	 * the values, and thus ignoring duplicates, would be considered as equivalent
	 * to a {@link List} having the same values, including duplicates. This is
	 * because adding all the values of the {@link List}, even the duplicates, would
	 * lead to have them only once in the {@link MultiMap} because they are stored
	 * in a {@link Set}. At the opposite, if the {@link MultiMap} store values with
	 * a {@link List}, and some duplicates are present, it is not equivalent to a
	 * {@link Set}, because no matter how we add the values to the key, if we add
	 * them only once, the duplicates will still miss.
	 * </p>
	 * 
	 * @param collection
	 *            the {@link Collection} to check
	 * @return <code>true</code> if a key is mapped to an equivalent
	 *         {@link Collection}, <code>false</code> otherwise
	 */
	public boolean containsKeyMappedTo(Collection<Value> collection);

	/**
	 * 
	 * @return the number of keys of this {@link MultiMap}
	 */
	public int size();

	/**
	 * 
	 * @return <code>true</code> if no key is stored in this {@link MultiMap},
	 *         <code>false</code> otherwise
	 */
	default boolean isEmpty() {
		return !iterator().hasNext();
	}

	/**
	 * Since Java 1.8, the {@link Map} interface includes a default method
	 * {@link Map#remove(Object, Object)} which clashes with our own, for a
	 * different purpose. Consequently, we cannot extend {@link Map}, despite the
	 * high similarity between the two. Instead, we added this method to provide a
	 * way to access to this {@link MultiMap} as a regular {@link Map}. In other
	 * words, any change made to this {@link MultiMap} affects the {@link Map}
	 * instance returned by this method and vice-versa.
	 * 
	 * @return a {@link Map} backing this {@link MultiMap}
	 */
	default Map<Key, Collection<Value>> toMap() {
		return new Map<Key, Collection<Value>>() {

			@Override
			public int size() {
				return MultiMap.this.size();
			}

			@Override
			public boolean isEmpty() {
				return MultiMap.this.isEmpty();
			}

			@Override
			public boolean containsKey(Object key) {
				return MultiMap.this.containsKey(key);
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean containsValue(Object value) {
				if (value instanceof Collection) {
					return MultiMap.this.containsKeyMappedTo((Collection<Value>) value);
				} else {
					return false;
				}
			}

			@Override
			public Collection<Value> get(Object key) {
				return MultiMap.this.getAll(key);
			}

			@Override
			public Collection<Value> put(Key key, Collection<Value> collection) {
				return MultiMap.this.replaceAll(key, collection);
			}

			@Override
			public Collection<Value> remove(Object key) {
				return MultiMap.this.remove(key);
			}

			@Override
			public void putAll(Map<? extends Key, ? extends Collection<Value>> map) {
				MultiMap.this.replaceAll(map);
			}

			@Override
			public void clear() {
				MultiMap.this.clear();
			}

			@Override
			public Set<Key> keySet() {
				return MultiMap.this.keySet();
			}

			@Override
			public Collection<Collection<Value>> values() {
				return MultiMap.this.collections();
			}

			@Override
			public Set<Entry<Key, Collection<Value>>> entrySet() {
				return MultiMap.this.entrySet();
			}
		};
	}
}
