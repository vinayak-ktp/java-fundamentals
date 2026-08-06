# 05 — Strings

`String` is the most used class in Java and the one with the most surprising behaviour,
because it is immutable, pooled, and has language-level support (`+`, literals) that no other
class gets.

| File | Concept |
|---|---|
| `StringPoolAndImmutability.java` | The string pool, `==` versus `equals`, why immutability costs |
| `StringConstructors.java` | Every way to build a `String` |
| `StringMethods.java` | The full API, grouped by purpose |
| `StringBuilderAndBuffer.java` | Mutable alternatives, capacity, thread safety |

---

## Immutability

A `String`'s characters never change after construction. Every "modification" — `concat`,
`replace`, `toUpperCase`, `substring`, `trim` — returns a **new** `String` and leaves the
original untouched.

```java
String s = "Hello";
s.toUpperCase();              // result thrown away!
System.out.println(s);        // still "Hello"
s = s.toUpperCase();          // this is what you meant
```

Why the language does this:

- **Pooling becomes safe.** Sharing one `"Hello"` between unrelated pieces of code only works
  if nobody can change it.
- **Thread safety for free.** An immutable object needs no synchronisation.
- **`hashCode` can be cached.** `String` computes it once and stores it, which is why strings
  are excellent `HashMap` keys.
- **Security.** A file path or SQL fragment cannot be altered between validation and use.

### The cost

```java
String s = "";
for (int i = 0; i < 5; i++) {
    s += i;                   // "0", "01", "012", "0123", "01234"
}
```

Five iterations create five throwaway objects, and each concatenation copies all the
characters accumulated so far — `O(n²)` total work. This is the single most common Java
performance mistake. Inside a loop, use `StringBuilder`.

(Outside a loop, `a + b + c` is fine: the compiler already rewrites a single expression into
one `StringBuilder`/`StringConcatFactory` chain.)

## The string pool

Literals live in a dedicated region of the heap called the **string pool** (or string constant
pool). Identical literals are stored once and shared:

```java
String s1 = "Hello";
String s2 = "Hello";
s1 == s2                      // true — the same object
```

`new String("Aditya")` explicitly bypasses the pool and allocates a fresh object on the heap:

```java
String s3 = new String("Aditya");
String s4 = new String("Aditya");
s3 == s4                      // false — two distinct objects
s3.equals(s4)                 // true  — same content
```

So `new String(...)` is almost always a mistake: it doubles the memory and breaks the `==`
coincidence without buying anything.

### Compile-time versus runtime concatenation

```java
String s1 = "Ja" + "va";
s1 == "Java"                  // true — folded by the compiler into a literal

String hello = "Hello";
String joined = hello + " World";
joined == "Hello World"        // false — built at runtime on the heap
```

A concatenation of **constants** is evaluated at compile time and lands in the pool. As soon
as a non-final variable is involved, the result is computed at runtime and is a new heap
object. Marking `hello` as `final` would make it a constant again and flip the result to
`true` — which is exactly the kind of invisible action-at-a-distance that makes `==` on
strings untrustworthy.

### `intern()`

`s.intern()` returns the pooled instance for that content, adding it to the pool if needed:

```java
new String("Hello").intern() == "Hello"   // true
```

Useful when deduplicating a very large number of repeated strings; unnecessary otherwise.

### The rule

**Compare strings with `equals()`, never with `==`.** `==` asks "the same object?", which
happens to be true for pooled literals and false everywhere else. Use
`equalsIgnoreCase` for case-insensitive comparison, and `Objects.equals(a, b)` when either
side might be null.

## Constructors

`StringConstructors.java` covers the full set:

| Constructor | Notes |
|---|---|
| `new String()` | empty string — use `""` |
| `new String(String)` | pointless copy |
| `new String(char[])` | the array is **copied**, so later mutation of it does not affect the String |
| `new String(char[], offset, count)` | `count` is a **length**, not an end index |
| `new String(byte[], offset, count)` | decodes bytes using the platform charset — specify one explicitly in real code |
| `new String(StringBuilder / StringBuffer)` | snapshot of the current content |

The offset/count pair is worth noting because it differs from `substring(begin, end)`, which
takes two indices. `new String(arr, 0, 6)` reads six characters; `substring(0, 6)` stops
before index 6 — same answer here, different reasoning, and the difference bites with
non-zero offsets.

## The method API

Grouped as in `StringMethods.java`:

**Size** — `length()`, `isEmpty()` (length 0), `isBlank()` (empty or only whitespace, Java 11+).

**Access** — `charAt(i)`, `toCharArray()`, `indexOf`/`lastIndexOf`, `chars()` for a stream.

**Comparison** — `equals`, `equalsIgnoreCase`, and `compareTo`, which returns a negative
number, zero or a positive number for lexicographic (dictionary) ordering. `compareTo` is what
makes `String` `Comparable`, and therefore sortable and usable in a `TreeMap`. Its return
value is a *difference*, not necessarily `-1`/`0`/`1` — only the sign is contractual.

**Searching** — `contains`, `startsWith`, `endsWith`, `indexOf`, `lastIndexOf` (`-1` when
absent).

**Extraction and transformation** — `substring(begin)` and `substring(begin, end)` where
**end is exclusive**; `toUpperCase`/`toLowerCase`; `repeat(n)` (Java 11+);
`trim()` versus `strip()` — `trim` removes anything below `U+0020`, `strip` is Unicode-aware
and is the correct modern choice.

**Regex-based, and this is the trap** — `replace` takes literal text, while `replaceAll`,
`split` and `matches` take **regular expressions**. `"a.b".replace(".", "-")` gives `"a-b"`;
`"a.b".replaceAll(".", "-")` gives `"---"`, because `.` matches everything. `split` also
discards trailing empty strings unless you pass a negative limit.

**Joining and formatting** — `String.join(delimiter, parts...)`, `String.valueOf(x)` (which
handles `null` where `x.toString()` would throw), and `String.format`/`printf` with
`%s` `%d` `%f` `%n`. Prefer `%n` over `\n` for a platform-correct line separator.

## `StringBuilder` and `StringBuffer`

Both are mutable character sequences with the same API. The difference is only
synchronisation:

| | `StringBuilder` | `StringBuffer` |
|---|---|---|
| Thread safe | no | yes, every method is `synchronized` |
| Speed | faster | slower |
| Introduced | Java 5 | Java 1.0 |

**Use `StringBuilder`.** A builder is almost always a local variable, so the locking in
`StringBuffer` is pure overhead — and if a builder genuinely is shared between threads, the
per-method locking is too fine-grained to make the *sequence* of appends correct anyway.

### Mutating methods

`append` (overloaded for every type), `insert(offset, x)`, `delete(start, end)`,
`deleteCharAt(i)`, `replace(start, end, str)`, `setCharAt(i, c)`, `reverse()`. Most return
`this`, which is what makes chaining work:

```java
sb.append("a").append("b").append("c");
```

### Capacity

The internal `char[]` has a **capacity** distinct from the `length()`:

- Default capacity is 16, or `16 + str.length()` when constructed from a string.
- On overflow it grows to `(old * 2) + 2` and copies. In `StringBuilderAndBuffer.java`,
  17 characters in a default builder produce a capacity of 34.
- `ensureCapacity(n)` pre-allocates; `trimToSize()` shrinks the array to the current length.

If you know the final size, passing it to the constructor avoids every intermediate copy.
`length()` is the number of characters; `capacity()` is the room available — they are almost
never equal.

## `String` versus the alternatives

| Need | Use |
|---|---|
| A fixed value, a map key, an API parameter | `String` |
| Building text in a loop | `StringBuilder` |
| Building text shared across threads | `StringBuffer`, or better, one builder per thread |

## Running the examples

```sh
javac -d out src/05_strings/*.java
java -cp out StringPoolAndImmutability
```
