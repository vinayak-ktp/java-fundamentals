# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repository is

A **learning reference**, not an application. 13 numbered modules under `src/`, each holding
standalone single-topic Java examples plus a `README.md` that explains those concepts in depth.

There is no build tool, no dependency manager, no test suite and no `main` entry point for the
project as a whole — every `.java` file is independently compilable and runnable. Verified against
**JDK 21**; some examples use Java 9–21 APIs (`List.of`, `Stream.toList`, private interface
methods, `Thread.threadId`).

## Commands

```sh
# compile one module, then run any example in it
javac -d out src/01_basics/*.java
java -cp out TypePromotion

# compile every module (the only "build" this repo has)
for d in src/*/; do javac -d out "$d"*.java; done
```

`src/06_oop/packages/` is a **separate source root** — it is the one place with real `package`
declarations, and it must be compiled from inside that directory:

```sh
cd src/06_oop/packages
javac -d out PackagesDemo.java college/*.java school/*.java
java -cp out PackagesDemo
```

Examples needing special invocation: `CommandLineArguments` (expects args),
`GarbageCollection` (`java -Xmx64m`), `ConsoleInput` (waits on stdin), everything in
`13_multithreading` (interleaves differently every run — that is intended).

## Structural conventions

These are deliberate and load-bearing; do not "fix" them.

- **No `package` declarations.** Module directories start with digits, which is illegal in a Java
  package name. The sole exception is `src/06_oop/packages/`, which needs real packages to
  demonstrate them.
- **One public class per file, helper types as `static` nested classes.** Because there are no
  packages, two files in the same directory cannot both declare a top-level `Student`. Nesting
  the helpers is what lets many demos reuse names like `Student`, `Animal` and `Box`.
- **Every file has its own `main`** and runs standalone. Files never import each other.
- **Module numbering is dependency order**, not arbitrary: 09 (generics) before 10 (collections),
  11 (lambdas) before 12 (streams), 13 (threads) last.

## Comment and documentation split

Code comments are **sparse and specific** — a quirk, a subtlety, or the concept being
demonstrated. Never narration of the next line. All depth belongs in the module `README.md`.
Adding paragraph-length explanation to a `.java` file works against this deliberately.

Each module README follows the same shape: a file→concept table, concept sections, an optional
**Code that does not compile** section, then **Running the examples**.

## Code that is wrong on purpose

Broken code is a teaching device here and must not be deleted or "corrected". Three tiers:

1. **Compiles and fails at runtime** — lives in the examples, wrapped in a `try`/`catch` that
   prints the lesson (`ArrayStoreException`, `ConcurrentModificationException`, NPE from unboxing
   `null`, stream reuse, `OutOfMemoryError`).
2. **Compiles, runs, silently wrong** — also live code: `Synchronization.UnsafeCounter` (lost
   increments), `ImmutableClass.LeakyStudent` (state escapes). `ProducerConsumer.BusyWaitBox` is
   written out but deliberately never called, because it deadlocks.
3. **Cannot compile** — a comment beside the working version, holding the snippet plus the exact
   `javac` error, with the reasoning in that module's README.

**When adding a tier-3 case, compile a throwaway snippet and copy the real `javac` output.** Every
quoted error message in this repo was verified that way, and several are counter-intuitive (the
array-literal case is only `illegal start of expression`; wildcard errors mention `CAP#1`).

## Keeping docs in sync

`README.md` at the root indexes the modules and documents these conventions. A new example needs
a row in its module README's file table; a new module needs a row in the root table and a place in
the suggested path.

The `reference/` directory is the original course material, kept locally, git-ignored, and
**deliberately unmentioned in any committed file** — do not reintroduce references to it. Its
`.gitignore` entry must stay, since that is what keeps it uncommitted.

Deliberate gaps, not oversights: no file-I/O or serialization module (the source course has none),
and the theory-only lectures contributed README prose rather than files.
