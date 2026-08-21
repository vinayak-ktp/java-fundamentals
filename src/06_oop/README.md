# 06 — Object-Oriented Programming

The largest module, because this is what Java *is*. Everything here builds on one idea: bundle
state with the behaviour that operates on it, then let types stand in for one another.

| File | Concept |
|---|---|
| `ClassesAndObjects.java` | Classes, objects, instance state and behaviour |
| `Constructors.java` | Initialisation, the implicit no-arg constructor |
| `ConstructorChaining.java` | `this(...)` delegation between constructors |
| `ThisAndSuper.java` | `this` and `super` for fields, methods and constructors |
| `Encapsulation.java` | Private state, getters, setters, validation |
| `Inheritance.java` | `extends`, and the forms of inheritance Java allows |
| `Polymorphism.java` | One reference type, many runtime behaviours |
| `OverridingRules.java` | What can and cannot be overridden |
| `AbstractClasses.java` | Partial implementations meant to be extended |
| `InterfacesConcept.java` | Pure contracts, multiple inheritance of type |
| `InterfaceMethods.java` | `default`, `static` and `private` interface methods |
| `DiamondProblem.java` | Conflicting defaults, and the resolution priority rule |
| `ObjectClassMethods.java` | `toString`, `equals`, `hashCode`, `getClass`, `clone` |
| `EnumConcept.java` | Type-safe constant sets with state and behaviour |
| `NestedClasses.java` | Static nested and inner classes |
| `LocalAndAnonymousClasses.java` | Classes declared inside methods and expressions |
| `ImmutableClass.java` | Building a type that cannot change, defensive copies |
| `WrapperClassesAndAutoboxing.java` | Primitives as objects, the caching trap |
| `packages/` | Namespacing, and resolving a name collision |

---

## Classes and objects

A **class** is a blueprint; an **object** is an instance of it. The class declares:

- **instance variables** (fields) — the state each object carries
- **instance methods** — the behaviour that acts on that state

```java
Student s1 = new Student();
```

Three things happen in that line: `new Student()` allocates on the heap and runs a
constructor, the constructor returns a reference, and `s1` stores that reference. `s1` is
*not* the object; it is a variable holding a pointer to it.

**Instance fields get default values** (`0`, `0.0`, `false`, `null`) — unlike locals, which
must be assigned before use. A field can legitimately be filled in later; an unassigned local
is nearly always a bug, so the compiler treats them differently on purpose.

## Constructors

A constructor has the class's name, no return type, and runs immediately after allocation.

**If you declare no constructor, the compiler supplies a no-arg one.** The moment you declare
*any* constructor, that gift disappears — which is why `Constructors.java` writes `Student()`
out explicitly. Code that used to compile (`new Student()`) breaks the day someone adds a
parameterised constructor, and this is why.

### Chaining with `this(...)`

```java
Student() {
    this("Unknown");                 // delegate to another constructor
    System.out.println("first");
}
```

Rules:

- `this(...)` must be the **first statement**, so a constructor can delegate exactly once.
- Delegation cannot be circular — the compiler detects the cycle.
- The chain unwinds inside-out: in `ConstructorChaining.java` the *fifth* constructor prints
  first, then fourth, third, second, first.

The pattern lets every constructor funnel into one place that does the real work, so
validation and field assignment are written once.

### `this` and `super`

`this` is a reference to the current object. It serves three purposes:

1. **Disambiguation** — `this.name = name` when a parameter shadows a field. Without it,
   `name = name` assigns the parameter to itself, a silent no-op the compiler will not flag.
2. **Delegation** — `this(...)` as above.
3. **Passing the current object** — `register(this)`.

`super` reaches the parent:

1. `super(...)` calls a parent constructor and, like `this(...)`, must come first.
2. `super.method()` calls the parent's version of an overridden method — the only way to reach
   it, and the standard way to *extend* rather than *replace* behaviour.

**Every constructor calls a parent constructor.** If you do not write `super(...)` or
`this(...)`, the compiler inserts `super()`. So a parent with only a parameterised constructor
forces every child to call `super(...)` explicitly. Construction always runs top-down:
`Object` first, then each subclass.

## Encapsulation

Make fields `private`; expose behaviour instead of state.

```java
private double balance;
public void withdraw(int amount) {
    if (amount > balance) { /* reject */ }
    balance -= amount;
}
```

The point is not the getter/setter ceremony — it is that **the class controls every change to
its own state**, so an invariant like "balance is never negative" can actually be guaranteed.
Public fields make that impossible: any code anywhere can break it.

Access modifiers, narrowest first:

| Modifier | Same class | Same package | Subclass (other package) | Everywhere |
|---|---|---|---|---|
| `private` | ✔ | | | |
| *(none)* — package-private | ✔ | ✔ | | |
| `protected` | ✔ | ✔ | ✔ | |
| `public` | ✔ | ✔ | ✔ | ✔ |

Default to `private` and widen only when there is a reason. Note that `protected` also grants
package access, which surprises people, and that a setter's real value is being a place to put
**validation** the caller cannot skip.

## Inheritance

`class Child extends Parent` gives the child every non-private member of the parent, plus
whatever it adds. Java supports:

- **single** — one child, one parent
- **multi-level** — `Student` → `EngineeringStudent` → `CseStudent`
- **hierarchical** — several children of one parent

and **not multiple inheritance of classes**: a class has exactly one superclass. That
restriction avoids the ambiguity of two parents supplying the same state; interfaces provide
multiple inheritance of *type* without that problem.

The relationship is one-directional. A parent reference cannot see child members — which is
what the commented-out `student.attendLab()` in `Inheritance.java` demonstrates.

Prefer **composition** ("has-a") over inheritance ("is-a") unless the subtype genuinely
substitutes for its parent everywhere. Inheritance is the tightest coupling the language
offers.

## Polymorphism

"Many forms": one reference type, many possible runtime types.

```java
Payment payment = new DebitCard();
payment.pay();                       // DebitCard.pay() runs
```

The split to internalise:

- The **reference type** decides *what you may call* — checked at compile time.
- The **object's actual type** decides *which implementation runs* — resolved at runtime, by
  the JVM, via dynamic dispatch.

That is why a method can accept `Payment` and work with every payment type ever written,
including ones that did not exist when it was compiled.

**Compile-time polymorphism** (overloading, `03_methods`) is a different mechanism: it picks
between distinct methods from the static argument types. **Runtime polymorphism** (overriding)
picks between implementations of the *same* signature from the actual object.

### What cannot be overridden

| Member | Why |
|---|---|
| `static` | belongs to the class; a child method with the same name **hides** it, and hiding is resolved by reference type |
| `private` | not visible to the child, so a same-named method is unrelated |
| `final` | explicitly closed; a `final` class cannot be extended at all |
| **fields** | never polymorphic — resolved at compile time by reference type |

That last row is the one worth remembering. In `OverridingRules.java`:

```java
Parent p = new Child();
p.value        // 10 — Parent's field, chosen by the reference type
p.getValue()   // 20 — Child's method, chosen by the object
```

Two conclusions: keep fields private, and put behaviour in methods.

Also: `@Override` is optional but always worth writing. It turns a typo'd signature — which
would silently become a *new, never-called method* — into a compile error.

## Abstract classes

```java
abstract class Animal {
    abstract void makeSound();       // no body: subclasses must supply one
    void sleep() { ... }             // shared implementation
}
```

An abstract class cannot be instantiated and exists to be extended. It answers the common
interview questions like this:

| Question | Answer |
|---|---|
| Can it have constructors? | **Yes** — subclass construction calls them |
| Can it be `final`? | **No** — that would make it unusable |
| Can it have static members? | Yes |
| Can it have `private` methods? | Yes, non-abstract ones |
| Can it have `final` methods? | Yes, non-abstract ones |
| Can it have *no* abstract methods? | Yes — it just becomes non-instantiable |
| Can an abstract method be `private`, `static` or `final`? | No — each contradicts "must be overridden" |

## Interfaces

An interface is a contract. Every member is implicitly `public`; every field is implicitly
`public static final`; abstract methods have no body.

A class may `implement` any number of interfaces, and an interface may `extend` several
others. This is Java's **multiple inheritance of type**.

### Methods with bodies

| Kind | Since | Purpose |
|---|---|---|
| `abstract` | 1.0 | the contract |
| `default` | 8 | an inheritable implementation |
| `static` | 8 | a helper tied to the interface, **not inherited** — call it as `Vehicle.brake()` |
| `private` | 9 | shared code between `default` methods |

`default` methods exist so the JDK could add `Collection.stream()` and `List.sort()` without
breaking every existing implementation on the planet. They are an evolution tool, not a
licence to put logic in interfaces.

### Interface versus abstract class

| | Interface | Abstract class |
|---|---|---|
| Multiple inheritance | yes | no |
| Instance state | no (constants only) | yes |
| Constructors | no | yes |
| Method bodies | `default`, `static`, `private` | any |
| Access modifiers | public (and private helpers) | any |

Rule of thumb: an interface defines *what a type can do*; an abstract class shares *how a
family of types does it*. Reach for the interface first, since it leaves the single `extends`
slot free.

### The diamond, and resolution priority

Two interfaces can both supply a `default` method with the same signature:

```
    A
   / \
  B   C     both default fun()
   \ /
    D       must override fun(), or it will not compile
```

Java refuses to guess. `D` must override, and may delegate explicitly with `B.super.fun()`.

When a class *and* an interface both provide a method, the priority is fixed:

1. the class's own method
2. an inherited superclass method
3. an interface `default` method

**A concrete class always beats a default method.** So a superclass method silently wins over
an interface default — `ClassBeatsInterface` in `DiamondProblem.java`.

## `Object` — the universal parent

Every class extends `Object` implicitly. Five members matter:

### `toString()`

The default is `ClassName@hexHashCode`, which is useless in logs. Override it.

### `equals()` and `hashCode()`

These are a **pair**, and their contract is:

> Equal objects **must** have equal hash codes.

Break it and hash-based collections break: `HashSet` will store two "equal" objects, and
`HashMap.get(key)` will fail to find a mapping that is provably present, because the lookup
starts from the hash bucket.

The canonical `equals`, as written in `ObjectClassMethods.java`:

1. `if (this == obj) return true;` — the cheap identity shortcut
2. `if (obj == null || obj.getClass() != this.getClass()) return false;` — null and type check
   in one line, which also prevents the `ClassCastException` on the next line
3. cast, then compare fields — with `equals` (or `Objects.equals` for nullable fields), never
   `==`

For `hashCode`, `Objects.hash(a, b, c)` is the one-liner. The classic manual version — start
at 17, multiply by 31 and add each field — is in a comment there; `31` is used because
`31 * x` compiles to a shift and subtract.

`equals` must also be reflexive, symmetric, transitive and consistent. That is why comparing
with `instanceof` instead of `getClass()` is subtly dangerous with subclasses: symmetry breaks.

### `getClass()`

Returns the runtime `Class` object — the entry point to reflection, and how the `equals`
type-check above works.

### `clone()`

`Object.clone()` makes a **shallow copy**: fields are copied field-by-field, so referenced
objects are *shared*, not duplicated. The class must implement the marker interface
`Cloneable` or `clone()` throws `CloneNotSupportedException`. The API is widely considered a
mistake — a copy constructor (`03_methods/CallByValue.java`) or a static factory is clearer
and type-safe.

### `instanceof`

True for the exact class **and any subclass**, so `dog instanceof Animal` and
`dog instanceof Object` are both true. `null instanceof Anything` is always false, which makes
it a convenient implicit null check.

## Enums

Before enums, constants were `public static final` fields — with three real problems:

1. **No type safety.** A method taking `int status` accepts `42`, or `Role.ADMIN` where a
   `PaymentStatus` was meant.
2. **Poor diagnostics.** Printing the constant gives `1` or `"Success"`, not its name.
3. **No grouping.** Nothing ties the related values together as a type.

An `enum` is a class whose instances are a fixed, compile-time set:

```java
enum PaymentStatus { SUCCESS, FAILED, PENDING }
```

Each constant is a singleton object. They can carry state, take constructor arguments, and even
override a method per constant:

```java
enum Direction {
    NORTH(0)   { public void move() { ... } },
    SOUTH(180) { public void move() { ... } };

    private final int degree;
    Direction(int degree) { this.degree = degree; }   // implicitly private
    public abstract void move();
}
```

That per-constant body is an anonymous subclass, and it replaces a `switch` over the enum with
polymorphism — adding a constant then forces you to supply the behaviour.

Built-in members:

| Member | Meaning |
|---|---|
| `values()` | array of all constants, in declaration order |
| `valueOf(String)` | constant by exact name; throws `IllegalArgumentException` otherwise |
| `name()` | the declared name — `final`, cannot be changed |
| `ordinal()` | zero-based declaration position — **never persist this**, it shifts when constants are reordered |
| `toString()` | defaults to `name()`, and is safe to override for display |

Enums are also the correct way to write a singleton, and are usable in `switch` and
`EnumMap`/`EnumSet`.

## Nested, inner, local and anonymous classes

Four distinct things that all live inside another class:

### Static nested class

`static class Helper { }` — no link to an outer instance, so it can have static members and is
constructed as `Outer.Helper h = new Outer.Helper()`. It can read the outer class's **static**
members directly and needs a reference for instance members.

Uses: helper classes, the Builder pattern, request/response DTOs, and any grouping that wants
to be package-private to everyone but its owner. Making it `private static` (as
`BankAccount.InterestCalculator` does) hides an implementation detail completely.

### Inner (non-static) class

`class Inner { }` — tied to an outer **instance**, and created through one:

```java
Outer.Inner inner = outer.new Inner();
```

That odd `outer.new` syntax exists because the instance is required. The inner class holds an
implicit reference to its outer instance, which is why:

- it can read the outer instance's private fields, using `Outer.this.x` when names collide
- **it keeps the outer object alive** — a real source of memory leaks when the inner instance
  outlives its logical owner

Prefer `static` nested unless you genuinely need the outer instance.

### Local class

Declared inside a method, constructor, block, loop or static block. Visible only there. It can
capture local variables, but only **effectively final** ones — declared once and never
reassigned — because the captured value is copied into the class. Reassigning the local
afterwards would leave the two out of sync, so the language forbids it.

### Anonymous class

A class declared and instantiated in one expression, with no name:

```java
Person p = new Person() {
    @Override void introduce() { ... }
};
```

It is a one-off subclass (of a class or interface), it can add fields and methods, and it obeys
the same effectively-final capture rule. For an interface with a **single** abstract method, a
lambda replaces it entirely and reads far better — see `11_functional`. Anonymous classes are
still needed when you must override several methods or add state.

## Immutability

The recipe, and every step matters:

1. make the class `final`, so behaviour cannot be overridden
2. make all fields `private final`
3. set them only in the constructor
4. no setters
5. **defensive-copy every mutable field, on the way in and on the way out**

Step 5 is the one people skip. `final` freezes the *reference*, not the object it points at —
so `LeakyStudent` in `ImmutableClass.java` has all-final fields and still leaks:

```java
leaky.getCollege().name = "IIT Bombay";     // mutated the internal object
```

`SafeStudent` copies in the constructor and copies again in the getter, so the caller only ever
touches a copy. `String`, the wrappers and `BigDecimal` are all immutable, which is why they
need no such care.

## Wrapper classes and autoboxing

Each primitive has an object counterpart: `byte`→`Byte`, `int`→`Integer`, `char`→`Character`,
and so on. Collections and generics hold references only, so wrappers are how primitives get
into a `List<Integer>`.

**Autoboxing** wraps automatically; **unboxing** unwraps. It happens on assignments, method
calls and arithmetic, so `Integer a = 10; int sum = a + 20;` compiles with two conversions you
never see.

Two traps:

### Caching, and `==`

`Integer` caches instances for **−128 … 127** (as do `Byte`, `Short`, `Long`, `Character`, and
`Boolean` entirely):

```java
Integer a = 100, b = 100;   a == b   // true  — the same cached object
Integer c = 200, d = 200;   c == d   // false — two objects
```

So `==` on wrappers appears to work in testing and fails in production with larger values.
**Always compare wrappers with `equals`.**

### Unboxing null

```java
Integer value = null;
int x = value;              // NullPointerException
```

Autoboxing hides the `value.intValue()` call that throws. This is a frequent source of
mysterious NPEs on lines with no visible method call — especially with
`map.get(missingKey)` assigned to an `int`.

Also worth knowing: boxing in a tight loop allocates. `int` in, `int` out for hot arithmetic.

## Packages

A package is a namespace and the unit of the default access level. `packages/` demonstrates the
awkward case: two classes with the same simple name.

```java
college.Student s1 = new college.Student();   // fully qualified
school.Student  s2 = new school.Student();
```

Neither can be imported, because importing one still leaves the other ambiguous — and
`import college.*; import school.*;` makes the bare name `Student` a compile error. Fully
qualified names are the only way out. Classes in the same package need no import at all, which
is why `college.Teacher` uses `Student` directly.

Conventions: reverse-domain names (`com.example.project`), all lowercase, and the directory
structure must mirror the package name.

## Code that does not compile

Counter-examples live as comments in the `.java` files, each with the exact `javac` error it
produces. The reasoning is here.

```java
Animal a = new Animal("Bruno");
// error: Animal is abstract; cannot be instantiated
```

An abstract class may have unimplemented methods, so an instance of it could receive a call with
no body to run. The class exists to be extended; only a concrete subclass can be instantiated.
An anonymous subclass — `new Animal("Bruno") { void makeSound() { ... } }` — is legal, because
that *is* a concrete subclass.

```java
class Child extends Parent {
    void sealed() { }
}
// error: sealed() in Child cannot override sealed() in Parent
//        overridden method is final
```

`final` on a method is a promise that the implementation is the only one. Superclasses rely on
that promise when their own correctness depends on the method's behaviour — a template method,
or anything a constructor calls. `private` and `static` methods cannot be overridden either, but
for different reasons: a `private` method is invisible to the child, so a same-named method is
simply unrelated; a `static` method belongs to the class, so a child's version **hides** rather
than overrides it, and hiding is resolved by reference type.

```java
Vehicle vehicle = new Car();
vehicle.brake();
// error: illegal static interface method call
```

`static` interface methods are deliberately **not inherited**, unlike `static` class methods.
When Java 8 added them, inheriting them would have meant a class implementing two interfaces
could inherit two conflicting statics — with no override mechanism to resolve it, since statics
are not polymorphic. Requiring `Vehicle.brake()` removes the ambiguity entirely.

```java
Student() {
    System.out.println("before");
    this("Unknown");
}
// error: call to this must be first statement in constructor
```

The same rule applies to `super(...)`. An object must be fully initialised bottom-up before
anything observes it, so the parent's constructor — or the sibling constructor that eventually
calls it — has to run before any other statement. Allowing a statement first would let it read
fields that are still at their type defaults. This is also why exactly one of `this(...)` or
`super(...)` can appear, and why the compiler inserts `super()` when you write neither.

```java
int captured = 5;
captured++;
class Local { void print() { System.out.println(captured); } }
// error: local variables referenced from an inner class
//        must be final or effectively final
```

A local lives on the stack and dies when the method returns; the class instance lives on the
heap and may outlive it. So the captured value is **copied** into the instance. If the local
could still change afterwards, the copy and the original would silently disagree — and there
would be no way to say which one `captured` means. Java forbids the situation instead of
picking. The same rule applies to lambdas and anonymous classes. (The workaround, when you
genuinely need a mutable capture, is a one-element array or an `AtomicInteger` — the *reference*
stays final while its contents change.)

```java
interface B extends A { default void fun() { ... } }
interface C extends A { default void fun() { ... } }
class D implements B, C { }
// error: types B and C are incompatible;
//        class D inherits unrelated defaults for fun() from types B and C
```

The diamond problem, and Java's answer is to refuse to guess. Neither `B` nor `C` is more
specific than the other, so there is no principled winner. `D` must override `fun()`, and may
delegate explicitly with `B.super.fun()`. Contrast this with a class-versus-interface conflict,
which *does* have a rule — the class wins — because a concrete implementation is always more
specific than a default.

## Running the examples

```sh
javac -d out src/06_oop/*.java
java -cp out ObjectClassMethods

# the packages demo has its own root
cd src/06_oop/packages
javac -d out PackagesDemo.java college/*.java school/*.java
java -cp out PackagesDemo
```
