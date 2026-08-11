# 07 — Keywords and Memory

Two keywords that change *where* and *when* things live, plus what the JVM does with memory
once nothing points at it.

| File | Concept |
|---|---|
| `StaticKeyword.java` | Class-level members, static blocks, why `main` is static |
| `FinalKeyword.java` | Assign-once variables, sealed methods and classes |
| `CommandLineArguments.java` | `String[] args` |
| `GarbageCollection.java` | Reachability, `System.gc()`, `OutOfMemoryError` |

---

## `static`

A `static` member belongs to the **class**, not to any instance. There is exactly one copy, no
matter how many objects exist — or whether any exist at all.

```java
static String college = "IIT Guwahati";   // one copy, shared
String name;                              // one copy per object
```

### Static fields

Shared state. `Student.count++` in a constructor counts every object ever created, which no
instance field could do. Change it through the class and every instance sees the new value
immediately, because they were never holding their own copy.

Access it as `ClassName.field`. Accessing it through an instance (`s1.college`) compiles but is
misleading — it hides the fact that the write affects everyone.

### Static methods

Callable without an object: `Math.max(...)`, `Integer.parseInt(...)`. The constraint that
follows from having no instance:

- a static method **cannot** use `this`, or read instance fields, or call instance methods
- an instance method **can** freely use static members

So a static method is a good fit for a pure function of its arguments, and a poor fit for
anything that conceptually belongs to one object.

### Why `main` is static

The JVM must call `main` *before any object of your class exists*. If `main` were an instance
method, the JVM would have to construct one first — and it has no way to know which constructor
to use or what arguments to pass. `static` removes the question:

```java
// what the JVM effectively does
YourClass.main(args);

// what it would otherwise need to guess
new YourClass(???).main(args);
```

### Static initialiser blocks

```java
static { grade = 8; }
```

Runs **once**, when the class is loaded, before any object is created and before `main` if it is
in the main class. Several blocks run in source order, interleaved with static field
initialisers.

Its purpose is initialisation that needs more than one expression — a lookup table to populate,
a value to compute, a `final` static field that cannot be assigned inline. There is also an
**instance** initialiser block (no `static`), which runs on every construction, before the
constructor body; it is rarely the clearest option.

### Initialisation order, once and for all

On first use of a class:

1. static fields get their type defaults
2. static field initialisers and `static` blocks, **in source order**

Then on every `new`:

3. instance fields get their defaults
4. `super(...)` — the parent constructs completely first
5. instance field initialisers and instance blocks, in source order
6. the constructor body

Step 4 preceding step 5 is why calling an overridable method from a constructor is dangerous:
the subclass's fields are still at their defaults when its overridden method runs.

## `final`

`final` means **assign exactly once**. What that implies depends on what it is applied to:

| Applied to | Effect |
|---|---|
| local variable | cannot be reassigned |
| field | must be assigned in the declaration, an initialiser block, or **every** constructor |
| static field | must be assigned in the declaration or a `static` block |
| method parameter | cannot be reassigned in the body |
| method | cannot be overridden |
| class | cannot be extended |

### Blank finals

A `final` variable need not be initialised where it is declared:

```java
final int x;
x = 4;          // fine — assigned exactly once
```

The compiler tracks every path and rejects a second assignment, or any path that leaves it
unassigned. For a `static final` field the only legal place is a `static` block — which is why
`MathConstant.PI` in `FinalKeyword.java` is written that way.

### `final` freezes the reference, not the object

This is the point people miss:

```java
final List<String> names = new ArrayList<>();
names.add("Aditya");        // fine — the list itself is mutable
```

The variable can never be repointed, but the object it points at is free to change.
Immutability needs more than `final`; see `06_oop/ImmutableClass.java`.

### `final` methods and classes

A `final` method cannot be overridden — useful when a superclass's correctness depends on it
(a template method, or something a constructor calls). A `final` class cannot be extended at
all; `String`, `Integer` and the other wrappers are all final, which is part of what makes
their immutability trustworthy.

### `static final` — the constant

Together they form a compile-time constant: one shared, unchangeable value, conventionally
named in `SCREAMING_SNAKE_CASE`. For a primitive or `String` initialised with a literal, the
compiler inlines the value at every use site.

### Effectively final

A local that is never reassigned after initialisation is *effectively* final even without the
keyword, and only such locals may be captured by a lambda, local class or anonymous class. See
`06_oop/LocalAndAnonymousClasses.java`.

## Command-line arguments

```sh
java CommandLineArguments input.txt output.txt
```

- `args` is **never null**; with no arguments it is an empty array. Check `args.length`, do not
  null-check.
- `args[0]` is the first *argument*, not the program name — unlike C's `argv[0]`.
- Everything arrives as a `String`, so numbers need `Integer.parseInt`, which throws
  `NumberFormatException` on bad input.
- The shell splits on whitespace before Java ever sees it, so multi-word values must be quoted.

## Garbage collection

Java has no `free` and no `delete`. The garbage collector reclaims any object that is no longer
**reachable**.

### Reachability

An object is reachable if a chain of references leads to it from a **GC root** — a local
variable on any thread's stack, a static field, an active JNI reference. Unreachable objects
are garbage, whether or not they still reference each other. That is why the GC handles
reference *cycles* correctly, where naive reference counting cannot.

```java
String s = new String("collect me");
s = null;                              // now unreachable → eligible
```

"Eligible" is the strongest guarantee available. The collector runs when *it* decides to.

### `System.gc()` is a hint

It requests a collection and returns; the JVM may ignore it entirely. There is no way to force
collection, and no way to know when an object was collected. Do not build logic on it.

`finalize()` — a method that was supposed to run before collection — is deprecated for removal
and was never reliable. For cleanup, use try-with-resources and `AutoCloseable`.

### The heap, roughly

| Region | Holds |
|---|---|
| Young generation (eden + two survivor spaces) | new objects; collected often and cheaply |
| Old generation | objects that survived several young collections |
| Metaspace | class metadata (not the heap proper, and not fixed-size since Java 8) |

Most objects die young, which is what makes generational collection efficient: a young
collection only has to trace the small set of live objects, then copy them out.

The collector itself runs on **daemon threads**, so it never keeps the JVM alive
(`13_multithreading/ThreadMethods.java` covers daemon threads).

### `OutOfMemoryError`

The GC cannot reclaim anything still reachable. `exhaustTheHeap()` builds a list of 1 MB
blocks, and because the list holds every block, nothing is ever collectable:

```sh
java -Xmx64m GarbageCollection    # fails after ~60 blocks
```

Note it is an `Error`, not an `Exception` — it signals a condition an application is not
generally expected to recover from. The example catches it only to keep the demo tidy.

This is also the shape of a real memory leak in Java: not a forgotten `free`, but a collection
(a cache, a listener list, a `static` map) that keeps growing because something still references
every entry.

## Running the examples

```sh
javac -d out src/07_keywords_and_memory/*.java
java -cp out StaticKeyword
java -cp out CommandLineArguments input.txt output.txt
java -Xmx64m -cp out GarbageCollection
```
