# 04 — Arrays

Java's fixed-size, homogeneous, zero-indexed container — and the foundation `ArrayList`,
`HashMap` and `StringBuilder` are all built on.

| File | Concept |
|---|---|
| `SingleDimensionalArrays.java` | Declaration, allocation, indexing, `length`, defaults |
| `MultiDimensionalArrays.java` | Rectangular grids, jagged arrays, literals |

---

## An array is an object

This is the fact that explains all the rest. `new int[3]` allocates on the **heap** and
returns a reference, exactly like any other object:

- It has a member, `length` — a `final` field, not a method. (`String` uses `length()`;
  arrays use `length`. There is no good reason for the inconsistency, and everyone trips over
  it once.)
- It inherits from `Object`, so `toString()`, `hashCode()` and `equals()` exist — and are all
  identity-based, which is why `System.out.println(arr)` prints `[I@1b6d3586` rather than the
  contents. Use `Arrays.toString(arr)`.
- Copying the variable copies only the reference. Two variables then alias one array.
- It can be `null`, and dereferencing that throws `NullPointerException` — distinct from an
  array of length 0, which is a perfectly good empty array.

## Declaring and allocating

```java
int[] rollNumbers = new int[3];   // preferred
int rollNumbers[] = new int[3];   // legal, C-style, discouraged
```

Both work; the first keeps the type (`int[]`) in one piece, which reads better and matches how
the type appears everywhere else. The size is fixed **at allocation** and can be any
non-negative `int` expression — it need not be a constant, but it can never change afterwards.
Growing means allocating a bigger array and copying (`Arrays.copyOf`), which is precisely what
`ArrayList` does internally.

### Elements are initialised

Unlike locals, array elements always start at the type's default — `0`, `0.0`, NUL,
`false`, or `null` for references. `new int[2]` is genuinely `{0, 0}`, guaranteed.

An array of a reference type is an array of **references**, all null: `new String[3]` holds no
strings at all, just three empty slots.

### Array literals

```java
int[] marks = {12, 14, 56};                 // only valid in a declaration
marks = new int[]{12, 14, 56};              // needed for assignment or an argument
```

The bare-braces form is a **declaration-only** shorthand. Anywhere else — reassignment,
passing to a method — needs the `new int[]{...}` form.

## Indexing

Indices run `0` to `length - 1`. Every access is **bounds-checked at runtime**, and an
out-of-range index throws `ArrayIndexOutOfBoundsException`. That check costs a little
performance and buys the absence of buffer overflows, which is a trade Java makes everywhere.

A negative index throws the same exception — there is no Python-style wrapping.

### Iterating

```java
for (int i = 0; i < rollNumbers.length; i++)   // when you need the index
for (int number : rollNumbers)                 // when you only need the value
```

Always write `i < length`, never `i <= length`. The enhanced for loop cannot go out of bounds
at all, and cannot write to the array through the loop variable — the variable is a copy of
the element, so assigning to it changes nothing.

## Multi-dimensional arrays

Java has no true 2-D array. `int[][]` is an **array whose elements are arrays**, and that one
sentence explains every behaviour below.

### Rectangular

```java
int[][] marks = new int[3][3];   // 1 outer array + 3 inner arrays, 4 objects total
```

`marks.length` is the number of **rows** (3). `marks[row].length` is the length of *that
row*. Always use the second form for the inner bound — it is correct for jagged arrays too,
and it does not silently assume a shape.

### Jagged

Because the rows are independent objects, they may differ in length:

```java
int[][] marks = new int[3][];    // rows allocated, columns not
marks[0] = new int[1];
marks[1] = new int[2];
marks[2] = new int[3];
```

Only the **first** dimension may be omitted, and it must be given: `new int[][]` alone is not
a valid allocation. Until each row is assigned, the rows are `null` — indexing into one throws
`NullPointerException`, not `ArrayIndexOutOfBoundsException`.

This is what makes triangular structures (Pascal's triangle, adjacency lists) natural in Java,
and it is why a `for` over `marks[row].length` is not merely good style but necessary.

### Nested literals

```java
int[][] marks = {
    {12, 14, 56},
    {34, 45, 67},
    {45, 67, 78}
};
```

The rows need not be the same length here either — `{{1}, {2, 3}}` is a valid jagged literal.

### Memory layout

A C 2-D array is one contiguous block. A Java `int[3][3]` is four separate heap objects: the
outer array holds three references, each pointing at a row that could live anywhere. So
row-major traversal is cache-friendly and column-major is not, and a "2-D" array costs an
extra pointer indirection per access. For performance-critical numeric code, a single flat
`int[rows * cols]` with manual `row * cols + col` indexing is measurably faster.

## Arrays versus collections

| | Array | `ArrayList` |
|---|---|---|
| Size | fixed at allocation | grows automatically |
| Element type | primitives or references | references only (autoboxing hides this) |
| Access | `arr[i]`, bounds-checked | `list.get(i)` |
| Size member | `length` field | `size()` method |
| Covariance | covariant, checked at runtime | invariant, checked at compile time |

The **covariance** row is the significant one: `Dog[]` is usable as an `Animal[]`, which the
compiler allows and the JVM then has to police at every store, throwing
`ArrayStoreException`. Generics deliberately chose the safer rule. Both sides of that trade
are demonstrated in `09_generics/ArrayCovariance.java` and `09_generics/Wildcards.java`.

Reach for an array when the size is genuinely fixed, when you need primitives without boxing,
or when you are implementing a data structure. Reach for a collection the rest of the time.

## The `java.util.Arrays` toolbox

Not used in these two examples, but this is where the useful operations live:

- `Arrays.toString(arr)` / `Arrays.deepToString(grid)` — printable form
- `Arrays.sort(arr)` — dual-pivot quicksort for primitives, stable merge sort for objects
- `Arrays.binarySearch(arr, key)` — requires a sorted array
- `Arrays.fill(arr, value)`
- `Arrays.copyOf(arr, newLength)` / `Arrays.copyOfRange(arr, from, to)`
- `Arrays.equals(a, b)` — element-wise, unlike `a.equals(b)`
- `Arrays.asList(arr)` — a fixed-size `List` **view** backed by the array
- `Arrays.stream(arr)` — the bridge into `12_streams`

## Running the examples

```sh
javac -d out src/04_arrays/*.java
java -cp out MultiDimensionalArrays
```
