# `MultiMap` Definition

A `MultiMap` allows to map a key to several values, as opposed to a `java.util.Map` which maps, as its documentation says, a key to *at most one value*. Functionally speaking, a `MultiMap` is equivalent to a `Map` which maps each key to a `Collection` of values, but with additional features to interact directly with the values directly.

This project was first a module of a `Collection` project. It has now its own project to avoid making a lib with many, loosely related components.

# Link with `java.util.Map`

Although older versions extended `java.util.Map<Key, Collection<Value>>`, Java 1.8 has introduced the default method `Map.remove(Object, Object)` which clashes with our `MultiMap.remove(Key, Value)` method (because of type erasure, `Key` and `Value` being generics). Thus, we cannot extend `Map` directly anymore, the methods being functionally different. The `MultiMap` interface still implements as much as relevant methods from the `Map` interface, to remain close to it, but also the method `toMap()` to translate a `MultiMap<Key, Value>` into a `Map<Key, Collection<Value>>`.

# Link with the old Apache `MultiMap`

The Apache Commons Collections library has provided a [`MultiMap`](https://commons.apache.org/proper/commons-collections/javadocs/api-3.2.2/org/apache/commons/collections/MultiMap.html) interface and implementations, in order to map a key to several values. But it did so by breaking the purpose of the `Map` interface by extending `Map` while returning a `Collection` of values instead of a single value through `MultiMap.get(key)`. To do so, it needed to ignore the generics and enforce the user to cast the result of `get(key)`.

Our class was born to fix this issue: it has the same purpose than the Apache `MultiMap` but assumes that it is a proper `Map` between a key and *a collection of* values rather than a (tricked) `Map` between a key and each of its *single* values. Thus, where the Apache `MultiMap` appears as a common `Map` but with the need to make casting and other tricky things to use it fully, this `MultiMap` implements as much as possible the `Map` concept to provide a user-friendly class, without breaking the contract of `java.util.Map`, and adding new methods to properly interact with single values.

Now, the apache `MultiMap` is deprecated in favor of `MultiValuedMap`.

# Link with the Apache `MultiValuedMap`

Since Apache Commons Collections 4.1, the [`MultiValuedMap`](https://commons.apache.org/proper/commons-collections/javadocs/api-release/org/apache/commons/collections4/MultiValuedMap.html) has been introduced. Contrary to the old Apache `MultiMap`, this interface does not break anymore the `Map` purpose, but it remains confusing on several points:
- while `Map.put(key, value)` and `Map.get(key)` correspond to inverse methods (one assigns a value to the key, the other retrieves it), it is not the case anymore with `MultiValuedMap`, for which `put` write a single value while `get` returns the whole collection of values.
- while `Map.put(key, value)` replaces the old value of the key by the new one, `MultiValuedMap.put(key, value)` add it to the collection
- similarly to `Map`, although we should only be able to add properly typed keys and values to the `MultiValuedMap`, several methods, like `get()` or `remove()`, take `Object` instances as parameters instead, which seems hard to justify. It can be explained through the [uses of `equals()`](https://stackoverflow.com/a/859239/2031083), but such use seems to us more like a proof of poor design rather than useful flexibility. Consequently, we do not support such a design and restrain our API to properly typed instances, for keys as well as values, in order to enforce at best type safety.
