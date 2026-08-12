# 09 — Generics

Type parameters let one class or method work with many types while keeping full compile-time
checking. This is what turned Java collections from casting-heavy to type-safe.

| File | Concept |
|---|---|
| `RawTypeProblem.java` | Life before generics: `Object`, casts, `ClassCastException` |
| `GenericClasses.java` | Type parameters on a class |
| `GenericMethods.java` | Type parameters on a method, and inference |
| `BoundedTypeParameters.java` | `extends` bounds, multiple bounds |
| `ArrayCovariance.java` | Why arrays are unsafe and generics are invariant |
| `Wildcards.java` | `?`, `? extends`, `? super`, and PECS |

---

## The problem generics solve

Before Java 5, a general-purpose container held `Object`:

```java
class Box {
    private Object value;
    Object getValue() { return value; }
}

Box box = new Box(10);
String s = (String) box.getValue();   // compiles fine — fails at runtime
```

Two things went wrong: **the type information was lost**, so every read needed a cast; and the
compiler could not check that cast, so a wrong one became a runtime `ClassCastException`.

Generics move that error to compile time:

```java
Box<Integer> box = new Box<>(10);
Integer value = box.getValue();
```

The casts disappear, and a wrong one is caught while you type it rather than in production.

### Upcasting and downcasting

Related background from `RawTypeProblem.java`:

- **Upcasting** (`String` → `Object`) is always safe and implicit — every `String` *is* an
  `Object`.
- **Downcasting** (`Object` → `String`) needs an explicit cast, because it may be wrong. The
  compiler accepts your assertion; the JVM verifies it and throws `ClassCastException` if you
  were wrong.

Generics exist so that you stop writing downcasts.

## Generic classes

```java
class Box<T> {          // T is a type PARAMETER
    private T value;
    T getValue() { return value; }
}

Box<Integer> b = new Box<>(10);   // Integer is the type ARGUMENT
```

`T` is a placeholder that the caller fills in. Conventional single-letter names: `T` for type,
`E` for element, `K`/`V` for key and value, `R` for result, `U`/`S` for a second and third
type. A class may declare as many as it needs — `Pair<T, U>`.

The diamond `<>` (Java 7+) tells the compiler to infer the arguments from the target type, so
`new Box<Integer>(10)` can be written `new Box<>(10)`.

### What you cannot do, and why: type erasure

Generics are implemented by **erasure**. The compiler checks the types, then removes them —
`Box<Integer>` and `Box<String>` compile to the *same* class, with `T` replaced by `Object`
(or by its bound). This kept generics backward-compatible with pre-Java-5 bytecode, at a price:
a type parameter cannot be instantiated, used to create an array, tested with `instanceof`,
declared `static`, caught, or given a primitive type argument — none of that information
survives to runtime, so use `List<Integer>` rather than a primitive.

Erasure is also why two overloads cannot differ only in a type argument: `f(List<String>)` and
`f(List<Integer>)` erase to the same signature.

## Generic methods

A method may be generic independently of its class. The type parameters go **before the return
type**:

```java
static <T> T identity(T value)                    { return value; }
static <T, U> void printPair(T first, U second)   { ... }
```

Call sites stay clean because the compiler **infers** the arguments from the actual
parameters — `identity(23)` is understood as `identity<Integer>(23)`. Explicit syntax
(`ClassName.<String>identity(...)`) exists but is rarely needed.

Use a generic method when the type relationship is confined to one call. Use a generic class
when the type must be remembered across calls.

## Bounded type parameters

An unbounded `T` is erased to `Object`, so nothing useful can be called on it. A **bound**
narrows what may be substituted and, in exchange, unlocks that type's API:

```java
class NumberBox<T extends Number> {
    void printAsDouble() { System.out.println(value.doubleValue()); }
}
```

`doubleValue()` is available because every legal `T` is a `Number`. `NumberBox<String>` no
longer compiles.

Notes:

- `extends` is used for **both** classes and interfaces here — there is no `implements` in a
  bound.
- **Multiple bounds** use `&`, and a class bound must come first:
  `<T extends Animal & Swimmable>`. At most one class, any number of interfaces.
- A bound also changes erasure: `T` erases to `Number` rather than `Object`, which is what makes
  the method call work at the bytecode level.
- There is no lower bound (`super`) on a type *parameter* — only on wildcards.

## Arrays are covariant, generics are not

This is the conceptual heart of the module.

**Arrays are covariant:** `Dog[]` is usable as an `Animal[]`. The compiler allows it, so the
JVM must check every single store and throw at runtime when the element does not fit:

```java
Dog[] dogs = new Dog[3];
Animal[] animals = dogs;        // allowed
animals[1] = new Animal();      // compiles → ArrayStoreException at runtime
```

**Generics are invariant:** `List<Dog>` is *not* a `List<Animal>`, and the assignment is
rejected. It has to be: if a `List<Dog>` could be treated as a `List<Animal>`, anything holding
that wider reference could add a `Cat`, and the next `Dog` read out of the list would explode.

Because erasure removes the type argument, the JVM could not perform the runtime check arrays
get. So generics prevent the situation at compile time instead. Invariance is not a limitation
to work around — it is the safety property. Wildcards are the sanctioned way to regain
flexibility without losing it.

## Wildcards

`?` means "some specific type I am not naming".

### `List<?>` — unknown type

Accepts any list at all. You can read elements as `Object` and call `size()`, but you cannot
`add` anything (except `null`), because the actual element type is unknown and nothing is
guaranteed to fit.

### `List<? extends Animal>` — upper bounded

"Some subtype of `Animal`." Every element **is** an `Animal`, so reading is fully typed:

```java
for (Animal a : values) { a.eat(); }
```

Writing is rejected — the real list might be a `List<Cat>`, so even `add(new Dog())` is unsafe.
This is a **producer**: it gives you values.

### `List<? super Animal>` — lower bounded

"`Animal` or some supertype of it." Any `Animal` (or subtype) definitely fits, so writing is
allowed:

```java
values.add(new Animal());
values.add(new Labrador());
```

Reading gives back only `Object`, because the list might be a `List<Object>`. This is a
**consumer**: it accepts values.

### PECS

> **P**roducer **E**xtends, **C**onsumer **S**uper.

If the parameter is a source of data, use `? extends`. If it is a destination, use `? super`.
If it is both, use an exact type. `Collections.copy(List<? super T> dest, List<? extends T> src)`
is the canonical illustration — both halves in one signature.

### `<T>` versus `<?>`

| | Type parameter `<T>` | Wildcard `<?>` |
|---|---|---|
| Names the type | yes — reusable across the signature | no |
| Expresses relationships | `<T> void copy(List<T> a, List<T> b)` forces both to match | cannot |
| Return types | can return `T` | cannot usefully return `?` |
| Best for | implementations that need the type | parameters that only consume or produce |

Practical rule: use a wildcard for a method parameter when the body does not need to name the
type; use `<T>` when it does, or when two parameters and/or the return value must agree.

## Running the examples

```sh
javac -d out src/09_generics/*.java
java -cp out ArrayCovariance
java -cp out Wildcards
```
