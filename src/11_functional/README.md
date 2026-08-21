# 11 — Functional Programming

Java 8 made behaviour something you can pass around as a value. Lambdas, functional interfaces
and method references are the vocabulary; `12_streams` is the main place they get used.

| File | Concept |
|---|---|
| `LambdaExpressions.java` | Lambda syntax, functional interfaces, `@FunctionalInterface` |
| `BuiltInFunctionalInterfaces.java` | `Function`, `Consumer`, `Supplier`, `Predicate` and friends |
| `FunctionComposition.java` | `andThen`, `compose` |
| `PredicateComposition.java` | `and`, `or`, `negate` |
| `MethodReferences.java` | The four `::` forms |

---

## Functional interfaces

A **functional interface** has exactly **one abstract method**. That single method is what a
lambda implements — which is why the lambda needs no name: there is only one method it could
possibly be.

```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}
```

`@FunctionalInterface` is optional but worth writing: it makes the compiler *enforce* the
single-abstract-method rule, so adding a second abstract method fails at the interface instead
of at every lambda that used it.

`default` and `static` methods do not count against the limit — which is exactly how
`Predicate` can offer `and`, `or` and `negate` while remaining functional. Neither do the public
methods of `Object` (`equals`, `hashCode`, `toString`).

Familiar pre-existing examples: `Runnable`, `Comparator`, `Callable`, `ActionListener`. All of
them became lambda-compatible in Java 8 without a single change, because they already had one
abstract method.

## Lambda syntax

```java
(int a, int b) -> { return a + b; }   // fully explicit
(a, b) -> a + b                       // types inferred, expression body
x -> x * x                            // single parameter, parentheses optional
() -> System.out.println("hi")        // no parameters
```

Rules:

- **Parameter types are inferred** from the interface's method signature — the *target type*.
  That is also why a lambda cannot be assigned to `var` or to `Object`: there is nothing to
  infer from.
- An **expression body** returns its value implicitly. A **block body** `{ ... }` needs an
  explicit `return` (unless the method returns `void`).
- Parentheses are optional only for exactly one inferred parameter.

### A lambda is not an anonymous class

They solve overlapping problems but differ:

| | Lambda | Anonymous class |
|---|---|---|
| `this` | the **enclosing** instance | the anonymous instance itself |
| Own fields/state | no | yes |
| Methods implemented | exactly one | any number |
| Compiled to | `invokedynamic`, often no new class | a real `Outer$1.class` |

The `this` difference is the one that causes real confusion. In a lambda, `this` still means the
surrounding object, which is almost always what you want.

### Variable capture

A lambda may use effectively final locals — declared once, never reassigned — for the same
reason local classes can (`06_oop/LocalAndAnonymousClasses.java`): the value is copied, so a
later reassignment would silently desynchronise. Instance and static fields have no such
restriction, since they are reached through a reference.

## The built-in interfaces

`java.util.function` provides the shapes you would otherwise redeclare in every project:

| Interface | Method | Shape |
|---|---|---|
| `Function<T,R>` | `apply` | T in, R out |
| `BiFunction<T,U,R>` | `apply` | two in, one out |
| `UnaryOperator<T>` | `apply` | T in, T out (a `Function<T,T>`) |
| `BinaryOperator<T>` | `apply` | two T in, T out |
| `Consumer<T>` | `accept` | T in, nothing out |
| `BiConsumer<T,U>` | `accept` | two in, nothing out |
| `Supplier<T>` | `get` | nothing in, T out |
| `Predicate<T>` | `test` | T in, `boolean` out |
| `BiPredicate<T,U>` | `test` | two in, `boolean` out |

Two things to know beyond the table:

- **Primitive specialisations exist** — `IntFunction`, `ToIntFunction`, `IntPredicate`,
  `IntSupplier`, `IntUnaryOperator`, and the `Long`/`Double` equivalents. They exist purely to
  avoid boxing, which matters in a hot loop.
- The collection and stream APIs are built on these: `forEach` takes a `Consumer`, `removeIf`
  takes a `Predicate`, `map` takes a `Function`, `Collectors.groupingBy` takes a `Function`. Once
  you know the six shapes, the whole API reads at a glance.

## Composition

The real payoff: small functions combine into larger ones without either knowing about the
other.

### `Function`

```java
f.andThen(g).apply(x)   // g(f(x))  — f first
f.compose(g).apply(x)   // f(g(x))  — g first
```

`andThen` reads left-to-right and is usually the clearer of the two. Both return a **new**
`Function`; neither mutates the originals.

```java
Function<Integer,Integer> add2 = x -> x + 2;
Function<Integer,Integer> times3 = x -> x * 3;

add2.andThen(times3).apply(2);   // 12  → (2+2)*3
add2.compose(times3).apply(2);   // 8   → 2*3+2
```

### `Consumer`

`andThen` chains consumers, and **every consumer receives the same input** — they are side
effects run in sequence, not a pipeline:

```java
printAsIs.andThen(printUpperCase).accept("Aditya");   // prints Aditya, then ADITYA
```

### `Predicate`

`and`, `or` and `negate` mirror `&&`, `||` and `!`, and they **short-circuit** just like the
operators do:

```java
Predicate<Student> passed  = s -> s.marks >= 40;
Predicate<Student> isAdult = s -> s.age >= 18;
Predicate<Student> eligible = passed.and(isAdult);
```

That last line is the point of the whole feature: the business rule is named, readable and
testable, and each condition is defined exactly once. Compare it to a compound boolean buried
inside an `if`.

`Predicate` also offers the static `Predicate.not(p)` (Java 11+) and `Predicate.isEqual(target)`.

## Method references

When a lambda does nothing but call an existing method, `::` says so more directly:

| Form | Example | Equivalent lambda |
|---|---|---|
| `Class::staticMethod` | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| `instance::method` | `System.out::println` | `x -> System.out.println(x)` |
| `Class::instanceMethod` | `String::toUpperCase` | `s -> s.toUpperCase()` |
| `Class::new` | `ArrayList::new` | `() -> new ArrayList<>()` |

The third form is the one that needs a moment: it is **unbound**, so the function's first
argument becomes the *receiver*. `String::toUpperCase` is therefore a
`Function<String, String>`, and `String::startsWith` is a `BiFunction<String, String, Boolean>`
where the first argument is the string and the second is the prefix.

Method references cannot express argument reordering, extra arguments or any logic — the moment
you need those, write the lambda. Overload resolution still applies, so an ambiguous reference
is a compile error.

## Where this leads

Passing behaviour as a value is what makes the following possible:

- `list.sort(comparator)` — the ordering is a parameter (`10_collections`)
- `list.removeIf(predicate)` — the condition is a parameter
- `stream.map(fn).filter(p).collect(...)` — a whole pipeline of parameters (`12_streams`)
- `new Thread(runnable)` — the task is a parameter (`13_multithreading`)
- `Optional.map(fn)` — apply only if a value is present

## Code that does not compile

Counter-examples live as comments in `LambdaExpressions.java`, each with the exact `javac` error.
The reasoning is here.

```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
    int other(int a);
}
// error: Unexpected @FunctionalInterface annotation
//        Calculator is not a functional interface
```

This is the annotation earning its keep. Without it the interface compiles happily, and the
failure lands instead on **every lambda that used it** — as a confusing "target type is not a
functional interface" error at a call site nobody was editing. With it, the mistake is reported
at the interface itself, where it was made.

`default`, `static` and `private` methods do not count toward the limit, which is how `Predicate`
offers `and`, `or` and `negate` while remaining functional. Neither do the public methods of
`Object`.

```java
var add = (a, b) -> a + b;
// error: cannot infer type for local variable add
```

A lambda has no type of its own. It acquires one from the **target type** — the variable,
parameter or return type it is assigned to — and that is also where the parameter types come
from. `var` asks the compiler to infer the variable's type from the expression, and the
expression is asking to infer its type from the variable: there is nothing to anchor either
side.

The same reasoning rules out `Object o = () -> ...` (`Object` is not a functional interface) and
explains why a lambda cannot be assigned to two unrelated interfaces without a cast.

## Running the examples

```sh
javac -d out src/11_functional/*.java
java -cp out FunctionComposition
java -cp out PredicateComposition
```
