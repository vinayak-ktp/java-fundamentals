# 08 — Exception Handling

What happens when a method cannot do what it was asked, and how the failure travels to code
that can deal with it.

| File | Concept |
|---|---|
| `TryCatchFinally.java` | The basic mechanism, and `finally`'s guarantees |
| `ExceptionPropagation.java` | How an exception moves up the call stack |
| `NestedTryAndMultiCatch.java` | Nesting, catch ordering, multi-catch |
| `ThrowAndThrows.java` | Raising versus declaring; checked versus unchecked |
| `CustomExceptions.java` | Writing your own exception type |

---

## Why the mechanism exists

Without it, an error either has to be signalled in the return value — which callers can ignore
— or the program simply stops:

```java
System.out.println("Step 1");
System.out.println(a / b);      // b is 0 → ArithmeticException
System.out.println("Step 2");   // never runs
```

An uncaught exception unwinds the entire call stack, hands the exception to the thread's
default handler (which prints the stack trace to `System.err`), and kills that thread. In
`main`, that ends the program.

Exceptions separate the error path from the happy path, cannot be silently ignored, and carry a
stack trace that says exactly where things went wrong.

## The hierarchy

```
Throwable
├── Error                      — JVM-level, do not catch
│   ├── OutOfMemoryError
│   └── StackOverflowError
└── Exception
    ├── RuntimeException       — UNCHECKED
    │   ├── NullPointerException
    │   ├── ArithmeticException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── ClassCastException
    │   ├── NumberFormatException
    │   └── IllegalArgumentException / IllegalStateException
    ├── IOException            — CHECKED
    ├── FileNotFoundException
    └── InterruptedException
```

**Checked** = everything under `Exception` *except* the `RuntimeException` subtree. The
compiler enforces that each one is either caught or declared with `throws`.

**Unchecked** = `RuntimeException` and its subclasses, plus `Error`. No compiler ceremony.

The intended distinction: a checked exception is a *recoverable, expected* condition outside
your control (the file is missing, the network dropped). An unchecked exception signals a
*programming bug* (null dereference, bad index, invalid argument) — there is no sensible
recovery, so the compiler does not force you to write one.

`Error` is for conditions an application cannot reasonably handle. Do not catch it, except
briefly and deliberately at a boundary.

## `try`, `catch`, `finally`

```java
try {
    // code that may throw
} catch (ArithmeticException e) {
    // handle it
} finally {
    // always runs
}
```

**`finally` always runs** — after a normal exit, after a caught exception, after an uncaught
one on its way through, and even after a `return`. That makes it the place for cleanup:
closing files, releasing locks (`13_multithreading/ExplicitLocks.java` depends on this).

Two subtleties:

- `finally` runs *after* a `return` value has been computed, so a `return` inside `finally`
  **discards** the one from `try` — including discarding an in-flight exception. Never return
  or throw from `finally`.
- The only things that skip it are `System.exit()` and the JVM dying.

`try`-with-resources is the modern replacement for the close-in-`finally` idiom: any
`AutoCloseable` declared in the parentheses is closed automatically, in reverse order, even on
an exception path. `01_basics/ConsoleInput.java` uses it.

### Useful members of the exception object

- `getMessage()` — the text passed to the constructor
- `printStackTrace()` — the full trace; prefer a logger in real code
- `getCause()` — the wrapped underlying exception, when one was chained
- `getStackTrace()` — the frames, programmatically

## Propagation

An exception is thrown, then travels **up** the call stack until a matching `catch` is found:

```
main → methodA → methodB     throws here
main ← methodA ← methodB     caught in methodB, so main carries on
```

Each frame in between is unwound — its locals are discarded and its `finally` blocks run. If no
frame catches it, the thread's default handler prints the trace and the thread dies.

The practical consequence is that you do **not** need a `try` in every method. Catch the
exception at the level that can actually do something about it — retry, use a default, report
to the user — and let it pass through everything else. A `catch` block that only logs and
continues is usually hiding a bug.

## Nesting and catch ordering

An inner `try` handles what it can; anything it does not match propagates to the outer one.
`NestedTryAndMultiCatch.java` shows both directions — the inner `catch` matching, and the inner
`catch` missing so the outer one handles it.

### Ordering is enforced

Catch blocks are tested **top to bottom**, first match wins. So a subclass must come before its
superclass:

```java
catch (ArithmeticException e) { }   // specific first
catch (RuntimeException e)    { }
catch (Exception e)           { }   // most general last
```

Putting `catch (Exception e)` first is a **compile error** for the blocks below it — the
compiler knows they are unreachable. This is one of the rare places where Java catches a
logic mistake for you.

### Multi-catch

```java
catch (ArithmeticException | NullPointerException e) { ... }
```

One block for several unrelated types, when the handling is genuinely identical. The types must
not be in a subclass relationship with each other (that would be redundant), and `e` is
effectively final. If the two cases need different handling, use two blocks — the fact that
multi-catch *compiles* does not mean it is the right choice.

### Catching too much

`catch (Exception e) { }` swallows everything, including bugs you needed to see, and is the
most common exception-handling mistake. Catch the narrowest type you can actually handle.

## `throw` versus `throws`

They are different keywords doing different jobs, and the one-letter difference is unfortunate:

```java
throw new IllegalArgumentException("Age cannot be negative");   // raise it, here, now
static void readFile() throws FileNotFoundException { ... }     // declare it may escape
```

- **`throw`** is a statement. It takes a `Throwable` **instance** — hence the `new`.
- **`throws`** is part of the method signature. It lists checked exception **types** the method
  may let escape, and is documentation the compiler enforces on callers.

An unchecked exception needs no `throws` clause, though adding one can be useful documentation.
Overriding a method may narrow the declared exceptions, never widen them.

### Wrapping

When you catch a low-level exception and rethrow a meaningful one, pass the original as the
cause:

```java
catch (IOException e) {
    throw new ConfigurationException("Could not read config", e);
}
```

Dropping the cause destroys the stack trace that explains what actually happened.

## Custom exceptions

Extend `Exception` for checked, `RuntimeException` for unchecked. The key design choice is to
**carry the data that explains the failure**, not just a message:

```java
static class InvalidAgeException extends Exception {
    private final int age;

    InvalidAgeException(String message, int age) {
        super(message);      // hand the message to Throwable
        this.age = age;
    }

    int getAge() { return age; }
}
```

The `super(message)` call is what makes `getMessage()` work. The `age` field is what lets the
handler report or react to the actual offending value — a message alone forces the caller to
parse text.

Choose checked when the caller can plausibly recover and you want to *force* them to consider
it; unchecked when the exception means the caller passed something invalid. Modern API design
leans unchecked, since `throws` clauses propagate virally through every layer.

## Practical guidelines

- Catch narrowly, and only where you can act.
- Never swallow an exception silently. If it truly is ignorable, comment why.
- Prefer try-with-resources over `finally` for anything closeable.
- Do not use exceptions for control flow — they are expensive (filling in a stack trace is the
  costly part) and they obscure intent.
- Validate early and throw `IllegalArgumentException`/`IllegalStateException` for programming
  errors.
- Preserve the cause when wrapping.
- Never return or throw from `finally`.

## Code that does not compile

Counter-examples live as comments in the `.java` files, each with the exact `javac` error it
produces. The reasoning is here.

```java
try { ... }
catch (Exception e) { }
catch (ArithmeticException e) { }
// error: exception ArithmeticException has already been caught
```

Catch blocks are tested in order, so `catch (Exception e)` matches everything and the block
below it can never run. Java does not merely warn about the dead code — it rejects the program.
Order subclasses before superclasses.

This is one of the few places the compiler catches a *logic* mistake rather than a type
mistake, and it is worth noticing why it can: reachability here is decidable purely from the
type hierarchy.

The same rule explains why multi-catch types cannot be related — `catch (IOException |
FileNotFoundException e)` is rejected, because the second is already covered by the first.

```java
static void readFile() {
    new FileReader("abc.txt");
}
// error: unreported exception FileNotFoundException;
//        must be caught or declared to be thrown
```

This is the entire meaning of "checked": the compiler verifies that every checked exception is
either handled locally or declared, all the way up the call chain. There is no way to ignore one
silently.

An unchecked exception needs neither. That difference is the *only* mechanical distinction
between the two — `RuntimeException` and `IOException` are otherwise ordinary classes with the
same API. Everything else about the split (bug versus recoverable condition) is convention about
when to use which.

Note that overriding narrows this rather than widening it: an override may declare fewer or
narrower checked exceptions than the method it overrides, never more. Otherwise a caller holding
a supertype reference could receive an exception the compiler told it was impossible.

## Running the examples

```sh
javac -d out src/08_exception_handling/*.java
java -cp out NestedTryAndMultiCatch
```
