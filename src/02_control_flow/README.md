# 02 — Control Flow

Everything that decides *which* statement runs next: selection, iteration and jumps.

| File | Concept |
|---|---|
| `IfElseConditionals.java` | `if`, `if-else`, nested `if`, the else-if ladder |
| `SwitchStatements.java` | `switch`, `break`, fall-through, String switch |
| `Loops.java` | `for`, comma-separated `for`, `while`, `do-while`, nesting |
| `JumpStatements.java` | `break`, `continue`, labelled break, labelled blocks |

---

## Selection: `if`

The condition must be a `boolean`. Java does not accept an `int` or a reference where a
boolean is expected, so `if (x = 5)` — the classic C typo — does not compile. That is one
whole category of bug the language removes for free.

### Braces are optional and you should still use them

```java
if (i % 2 == 0)
    System.out.println("even");
```

A braceless `if` governs exactly **one** statement. Adding a second line later, at the same
indentation, silently puts it outside the `if`. This is how the Apple "goto fail" TLS bug
happened. Always brace.

### The else-if ladder

```java
if (i == 5)      { ... }
else if (i == 6) { ... }
else if (i == 7) { ... }
else             { ... }
```

Conditions are evaluated top to bottom and **at most one branch runs**. As soon as one
matches, the rest are skipped entirely — so order matters when conditions overlap, and the
cheapest or most likely test belongs first.

### The ladder versus independent ifs

This is the point of `independentIfs()` in the example, and the distinction is easy to get
wrong:

```java
int age = 50;

if (age > 80) { println("very old");      }   // skipped
if (age > 60) { println("old");           }   // skipped
if (age > 40) { println("becoming old");  }   // RUNS
if (age > 20) { println("young");         }   // ALSO RUNS
else          { println("child");         }
```

Four separate `if` statements are four independent decisions, so `age = 50` prints **two**
lines. Turn the first three into `else if` and only one line prints.

The second trap is that the `else` belongs to the **nearest unmatched `if`** — here only the
fourth one. `age = 50` will never print "child", because that `else` is reached only when
`age <= 20`.

### Nested `if` versus `&&`

```java
if (i > 5) { if (i < 10) { ... } }   // nested
if (i > 5 && i < 10)     { ... }     // flattened
```

Identical behaviour, including the short-circuit. Prefer the flattened form; reach for
nesting only when the outer condition guards something the inner one *needs*, such as a null
check before a dereference — and even then `&&` usually reads better.

## Selection: `switch`

`switch` compares one expression against a list of constants. Its restrictions come from how
it compiles — to a jump table or a binary search rather than a chain of comparisons.

**Permitted switch types:** `byte`, `short`, `char`, `int`, their wrapper classes, `enum`,
and `String` (since Java 7). Notably absent: `long`, `float`, `double`, `boolean`.

**Case labels must be compile-time constants and must be unique.** Duplicate labels are a
compile error, not a runtime surprise.

### Fall-through

A `case` without `break` falls into the next one. This is the single biggest source of switch
bugs and also its only elegant feature:

```java
switch (i) {
    case 1:
    case 2:
    case 3:
        System.out.println("small");   // shared by 1, 2 and 3
        break;
    default:
        System.out.println("large");
}
```

`default` need not be last, and matches when nothing else does. A `break` on the final case
is redundant but conventional — it survives someone appending a case below it.

### String switch

```java
switch (status) {
    case "success": ...
}
```

Implemented as a `hashCode()` switch followed by `equals()` checks, so it is `equals`-based
(content, not identity) and **throws `NullPointerException` on a null selector**. Null-check
before switching on a String.

### Modern switch (Java 14+)

Worth knowing exists, though not used in these examples: arrow labels remove fall-through,
and `switch` becomes an *expression* that produces a value.

```java
String size = switch (i) {
    case 1, 2, 3 -> "small";
    default      -> "large";
};
```

The arrow form is exhaustively checked over enums, so adding an enum constant becomes a
compile error rather than a silently missed branch.

## Iteration

### `for`

```java
for (int i = 10; i >= 1; i--) { ... }
```

The exact order of execution:

1. the **init** statement, once
2. the **condition** — if false, the loop ends immediately
3. the **body**
4. the **update**
5. back to step 2

Two consequences: a `for` loop may run zero times, and a variable declared in the init
section is scoped to the loop and vanishes afterwards.

All three sections are optional. `for (;;)` is an infinite loop.

### Comma-separated sections

The init and update sections accept several comma-separated statements — but the condition is
a single expression, so multiple conditions must be joined with `&&`:

```java
for (int i = 1, j = 1; i <= 10 && j <= 5; i++, j += 2) { ... }
```

Both declarations must be of the same type, since it is one declaration statement.

### `while` versus `do-while`

`while` tests **before** the body, so it may run zero times. `do-while` tests **after**, so
it always runs **at least once** — and it needs a terminating semicolon:

```java
do { ... } while (condition);
```

That guaranteed first iteration is exactly what a menu loop wants: show the menu, read the
choice, repeat while the choice is not "exit".

### The enhanced for loop

```java
for (int value : array) { ... }
```

Works on arrays and on anything implementing `Iterable`. It is compiled into an index loop
(arrays) or an iterator loop (`Iterable`) — see `10_collections/IterableAndIterator.java`.
The limitation: no access to the index, and no way to remove elements safely. When you need
either, use the classic form or an explicit `Iterator`.

### Nesting

The inner loop runs to completion on **every** iteration of the outer one, which is where the
`O(n²)` in nested loops comes from. Using the outer variable as the inner bound
(`j <= i`) is the standard way to build triangular output.

## Jump statements

### `break`

Leaves the innermost enclosing loop or `switch` immediately. `breakOnFirstDivisor()`
demonstrates the idiom of inspecting the loop variable afterwards to learn *how* the loop
ended:

```java
int i;
for (i = 2; i < p; i++) {
    if (p % i == 0) break;
}
if (i == p) { /* never broke out, so p is prime */ }
```

This only works because `i` is declared **outside** the loop. Declared in the init section it
would be out of scope on the line below.

### `continue`

Skips the rest of the current iteration and jumps to the update step. Useful as a guard that
avoids deep nesting:

```java
for (...) {
    if (!isInteresting(x)) continue;
    // the interesting case, at one level of indentation
}
```

In a `while` loop, `continue` jumps straight back to the condition — so any increment must
happen *before* the `continue`, or the loop hangs forever.

### Labels

Java has no `goto`, but a plain `break` only escapes **one** level of nesting, which is not
always enough. A label names an enclosing statement so you can break out of several levels at
once:

```java
outer:
for (int i = 1; i <= 10; i++) {
    for (int j = 1; j <= i; j++) {
        if (j >= 5) break outer;   // leaves BOTH loops
    }
}
```

`continue outer;` also works, meaning "start the next iteration of the outer loop".

Labels are not limited to loops — any block can carry one, and `break label` then jumps to
just past that block's closing brace. It only ever jumps **forward**, which is what keeps it
from being a `goto`:

```java
first: {
    second: {
        if (done) break first;   // skips to after the outer block
    }
}
```

Labels are legitimately useful in search loops over a grid. They are also easy to abuse — if
a label is helping, that is often a hint the loop body wants to be its own method, where a
plain `return` does the same job more clearly.

## Running the examples

```sh
javac -d out src/02_control_flow/*.java
java -cp out JumpStatements
```
