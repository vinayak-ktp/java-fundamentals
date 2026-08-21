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
String s = box.getValue();   // will not compile — and that is the point
```

The casts disappear, and the mistake is caught while you type it rather than in production.

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

| Illegal | Reason |
|---|---|
| `new T()` | the constructor is not known at runtime |
| `new T[10]` | the array's runtime element type is not known |
| `x instanceof List<String>` | the type argument is gone at runtime |
| `static T field;` | a static member is per-class, and `T` is per-instance |
| `catch (MyException<T> e)` | the runtime cannot distinguish the types |
| primitive type arguments — `List<int>` | erasure needs a reference type; use `List<Integer>` |

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

**Generics are invariant:** `List<Dog>` is *not* a `List<Animal>`, and the assignment simply
does not compile. Consider why it must not:

```java
List<Dog> dogs = new ArrayList<>();
List<Animal> animals = dogs;    // if this were legal...
animals.add(new Cat());         // ...a Cat would be in a List<Dog>
Dog d = dogs.get(0);            // ...and this would explode
```

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

## Code that does not compile

This is the module where the counter-examples carry the most weight — the rules are defined as
much by what generics *refuse* as by what they allow. Each one lives as a comment in the
`.java` files with the exact `javac` error; the reasoning is here.

```java
List<Dog> dogs = new ArrayList<>();
List<Animal> animals = dogs;
// error: incompatible types: List<Dog> cannot be converted to List<Animal>
```

Invariance, and it is a feature. If the assignment were allowed, `animals.add(new Cat())` would
put a `Cat` into a `List<Dog>`, and the next `dogs.get(0)` would fail. Arrays *do* allow it and
pay for it with a runtime check on every store (`ArrayStoreException`); generics cannot even do
that, because erasure removes the element type before the program runs. So the check has to
happen at compile time, which means rejecting the assignment outright.

```java
static void readOnly(List<? extends Animal> values) {
    values.add(new Dog());
}
// error: incompatible types: Dog cannot be converted to CAP#1
```

`CAP#1` is the compiler's name for the **captured** unknown type — "whatever specific subtype of
`Animal` this list actually holds". Since it could be `List<Cat>`, no particular element is
known to fit, and even `Dog` is rejected. Hence: `? extends` is safe to read from and closed to
writes. (`null` is the one value that can be added, being a member of every reference type.)

```java
static void writeOnly(List<? super Animal> values) {
    Animal a = values.get(0);
}
// error: incompatible types: CAP#1 cannot be converted to Animal
```

The mirror image. The list holds `Animal` or some supertype, so any `Animal` can safely be
*added* — but a read might return an `Object`, so nothing narrower than `Object` is guaranteed
on the way out. Together these two errors *are* PECS: Producer Extends, Consumer Super.

```java
NumberBox<String> box = new NumberBox<>("x");
// error: type argument String is not within bounds of type-variable T

MultiBoundBox<Dog> box = new MultiBoundBox<>(new Dog());
// error: type argument Dog is not within bounds of type-variable T
```

The bound is a contract in both directions: the class gets to call `Number`'s methods, and in
exchange the caller may only supply a `Number`. The second case shows a multiple bound
(`<T extends Animal & Swimmable>`) rejecting a type that satisfies one half — `Dog` is an
`Animal` but does not implement `Swimmable`.

### The erasure family

All four of these fail for the same underlying reason — `T` does not exist at runtime:

```java
T value = new T();          // error: unexpected type
T[] array = new T[10];      // error: generic array creation
static T shared;            // error: non-static type variable T cannot be
                            //        referenced from a static context
List<int> primitives;       // error: unexpected type
```

- `new T()` — the constructor to call is not known; there is no runtime `T` to ask.
- `new T[10]` — an array carries its element type at runtime for the store check, and `T` cannot
  supply one. The workaround is `(T[]) new Object[10]` with an unchecked warning, which is
  exactly what `ArrayList` does internally.
- `static T` — a static member exists once per class, while `T` is chosen per instance. There is
  no single type it could have.
- `List<int>` — erasure replaces `T` with `Object` (or its bound), and a primitive is not a
  reference. Use `List<Integer>` and let autoboxing handle it, or a primitive-specialised stream
  when the boxing matters.

## Running the examples

```sh
javac -d out src/09_generics/*.java
java -cp out ArrayCovariance
java -cp out Wildcards
```
