# 01 — Basics

The ground floor of the language: what a program looks like, what kinds of values exist,
how those values convert into one another, and what the operators actually do to them.

| File | Concept |
|---|---|
| `HelloWorld.java` | Program entry point, the two output streams |
| `DataTypesAndLiterals.java` | The eight primitive types and every literal form |
| `FloatingPointPrecision.java` | Why `float` and `double` cannot hold exact decimals |
| `TypeCasting.java` | Widening, narrowing, truncation, the compound-assignment quirk |
| `TypePromotion.java` | How mixed-type expressions pick a result type |
| `ArithmeticAndRelationalOperators.java` | `+ - * / %`, compound assignment, `++`/`--`, comparisons |
| `BitwiseAndShiftOperators.java` | `&`, `\|`, `^`, `~`, `<<`, `>>`, `>>>` |
| `LogicalOperators.java` | `&&`, `\|\|`, `!` and short-circuiting |
| `ConsoleInput.java` | Reading from the keyboard through `System.in` |

---

## The entry point

Every Java program starts at a method with exactly this signature:

```java
public static void main(String[] args)
```

Each word is load-bearing:

- **`public`** — the JVM lives outside your class and must be able to see it.
- **`static`** — the JVM calls this method *before any object of your class exists*. A
  non-static method would need an instance, and the JVM has no idea how to build one (which
  constructor? what arguments?). This is the concrete answer to "why is `main` static".
- **`void`** — the exit status of a Java program comes from `System.exit()`, not from a
  return value.
- **`String[] args`** — the command-line arguments. See
  `07_keywords_and_memory/CommandLineArguments.java`.

`javac Foo.java` produces `Foo.class`; `java Foo` loads that class and calls its `main`.

## Output: two streams, not one

`System.out` and `System.err` are both `PrintStream` objects, but they are **separate
streams**. A shell can redirect them independently:

```sh
java MyApp > output.txt 2> errors.txt
```

That is the entire reason to send diagnostics to `err`: a caller piping your normal output
into another program does not want your warnings mixed into the data. `err` is also
unbuffered, so it appears immediately, which matters when a program crashes.

## The eight primitive types

| Type | Size | Range | Default |
|---|---|---|---|
| `byte` | 8 bit | −128 … 127 | `0` |
| `short` | 16 bit | −32,768 … 32,767 | `0` |
| `int` | 32 bit | ≈ ±2.1 billion | `0` |
| `long` | 64 bit | ≈ ±9.2 × 10¹⁸ | `0L` |
| `float` | 32 bit | ~7 significant digits | `0.0f` |
| `double` | 64 bit | ~15 significant digits | `0.0d` |
| `char` | 16 bit | `0` … `65535` (unsigned) | the NUL character, code point 0 |
| `boolean` | JVM-dependent | `true` / `false` | `false` |

Two consequences of that table that trip people up:

- **`char` is a number.** `'a'` is 97. That is why `'a' + 1` is `98` (an `int`), and why
  `int i = 'a';` compiles without a cast.
- **Those defaults apply to *fields* only.** A local variable has no default at all — the
  compiler rejects any read before an assignment. That is deliberate: an uninitialised local
  is almost always a bug, whereas a field genuinely may be filled in later.

### Literal forms

Integer literals can be written in four bases:

```java
byte decimal = 5;
byte binary  = 0b101;   // 0b prefix, Java 7+
byte octal   = 07;      // a leading zero — a classic source of surprise
byte hex     = 0XA;     // 0x or 0X, digits 0-9 and A-F
```

The leading-zero rule means `int x = 011;` is **9**, not 11. Never zero-pad a decimal
literal.

Suffixes and underscores:

```java
long big     = 3_412_567_89L;   // L required above Integer.MAX_VALUE
float f      = 10.54f;          // f required — a bare 10.54 is a double
double sci   = 6.022e23;        // e notation
int readable = 1_000_000;       // underscores are ignored by the compiler
```

Underscores may sit anywhere *between* digits, and repeats are legal (`6.02____2e23`), but
not at the start, the end, or next to the decimal point.

## Floating point is not decimal

`float` and `double` are binary fractions. `0.1`, `0.2` and `0.7` have no exact binary
representation, exactly as `1/3` has no exact decimal one. So:

```java
System.out.println(0.1 + 0.2);   // 0.30000000000000004
```

Printing more digits than the type carries exposes the stored approximation, which is what
`FloatingPointPrecision.java` does with `%.20f`.

Practical rules:

- **Never compare floating point with `==`.** Compare `Math.abs(a - b) < epsilon`.
- **Never use `double` for money.** Use `BigDecimal`, or store integer cents.
- `double` is the default for a reason — `float`'s ~7 digits run out fast.

## Type conversion

### Widening (implicit)

Smaller into larger loses nothing, so no cast is needed:

```
byte → short → int → long → float → double
        char ↗
```

`long → float` is on that list even though `float` has fewer significant digits than `long`
has bits, so a very large `long` silently loses precision while widening. It is legal
because the *magnitude* still fits.

### Narrowing (explicit)

Larger into smaller can lose data, so the compiler demands a cast as an acknowledgement:

```java
int i = 300;
byte b = (byte) i;   // 44 — the high bits are simply discarded
```

`300` is `1_0010_1100`; keeping the low 8 bits gives `0010_1100` = 44. This is truncation of
bits, not clamping to `Byte.MAX_VALUE`.

### Truncation toward zero

Floating point to integer discards the fraction — it does **not** round:

```java
(int) 15.678   // 15
(int) -15.678  // -15, toward zero, not toward negative infinity
```

Use `Math.round()` when you want rounding.

### boolean is isolated

`boolean` converts to and from nothing. No `int` to `boolean`, no `boolean` to `int`. This
kills the C idiom `if (x = 5)`, which is a genuine feature.

### The compound-assignment quirk

```java
byte b = 50;
b = b * 2;         // does NOT compile
b = (byte)(b * 2); // fine
b *= 2;            // also fine!
```

`b * 2` promotes to `int`, so the plain assignment needs a cast. But the compound operators
(`+=`, `*=`, …) have an **implicit narrowing cast built into the specification**, so they
silently do the cast for you — and silently overflow when the result does not fit.

## Type promotion in expressions

Before any binary numeric operation, the operands are promoted:

1. If either operand is `double`, the other becomes `double`.
2. Else if either is `float`, the other becomes `float`.
3. Else if either is `long`, the other becomes `long`.
4. **Otherwise both become `int`** — this last rule catches everyone.

Rule 4 means `byte + byte` is an `int`, `short * short` is an `int`, and `char / char` is an
`int`. There is simply no arithmetic on types narrower than `int`.

`TypePromotion.java` puts the rules together:

```java
double result = (f * b) + (i / c) - (d * s);
//               float     int       double
```

`i / c` is the interesting one: `i` is `int`, `c` is `char`, so the `char` becomes its code
point (97) and the whole thing is **integer division** — the fraction is gone before the
surrounding `double` arithmetic ever sees it. Mixing integer division into a floating-point
expression is one of the most common silent bugs in Java.

## Operators

### Arithmetic

`/` on two integers is integer division (`7 / 2` is `3`). `%` keeps the sign of the
*left* operand (`-7 % 3` is `-1`). Integer division or modulo by zero throws
`ArithmeticException`; floating-point division by zero quietly produces `Infinity` or `NaN`.

### Increment and decrement

The difference is only in the *value of the expression*, never in the effect on the variable:

```java
int i = 9;
int post = i++;   // post = 9, i = 10   (use, then increment)
int pre  = ++i;   // pre  = 11, i = 11  (increment, then use)
```

As a standalone statement `i++` and `++i` are identical. Only reach for the distinction when
you genuinely want the old value.

### Relational

`== != < > <= >=` all yield `boolean`. On **references**, `==` compares identity — whether
the two variables point at the same object — not content. That is why strings are compared
with `equals`; see `05_strings/StringPoolAndImmutability.java`.

### Bitwise

| Op | Meaning | Example |
|---|---|---|
| `&` | AND | `2 & 3` = 2 |
| `\|` | OR | `2 \| 3` = 3 |
| `^` | XOR | `2 ^ 3` = 1 |
| `~` | NOT (all bits flipped) | `~2` = −3 |

`~2` is −3 because Java integers use **two's complement**: flipping every bit of `n` gives
`-n - 1`.

### Shifts

- `<<` shifts left, filling with zeros — a multiply by 2ⁿ.
- `>>` shifts right **preserving the sign bit** — a floor-divide by 2ⁿ. `-8 >> 1` is `-4`.
- `>>>` shifts right **filling with zeros**, so a negative number becomes a large positive
  one. There is no `<<<`, because left-shifting already fills with zeros.

Two quirks worth memorising:

- **The shift distance is taken modulo the operand width** — 32 for `int`, 64 for `long`. So
  `1 << 33` is `1 << 1` = `2`, not `0`.
- **`byte` and `short` are promoted to `int` before shifting**, so the result is an `int` and
  needs a cast to be stored back.

### Logical and short-circuiting

`&&` and `||` evaluate the right side **only if the result is still undecided**. This is not
just an optimisation, it is a correctness tool:

```java
if (s != null && s.length() > 0)   // the length() call is unreachable when s is null
```

`&` and `|` also work on booleans but always evaluate both sides — occasionally useful when
the right side has a side effect you need, and a bug the rest of the time.

## Reading input

`ConsoleInput.java` shows the layered design of Java I/O, which is worth understanding once:

1. The OS buffers keystrokes until Enter.
2. `System.in` is an `InputStream` — it produces **raw bytes**.
3. `InputStreamReader` decodes bytes into **characters** using a charset.
4. `BufferedReader` assembles characters into **lines** and buffers them, so one read syscall
   serves many `readLine()` calls.

Each layer does one job and wraps the one below it — the decorator pattern, applied
throughout `java.io`.

Details that matter:

- `System.in.read()` returns **one byte as an `int`** (`-1` at end of stream), so it needs a
  cast to `char` to print as text.
- `readLine()` always returns a `String`; numbers must be parsed with `Integer.parseInt`,
  which throws `NumberFormatException` on bad input.
- The reader is opened in a **try-with-resources** block so it is closed on every exit path.
- `java.util.Scanner` is the friendlier alternative: it parses tokens directly
  (`nextInt()`, `nextDouble()`) at the cost of speed. Its classic trap is that `nextInt()`
  consumes the number but leaves the newline in the buffer, so an immediately following
  `nextLine()` returns an empty string — discard that leftover newline with a bare
  `nextLine()` call.

## Running the examples

```sh
javac -d out src/01_basics/*.java
java -cp out TypePromotion
```
