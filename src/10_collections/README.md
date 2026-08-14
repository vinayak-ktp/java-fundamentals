# 10 — Collections Framework

The standard library's data structures, plus the two interfaces (`Iterable`, `Comparable`) and
one strategy object (`Comparator`) that tie them together.

| File | Concept |
|---|---|
| `IterableAndIterator.java` | The iteration protocol behind the enhanced for loop |
| `CustomIterable.java` | Making your own type iterable |
| `FailFastIterator.java` | `ConcurrentModificationException` and safe removal |
| `CollectionInterfaceMethods.java` | The methods every collection has |
| `ListConcept.java` | Ordered, indexed, duplicate-allowing sequences |
| `SetConcept.java` | Duplicate-free collections and their three orderings |
| `TreeSetNavigation.java` | `SortedSet` and `NavigableSet` queries |
| `MapConcept.java` | Key–value mappings and their views |
| `TreeMapNavigation.java` | `SortedMap` and `NavigableMap` queries |
| `QueueAndDeque.java` | FIFO, double-ended, and stack usage |
| `PriorityQueueConcept.java` | A binary heap |
| `ComparableConcept.java` | A type's natural order |
| `ComparatorConcept.java` | Orderings supplied from outside |

---

## The shape of the framework

```
Iterable
└── Collection
    ├── List        ordered, indexed, duplicates allowed
    │   ├── ArrayList
    │   └── LinkedList
    ├── Set         no duplicates
    │   ├── HashSet
    │   │   └── LinkedHashSet
    │   └── TreeSet          (SortedSet → NavigableSet)
    └── Queue       ordered for processing
        ├── ArrayDeque       (Deque)
        ├── LinkedList
        └── PriorityQueue

Map              NOT a Collection
├── HashMap
│   └── LinkedHashMap
└── TreeMap                  (SortedMap → NavigableMap)
```

`Map` sits outside the `Collection` hierarchy because its unit is a *pair*, not an element. It
connects back through the three view methods (`keySet`, `values`, `entrySet`).

**Program to the interface:** declare `List<String> names = new ArrayList<>();`, so the
implementation can change without touching the rest of the code.

## `Iterable` and `Iterator`

`Collection extends Iterable`, and `Iterable` has one method: `iterator()`. An `Iterator` has
three:

| Method | Purpose |
|---|---|
| `hasNext()` | is there another element? |
| `next()` | return it and advance |
| `remove()` | delete the element the last `next()` returned |

The enhanced for loop is pure syntax sugar over this — the compiler rewrites

```java
for (String name : container) { ... }
```

into the `hasNext()`/`next()` loop. That is the whole reason implementing `Iterable` is
worthwhile: your own type gains for-each support, as `CustomIterable.java` demonstrates with an
anonymous `Iterator` holding the cursor.

`remove()` is optional in the API and unsupported on immutable collections
(`List.of(...)` throws `UnsupportedOperationException`).

## Fail-fast iteration

The `java.util` iterators track a modification count. If the collection is changed structurally
through anything other than the iterator, the next `next()` throws
**`ConcurrentModificationException`**:

```java
for (Integer value : list) {
    if (value == 3) list.remove(value);   // throws
}
```

This is a *bug detector*, not a concurrency feature — it fires on a single thread just as
readily, and it is best-effort, so it must never be relied on for correctness.

Three correct alternatives:

```java
iterator.remove();                    // explicit iterator
list.removeIf(v -> v == 3);           // Java 8+, the clearest option
// or collect into a new list
```

The `remove(int)` versus `remove(Object)` trap compounds this on a `List<Integer>`:
`list.remove(3)` removes **index 3**, while `list.remove(Integer.valueOf(3))` removes the
**value** 3.

## `Collection` — the common API

| Category | Methods |
|---|---|
| Size | `size()`, `isEmpty()` |
| Membership | `contains(Object)`, `containsAll(Collection)` |
| Modification | `add(E)`, `remove(Object)`, `clear()` |
| Bulk | `addAll` (union), `removeAll` (difference), `retainAll` (intersection) |
| Conversion | `toArray()`, `toArray(T[])` |
| Iteration | `iterator()`, `forEach(Consumer)`, `stream()` |

`contains`, `remove` and the bulk operations all rely on **`equals`** (and, for hash-based
collections, `hashCode`). A value type stored in a collection must override both, or lookups
will silently fail — see `06_oop/ObjectClassMethods.java`.

`add` returns a `boolean` that means different things by collection: a `List` always returns
`true`, while a `Set` returns `false` when the element was already present.

`toArray(new Integer[0])` is the typed overload — pass a zero-length array of the right type
and the method allocates the correct size.

## `List`

Ordered by position, indexed, duplicates allowed. Adds `get`, `set`, indexed `add`/`remove`,
`indexOf`, `lastIndexOf`, `subList` and `listIterator` on top of `Collection`.

| | `ArrayList` | `LinkedList` |
|---|---|---|
| Backing | resizable array | doubly linked nodes |
| `get(i)` | **O(1)** | O(n) |
| `add` at end | O(1) amortised | O(1) |
| insert/remove in middle | O(n) — shifting | O(1) *given the node*, O(n) to find it |
| Memory | compact | 3 references per element |
| Cache locality | good | poor |

**Use `ArrayList` unless you have a specific reason not to.** `LinkedList` wins only for heavy
queue-like access at the ends, where `ArrayDeque` usually beats it anyway. `ArrayList`'s
amortised O(1) append comes from growing by ~50% and copying; presizing via
`new ArrayList<>(expectedSize)` avoids the copies.

A `ListIterator` can walk backwards (`hasPrevious`, `previous`) and modify during iteration
(`set`, `add`) — the only safe way to insert while iterating.

`List.of(...)` and `List.copyOf(...)` return **immutable** lists: every mutator throws
`UnsupportedOperationException`. They are excellent for constants and for defensive copies, and
they are a common surprise when passed to code that expects to mutate.

## `Set`

No duplicates: `add` returns `false` instead of storing a second copy.

| | `HashSet` | `LinkedHashSet` | `TreeSet` |
|---|---|---|---|
| Backing | hash table | hash table + linked list | red-black tree |
| `add`/`contains`/`remove` | O(1) average | O(1) average | O(log n) |
| Order | none guaranteed | insertion order | sorted |
| `null` allowed | one | one | no (comparison would NPE) |
| Requires | `equals` + `hashCode` | `equals` + `hashCode` | `Comparable` or a `Comparator` |

The hash-based sets share their constructors: default, initial capacity, capacity plus load
factor, and from another collection. **Capacity and load factor only tune when the table
resizes** — they never change behaviour. The default load factor 0.75 is a reasonable
time/space trade-off.

### The `TreeSet` catch

`TreeSet` decides duplicates using `compareTo`/`compare`, **not `equals`**. Two objects that
compare as 0 collapse into one entry even if `equals` says they differ — as
`ComparableConcept.equalityInSortedSets()` shows. Keeping the two consistent is strongly
recommended.

## `NavigableSet` — `TreeSet`'s real value

Because it is a balanced BST, ordered queries cost O(log n):

| Method | Meaning |
|---|---|
| `first()` / `last()` | smallest / largest (leftmost / rightmost node) |
| `lower(e)` / `higher(e)` | strictly below / above |
| `floor(e)` / `ceiling(e)` | at or below / at or above |
| `headSet(e)` / `tailSet(e)` | view below / from `e` |
| `subSet(from, to)` | view of a range |
| `pollFirst()` / `pollLast()` | retrieve **and remove** |
| `descendingSet()` / `descendingIterator()` | reversed view |

Default inclusiveness: `headSet` and `subSet` **exclude** the upper bound, `tailSet` **includes**
the lower one — the same half-open `[from, to)` convention as `substring`. The overloads
(`headSet(80, true)`, `subSet(10, false, 80, true)`) make each end explicit.

These are **views**, not copies: they read through to the backing set, and changes are visible
in both directions.

## `Map`

| | `HashMap` | `LinkedHashMap` | `TreeMap` |
|---|---|---|---|
| `get`/`put` | O(1) average | O(1) average | O(log n) |
| Order | none | insertion (or access) order | sorted by key |
| `null` key | one allowed | one allowed | not allowed |

Core methods: `put`, `get`, `remove`, `containsKey`, `containsValue`, `size`, `isEmpty`,
`putAll`, `clear`.

`put` **returns the previous value** for that key, or `null` if the key was new — a detail that
saves a separate `get`.

### Null-safe and conditional operations

| Method | Behaviour |
|---|---|
| `getOrDefault(k, d)` | `d` when absent, no null handling needed |
| `putIfAbsent(k, v)` | only inserts when missing; returns the existing value |
| `remove(k, v)` | removes only if the value matches too |
| `replace(k, v)` / `replace(k, old, new)` | update only an existing key |
| `computeIfAbsent(k, f)` | the standard idiom for a multimap |
| `merge(k, v, f)` | the standard idiom for counting |

The distinction between `put` (always overwrites) and `putIfAbsent` (never overwrites) is
worth internalising.

### The three views

`keySet()`, `values()` and `entrySet()` are **live views**, not copies — removing from
`keySet()` removes the mapping. Iterating `entrySet()` is the efficient way to walk a map,
since it avoids a `get` per key:

```java
for (Map.Entry<Integer, String> entry : map.entrySet()) {
    entry.getKey(); entry.getValue();
}
```

`Map.of(...)` is immutable, like `List.of`.

`TreeMap` mirrors the whole `NavigableSet` API in entry form: `firstEntry`, `lastEntry`,
`lowerEntry`, `floorEntry`, `higherEntry`, `ceilingEntry`, `headMap`, `tailMap`, `subMap`,
`pollFirstEntry`, `descendingMap`.

## `Queue` and `Deque`

`Queue` deliberately provides two variants of each operation:

| Operation | Throws on failure | Returns a special value |
|---|---|---|
| insert | `add(e)` | `offer(e)` → `false` |
| remove | `remove()` | `poll()` → `null` |
| inspect | `element()` | `peek()` → `null` |

**Prefer the returning variants**, unless an empty queue genuinely is an error.

`ArrayDeque` implements `Deque` (double-ended queue), giving `offerFirst`/`offerLast`,
`peekFirst`/`peekLast`, `pollFirst`/`pollLast`, plus `push`/`pop`/`peek` for stack use.

**Use `ArrayDeque`, not `Stack`.** `java.util.Stack` extends `Vector`, is synchronised on every
operation, and iterates bottom-to-top — all historical mistakes. `ArrayDeque` is faster and has
the right semantics.

### `PriorityQueue`

A binary heap. `poll()` always returns the **smallest** element by natural order, or by the
comparator supplied at construction. A max-heap is just
`new PriorityQueue<>(Comparator.reverseOrder())`.

The critical caveat: **only the head is ordered.** Iterating or printing a `PriorityQueue` gives
heap order, not sorted order. `offer`/`poll` are O(log n); `peek` is O(1).

## Ordering: `Comparable` versus `Comparator`

### `Comparable<T>` — the natural order

Implemented by the type itself, one method:

```java
public int compareTo(Student other)
```

Return negative if `this` comes first, zero if they tie, positive if `other` comes first. Used
automatically by `Collections.sort`, `Arrays.sort`, `TreeSet` and `TreeMap`.

Write `Integer.compare(a, b)` rather than `a - b`: subtraction **overflows** for large values
and silently returns the wrong sign. Add a tie-breaker (marks, then name) so the order is total
and stable across runs.

The contract also asks that `compareTo` be consistent with `equals` — because `TreeSet` and
`TreeMap` use `compareTo` for identity, as noted above.

### `Comparator<T>` — orderings from outside

A separate strategy object, so the same type can be sorted many ways without modifying it (or
being modifiable at all). Three equivalent forms, in increasing brevity:

```java
list.sort(new SortByMarks());                              // named class — reusable
list.sort(new Comparator<Student>() { ... });              // anonymous class
list.sort((a, b) -> Integer.compare(a.rollNo, b.rollNo));  // lambda
```

And the factory methods, which are what you should actually reach for:

```java
Comparator.comparingInt((Student s) -> s.marks)
          .reversed()
          .thenComparing(s -> s.name);
```

`comparing`, `comparingInt`/`Long`/`Double`, `thenComparing`, `reversed`,
`naturalOrder`, `reverseOrder`, `nullsFirst`, `nullsLast`. These compose, read as prose, and
avoid the overflow and tie-breaking mistakes hand-written comparators make.

`Comparator` is a functional interface, which is why a lambda works — see `11_functional`.

## Choosing a collection

| Need | Use |
|---|---|
| Indexed sequence | `ArrayList` |
| Queue or stack | `ArrayDeque` |
| Uniqueness, order irrelevant | `HashSet` |
| Uniqueness, insertion order | `LinkedHashSet` |
| Uniqueness, sorted, range queries | `TreeSet` |
| Key–value lookup | `HashMap` |
| Key–value, insertion order | `LinkedHashMap` |
| Key–value, sorted, range queries | `TreeMap` |
| Always-smallest-first access | `PriorityQueue` |
| A fixed constant | `List.of` / `Set.of` / `Map.of` |
| Shared across threads | `ConcurrentHashMap`, `CopyOnWriteArrayList` (see `13_multithreading`) |

## Running the examples

```sh
javac -d out src/10_collections/*.java
java -cp out TreeSetNavigation
java -cp out ComparatorConcept
```
