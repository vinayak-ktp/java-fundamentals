# 12 — Streams and Optional

A stream is a pipeline for processing a sequence of elements declaratively — you describe *what*
you want, not the loop that gets it. `Optional` is the related idea applied to a single value
that may be absent.

| File | Concept |
|---|---|
| `StreamBasics.java` | Pipeline structure, laziness, sources, single use |
| `IntermediateOperations.java` | `filter`, `map`, `flatMap`, `sorted`, `distinct`, `limit`, `skip`, `peek` |
| `TerminalOperations.java` | `collect`, `reduce`, matching, primitive statistics |
| `CollectorsConcept.java` | `groupingBy`, `partitioningBy`, `joining`, `toMap` |
| `ParallelStreams.java` | Running a pipeline on multiple threads |
| `OptionalConcept.java` | Representing "maybe a value" in the type system |

---

## What a stream is, and is not

**A stream is not a data structure.** It stores nothing; it pulls elements from a source and
passes them through operations. Three parts:

```java
list.stream()                 // 1. source
    .filter(x -> x > 10)      // 2. intermediate operations (lazy, any number)
    .map(x -> x * 2)
    .forEach(System.out::println);   // 3. terminal operation (exactly one, runs it)
```

Four defining properties:

- **Lazy.** Intermediate operations only build up a description. Nothing executes until the
  terminal operation asks for elements — `laziness()` in `StreamBasics.java` prints "no work has
  happened yet" *after* the `map` was declared.
- **Single use.** Once consumed, a stream is spent; reusing it throws `IllegalStateException`.
  Create a new stream from the source instead.
- **Non-mutating.** The source is not modified. Operations return new streams.
- **Element-at-a-time where possible.** Elements are pulled through the whole pipeline one by
  one, not materialised into intermediate collections, so `filter().findFirst()` stops as soon as
  it has an answer.

### Sources

```java
collection.stream()              // any Collection
Stream.of("a", "b")              // fixed elements
Arrays.stream(array)             // an array
Stream.iterate(1, x -> x + 1)    // infinite — must be bounded with limit()
Stream.generate(Math::random)    // infinite
IntStream.range(0, 10)           // primitive range
Files.lines(path)                // a file, lazily
```

## Intermediate operations

| Operation | Effect | State |
|---|---|---|
| `filter(Predicate)` | keep matching elements | stateless |
| `map(Function)` | transform each element 1→1 | stateless |
| `flatMap(Function)` | transform each element 1→many, then flatten | stateless |
| `peek(Consumer)` | observe each element, pass it on | stateless |
| `limit(n)` | first `n` elements, short-circuiting | short-circuit |
| `skip(n)` | drop the first `n` | stateless |
| `distinct()` | drop duplicates (`equals`/`hashCode`) | **stateful** |
| `sorted()` | order elements | **stateful** |
| `mapToInt` / `boxed` | convert between object and primitive streams | stateless |

The **stateless/stateful** distinction is not trivia. A stateless operation handles each element
in isolation, so it parallelises cleanly and streams through with constant memory. `sorted()`
must buffer the *entire* stream before it can emit anything — so it cannot be used on an
infinite stream, and it is a barrier in a parallel pipeline. `distinct()` must remember
everything it has seen.

### `map` versus `flatMap`

`map` is one-to-one. `flatMap` is one-to-many followed by flattening:

```java
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));

nested.stream().flatMap(inner -> inner.stream())   // 1, 2, 3, 4
```

The function passed to `flatMap` must return a **`Stream`**, not a collection. Use it for nested
collections, for splitting one element into several, and for `Optional` chains (see below).

### `peek` is for debugging only

It exists to observe a pipeline mid-flight. Using it for side effects is a mistake: the number
of times it runs depends on the terminal operation, and it can be **elided entirely** when the
JVM can prove the elements are not needed (`count()` on a sized stream may skip the whole
pipeline).

## Terminal operations

| Operation | Returns |
|---|---|
| `forEach` / `forEachOrdered` | nothing |
| `toList()` (Java 16+) | an unmodifiable `List` |
| `collect(Collector)` | anything a `Collector` can build |
| `reduce` | a single combined value |
| `count()` | `long` |
| `findFirst()` / `findAny()` | `Optional` — short-circuiting |
| `anyMatch` / `allMatch` / `noneMatch` | `boolean` — short-circuiting |
| `min` / `max` | `Optional` |
| `sum` / `average` | on primitive streams only |

### `reduce`

Folds the stream into one value:

```java
list.stream().reduce(0, (a, b) -> a + b);   // with an identity → returns int
list.stream().reduce((a, b) -> a * b);      // without → returns Optional
```

Without an identity the stream may be empty, so the result is an `Optional`. The accumulator
must be **associative** for the result to be correct in parallel.

### Primitive streams

`sum`, `average`, `max` and `min` live on `IntStream`, `LongStream` and `DoubleStream`, not on
`Stream<T>`. Convert with `mapToInt`/`mapToLong`/`mapToDouble`, and back with `boxed()`:

```java
list.stream().filter(x -> x > 10).mapToInt(x -> x).average();   // OptionalDouble
```

This also avoids boxing every element, which is the other reason the primitive streams exist.
`summaryStatistics()` returns count, sum, min, average and max in one pass.

### `allMatch` on an empty stream

`allMatch` returns **`true`** for an empty stream (vacuous truth), while `anyMatch` returns
`false`. Worth knowing before it surprises you in a test.

## Collectors

`Collectors` are the recipes `collect()` uses.

| Collector | Produces |
|---|---|
| `toList()` / `toSet()` | a collection |
| `toMap(keyFn, valueFn[, mergeFn])` | a `Map` |
| `joining([sep[, prefix, suffix]])` | a `String` |
| `counting()` | `Long` |
| `summingInt` / `averagingInt` / `summarizingInt` | statistics |
| `mapping(fn, downstream)` | transform before collecting |
| `groupingBy(classifier[, downstream])` | `Map<K, List<T>>` |
| `partitioningBy(predicate[, downstream])` | `Map<Boolean, List<T>>` |

**`toMap` throws `IllegalStateException` on duplicate keys** unless you supply a merge function.
This catches people constantly, and the fix is the third argument:

```java
Collectors.toMap(String::length, s -> s, (a, b) -> a + "|" + b)
```

### `groupingBy` and the downstream collector

`groupingBy` alone gives `Map<K, List<T>>`. The second parameter — a **downstream collector** —
reshapes the values, and this composability is the whole design:

```java
groupingBy(String::length)                                        // {2=[AA, DD], 3=[BBB]}
groupingBy(String::length, Collectors.counting())                 // {2=2, 3=1}
groupingBy(String::length, Collectors.mapping(String::toLowerCase, toList()))
```

Downstream collectors nest arbitrarily deep, so multi-level grouping is just
`groupingBy(a, groupingBy(b))`.

`partitioningBy` is the special case of a boolean classifier, and it **always returns both
keys**, even when one side is empty — unlike `groupingBy`, which only creates keys it sees.

## Parallel streams

`list.parallelStream()` or `stream().parallel()` splits the source with a **`Spliterator`** and
runs the pipeline on the common `ForkJoinPool` (by default, one thread per available core minus
one).

It is not free. The requirements:

- **Enough work to be worth it.** Splitting, scheduling and merging have real overhead. A small
  list or a cheap operation is slower in parallel.
- **A splittable source.** `ArrayList` and arrays split evenly; `LinkedList` and `Stream.iterate`
  split badly.
- **Stateless, non-interfering, associative operations.** No shared mutable state, no modifying
  the source mid-stream.

`forEach` gives **no order guarantee** in parallel; `forEachOrdered` restores it and gives back
some of the speedup. `reduce` and `collect` are safe because they never mutate shared state.

Because all parallel streams share the common pool by default, one long blocking task can stall
every other parallel stream in the JVM. Measure before reaching for it; the sequential version
is usually fast enough and always simpler.

## `Optional`

A container holding either one value or nothing. Its purpose is to replace "this method returns
null sometimes" — a fact that lives only in documentation — with a **type the caller cannot
ignore**.

Think of it as a stream of at most one element: `map`, `flatMap` and `filter` behave the same
way.

### Creating

```java
Optional.of(value)            // value must be non-null, else NullPointerException
Optional.ofNullable(value)    // null becomes empty — the usual choice
Optional.empty()
```

### Reading

| Method | Behaviour |
|---|---|
| `isPresent()` / `isEmpty()` | a plain boolean check |
| `ifPresent(Consumer)` | run only when present |
| `ifPresentOrElse(Consumer, Runnable)` | both branches, Java 9+ |
| `orElse(other)` | default — **always evaluated**, even when present |
| `orElseGet(Supplier)` | lazy default — evaluated only when empty |
| `orElseThrow()` / `orElseThrow(Supplier)` | throw when empty |
| `get()` | throws `NoSuchElementException` when empty |

Prefer `orElseGet` when the default is expensive to compute, since `orElse`'s argument is
evaluated eagerly. Avoid `get()` — unchecked, it is just a `NullPointerException` with extra
steps.

### Transforming

```java
Optional.of("Aditya")
        .map(String::length)         // Optional<Integer>
        .filter(len -> len > 4)      // stays present only if the test passes
        .ifPresent(System.out::println);
```

`map` applies the function only if a value is present, and wraps the result. `flatMap` is for
when the function *itself* returns an `Optional` — without it you would get
`Optional<Optional<Address>>`:

```java
getUser()
    .flatMap(user -> user.address)   // Optional<Address>, not Optional<Optional<Address>>
    .map(address -> address.city)
    .ifPresent(System.out::println);
```

That chain replaces the three-deep nested null check, which is the clearest demonstration of
why `Optional` is worth having.

### How to use it well

- **Do** return `Optional` from a method whose result may legitimately be absent.
- **Do not** use it for fields, method parameters or collection elements. It is not
  `Serializable`, it adds an allocation, and an empty collection or a null parameter check is
  clearer.
- **Never** return `null` from a method declared to return `Optional`.
- Chain `map`/`filter`/`orElse` rather than `isPresent()` + `get()` — the latter is the
  null-check you were trying to get rid of.

## Running the examples

```sh
javac -d out src/12_streams/*.java
java -cp out CollectorsConcept
java -cp out OptionalConcept
java -cp out ParallelStreams
```
