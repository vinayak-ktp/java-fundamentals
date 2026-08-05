# 03 — Methods

How behaviour is packaged, named, overloaded and called — and what actually gets passed when
you call one.

| File | Concept |
|---|---|
| `MethodBasics.java` | Declaration, parameters, return values, the call stack |
| `MethodOverloading.java` | Several methods sharing one name |
| `VariableScope.java` | Where a name is visible and how long it lives |
| `CallByValue.java` | What Java actually passes to a method |

---

## Anatomy of a method

```java
static int multiply(int a, int b) {
    return a * b;
}
```

- **`static`** — belongs to the class, callable without an object. Non-static (instance)
  methods are covered in `06_oop`; here everything is static so `main` can call it directly.
- **Return type** — `void` means "returns nothing". A bare `return;` in a `void` method just
  exits early; it is optional at the end.
- **Parameters** — the variables declared in the signature. The values passed at the call
  site are the **arguments**. Any number is allowed, including zero.

A method with a non-`void` return type must return on **every** path, or it will not compile.
That check is syntactic, not clever: the compiler must be able to *prove* a value is
returned, which is why an `if` without an `else` at the end of a method is often rejected
even when a human can see it always returns.

### The four shapes

`MethodBasics.java` walks the grid deliberately — no input/no output, input/no output, no
input/output, input/output — because every method you ever write is one of those four.

## The call stack

Each call pushes a **frame** holding that invocation's parameters, locals and return address.
When the method returns, its frame is popped. That is why the prints in `chainedCalls()`
appear in reverse order of the calls: `levelTwo` finishes before `levelOne` can print.

```
main → levelOne → levelTwo
main ← levelOne ← levelTwo     "level two", then "level one", then "back in main"
```

Two visible consequences:

- Locals die with the frame, which is why one method cannot see another's variables.
- Recursion without a base case exhausts the stack and throws `StackOverflowError`. The stack
  is a fixed-size region, unlike the heap.

The same stack is what carries an exception upward — see `08_exception_handling`.

## Overloading

Several methods may share a name if their **parameter lists** differ in:

- the **number** of parameters — `sum(int, int)` versus `sum(int, int, int)`
- the **types** — `sum(int, int)` versus `sum(double, double)`
- the **order** of different types — `greet(String, int)` versus `greet(int, String)`

Together those form the **signature**. The **return type is not part of it**, so two methods
that differ only in what they return cannot coexist.

Which overload runs is decided **at compile time** from the static types of the arguments —
this is *static* or *compile-time* polymorphism, as opposed to overriding, which is resolved
at runtime (`06_oop/Polymorphism.java`).

### How the compiler picks

It tries three phases, in order, and stops at the first that finds a match:

1. exact match, or match by **widening** (`int` → `long` → `float` → `double`)
2. match by **boxing/unboxing** (`int` → `Integer`)
3. match by **varargs**

So with both `f(long)` and `f(Integer)` available, `f(5)` calls `f(long)` — widening beats
boxing. And `f(int...)` loses to both. Ambiguity that no phase resolves is a compile error,
which is why overloading heavily on numeric types gets unpleasant fast.

Overload for genuine convenience (`println` of every type), not to save yourself from picking
distinct names.

## Scope

Scope is *where a name is visible*; lifetime is *how long the value exists*. They usually
coincide, and the block is the unit of both:

```java
static String name = "Aditya";   // class scope: every method here sees it

public static void main(String[] args) {
    int x = 4;                   // method scope
    if (x == 4) {
        int insideBlock = 7;     // block scope — gone at the closing brace
    }
    // insideBlock is not visible here
}
```

Key rules:

- A variable is visible from its declaration to the end of its enclosing block. Not before —
  Java has no hoisting.
- **Locals have no default value.** The compiler rejects a read before assignment. Fields do
  get defaults (`06_oop/ClassesAndObjects.java`), because a field may legitimately be filled
  in later.
- Two methods can each declare `int x` with no relationship whatsoever. `printLocals()` in
  the example prints `40` while `main`'s `x` is still `4`.
- A local **shadows** a field of the same name. `this.name` reaches the field from an instance
  method; from a static method the class name does (`VariableScope.name`).
- Java forbids shadowing a local with another local in a nested block — unlike C. That
  removes a real class of confusion.

The practical guidance: declare a variable in the smallest block that needs it. A narrow
scope means fewer live names to reason about, and it lets the compiler catch more mistakes.

## Call by value — the whole story

**Java is always call by value. There is no call by reference.** The confusion comes from
what the value *is*: for an object, the value of the variable is a **reference**, and it is
that reference which gets copied.

### Primitives

```java
int x = 4;
addTen(x);          // the method gets a copy of 4
// x is still 4
```

### Objects

```java
Point p = new Point(4, 5);

addTen(p);          // the reference is copied — both point at the SAME Point
// p.x is now 14   ← the object was mutated through the copy

reassign(p);        // the copy is repointed at a new Point
// p.x is still 14 ← the caller's reference never moved
```

`addTen(Point)` and `reassign(Point)` in `CallByValue.java` are the two halves of the proof:

- The callee **can** change the object's fields, because the copied reference addresses the
  same object.
- The callee **cannot** make the caller's variable point somewhere else, because it only ever
  held a copy of the reference.

A true call-by-reference language (C++ `Point&`) would make `reassign` visible to the caller.
Java cannot express that. If a method must hand back a new object, **return it**.

### Why it matters

- Passing an object is cheap regardless of size — only a reference is copied.
- A method taking a mutable object may modify it. That is worth documenting, and worth
  avoiding: prefer returning a new value over mutating an argument.
- Immutable types (`String`, the wrappers, `06_oop/ImmutableClass.java`) sidestep the question
  entirely — there is nothing to mutate, so no caller can be surprised.

### The copy constructor

`CallByValue.java` also includes `Point(Point other)`, a constructor that clones an existing
instance. That is the building block of the **defensive copy**, the technique that makes
immutability actually hold; see `06_oop/ImmutableClass.java`.

## Running the examples

```sh
javac -d out src/03_methods/*.java
java -cp out CallByValue
```
