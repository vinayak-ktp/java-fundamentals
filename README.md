# Core Java

A clean, semantically organised reference for Core Java — every concept from a chronological
video course, reorganised by topic instead of by lecture number, refactored to idiomatic Java,
and documented module by module.

Each of the 13 modules holds runnable single-topic examples plus a `README.md` that explains the
concepts in depth: not just the syntax, but the mechanism behind it and the traps that come with
it.

## Modules

| # | Module | What it covers |
|---|---|---|
| 01 | [Basics](src/01_basics/README.md) | Entry point, primitives, literals, floating-point precision, casting, promotion, operators, console input |
| 02 | [Control Flow](src/02_control_flow/README.md) | `if` forms, `switch`, the four loops, `break`/`continue`/labels |
| 03 | [Methods](src/03_methods/README.md) | Declaration, the call stack, overloading and resolution, scope, call-by-value |
| 04 | [Arrays](src/04_arrays/README.md) | Arrays as objects, indexing, jagged arrays, memory layout, `java.util.Arrays` |
| 05 | [Strings](src/05_strings/README.md) | Immutability, the string pool, the full API, `StringBuilder` and capacity |
| 06 | [OOP](src/06_oop/README.md) | Classes, constructors, encapsulation, inheritance, polymorphism, interfaces, `Object`, enums, nested classes, immutability, wrappers, packages |
| 07 | [Keywords and Memory](src/07_keywords_and_memory/README.md) | `static`, initialisation order, `final`, command-line arguments, garbage collection |
| 08 | [Exception Handling](src/08_exception_handling/README.md) | The hierarchy, checked vs. unchecked, `finally`, propagation, custom exceptions |
| 09 | [Generics](src/09_generics/README.md) | Type parameters, erasure, bounds, covariance, wildcards and PECS |
| 10 | [Collections](src/10_collections/README.md) | `List`, `Set`, `Map`, `Queue`, iteration, navigation, `Comparable` and `Comparator` |
| 11 | [Functional](src/11_functional/README.md) | Functional interfaces, lambdas, composition, method references |
| 12 | [Streams](src/12_streams/README.md) | Pipelines, intermediate and terminal operations, collectors, parallel streams, `Optional` |
| 13 | [Multithreading](src/13_multithreading/README.md) | Threads, synchronisation, `volatile`, `wait`/`notify`, locks, atomics, executors |

## Suggested path

The numbering is the intended reading order, and later modules build on earlier ones:

```
01 → 02 → 03 → 04 → 05 → 06 → 07 → 08 → 09 → 10 → 11 → 12 → 13
                          └── largest; take it in pieces
```

- **01–05** are the language basics; they stand alone.
- **06** is the core of the language. Everything after it assumes classes, interfaces and
  polymorphism.
- **09 → 10** in that order: collections make far more sense once generics do.
- **11 → 12** in that order: streams are built entirely on lambdas.
- **13** is the hardest module and depends on 06 (objects, immutability) and 10 (collections).

## Layout

```
core-java/
├── README.md              this file
├── sketch.txt             the original target structure this repo was built from
└── src/
    ├── 01_basics/
    │   ├── README.md
    │   └── *.java
    ├── ...
    └── 13_multithreading/
        ├── README.md
        └── *.java
```

Conventions used throughout:

- **One public class per file**, with helper types as `static` nested classes. That keeps each
  file self-contained and stops same-named helpers (`Student`, `Animal`, `Box`) colliding
  between demos in the same folder.
- **No `package` declarations**, so the numbered directory names stay legal — a Java package
  cannot begin with a digit. The single exception is `src/06_oop/packages/`, which needs real
  packages to demonstrate them and compiles as its own source root.
- **Every file has a `main`** and runs on its own.
- **Comments are sparse and specific** — they explain a quirk, a subtlety or the concept being
  demonstrated, never what the next line obviously does. The depth lives in the module READMEs.
- **Code that is wrong on purpose is kept, not deleted.** Failures that compile and run sit in
  the examples themselves, wrapped in a `try`/`catch` that names the lesson. Code that cannot
  compile sits beside the working version as a comment, with the exact `javac` error it produces
  — and a **Code that does not compile** section in that module's README explains why. Every
  quoted error message was taken from real compiler output.

## Running the examples

Requires a JDK. Everything here is verified against **JDK 21**; a couple of examples use APIs
from Java 9–16 (`List.of`, `Stream.toList`, private interface methods, `Thread.threadId`), so
JDK 21 or newer is the safe choice.

```sh
# compile one module
javac -d out src/01_basics/*.java

# run any example in it
java -cp out TypePromotion
```

The `packages` demo has its own source root:

```sh
cd src/06_oop/packages
javac -d out PackagesDemo.java college/*.java school/*.java
java -cp out PackagesDemo
```

Compile everything at once:

```sh
for d in src/*/; do javac -d out "$d"*.java; done
```

A few examples are intentionally interactive or non-deterministic:

- `01_basics/ConsoleInput.java` waits for keyboard input.
- `07_keywords_and_memory/CommandLineArguments.java` expects arguments —
  `java -cp out CommandLineArguments a b c`.
- `07_keywords_and_memory/GarbageCollection.java` deliberately exhausts the heap; run it with
  `java -Xmx64m -cp out GarbageCollection`.
- Everything in `13_multithreading` interleaves differently on each run. That is the lesson, not
  a bug.

## About the source material

All of this code was written while following the
[Coder Army Java playlist](https://www.youtube.com/playlist?list=PLQEaRBV9gAFsR15tNo2QLF9d2qc-c018p)
— 57 lectures, covering the language from `main` through to the executor framework. Credit for the
material and the teaching goes there.

The course's own code and notes are already public on GitHub, but they are laid out
**chronologically**: one folder per lecture, with files named `Demo.java`, `Demo2.java` and so on.
That works while you are watching along and is close to useless as a reference afterwards — to
revisit wildcards you have to remember it was lecture 28.

This repository is that same material reorganised **by concept**: regrouped into topic modules,
renamed to standard Java conventions, stripped of dead code, and consolidated so that one file
covers one idea.

Where the original had commented-out code that *was* the lesson — the `Object`-based container
before generics, the busy-wait producer/consumer before `wait`/`notify` — both the broken and the
working version were kept side by side, since the contrast is the point.

