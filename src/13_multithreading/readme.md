# 13 — Multithreading and Concurrency

Running more than one thing at a time, and the machinery needed to keep that correct. This is
the module where the JVM's memory model starts to matter.

| File | Concept |
|---|---|
| `ThreadCreation.java` | `Thread` versus `Runnable`, `start()` versus `run()` |
| `ThreadLifecycle.java` | The six thread states |
| `ThreadMethods.java` | `sleep`, `join`, `yield`, `interrupt`, priority, daemon |
| `Synchronization.java` | Race conditions, critical sections, `synchronized` |
| `StaticSynchronization.java` | The class lock versus the instance lock |
| `VolatileKeyword.java` | Visibility between threads |
| `ProducerConsumer.java` | `wait()` / `notify()` coordination |
| `ExplicitLocks.java` | `ReentrantLock` and `tryLock` |
| `ReadWriteAndStampedLocks.java` | Shared reads, exclusive writes, optimistic reads |
| `AtomicVariables.java` | Lock-free updates via compare-and-set |
| `ExecutorFrameworkBasics.java` | Thread pools |
| `FutureAndCallable.java` | Tasks that return values or throw |
| `CustomThreadPool.java` | `ThreadPoolExecutor` tuning |

---

## Process versus thread

A **process** has its own memory space. A **thread** is a unit of execution *inside* a process:
it gets its own stack, program counter and registers, but **shares the heap** with every other
thread in the process.

That shared heap is the entire source of both the power and the difficulty. Threads can
cooperate on the same data cheaply — and they can corrupt it just as cheaply.

**Concurrency** is several tasks making progress by interleaving (possibly on one core).
**Parallelism** is several tasks literally executing at the same instant on different cores.

## Creating threads

```java
class MyThread extends Thread { public void run() { ... } }   // 1
class MyRunnable implements Runnable { public void run() { ... } }   // 2
new Thread(() -> ...);                                        // 2, as a lambda
```

**Prefer `Runnable`.** It separates *what to do* from *the worker that does it*, leaves the single
`extends` slot free, and is what every executor API accepts. `Runnable` is a functional
interface, so a lambda is usually the whole implementation.

### `start()` versus `run()`

```java
t.start();   // asks the OS for a new thread, which then calls run()
t.run();     // an ordinary method call on the CURRENT thread — no concurrency at all
```

Calling `run()` directly is a silent bug: everything works, nothing is concurrent.

What `start()` actually does: the JVM asks the OS to create a native thread, that thread gets its
own stack and program counter, and it begins executing `run()`.

**A `Thread` object is single-use.** A second `start()` throws
`IllegalThreadStateException`, even after the thread has terminated. This is one of the reasons
thread pools exist.

### Non-determinism

`interleaving()` in `ThreadCreation.java` prints different output on different runs. There is no
guaranteed ordering between threads unless you create one explicitly. Never write code that
depends on observed interleaving — it will differ on another machine, another JVM, or another
day.

## The thread lifecycle

| State | Meaning |
|---|---|
| `NEW` | created, `start()` not yet called |
| `RUNNABLE` | eligible to run; the OS scheduler decides when it is actually on a core |
| `BLOCKED` | waiting to acquire a monitor lock |
| `WAITING` | `wait()`, `join()` with no timeout — waiting indefinitely for another thread |
| `TIMED_WAITING` | `sleep(ms)`, `wait(ms)`, `join(ms)` |
| `TERMINATED` | `run()` has finished |

Note that Java has no "RUNNING" state: `RUNNABLE` covers both "on a core" and "ready and
waiting for one", because the JVM cannot see the OS scheduler's decision.

The distinction between `BLOCKED` and `WAITING` matters when reading a thread dump:
`BLOCKED` means contention on a lock; `WAITING` means the thread is deliberately parked until
someone signals it.

## Thread methods

| Method | Effect |
|---|---|
| `Thread.sleep(ms)` | park the **current** thread; `RUNNABLE` → `TIMED_WAITING` → `RUNNABLE`. Does **not** release any lock. |
| `t.join()` | the **caller** waits for `t` to terminate |
| `t.join(ms)` | same, but gives up after the timeout |
| `Thread.yield()` | a hint that others of equal priority may run; stays `RUNNABLE`, and the OS may ignore it |
| `t.interrupt()` | sets `t`'s interrupt flag — a *request* to stop |
| `t.isInterrupted()` | read the flag |
| `Thread.interrupted()` | read **and clear** the flag (static, current thread) |
| `t.isAlive()` | started and not yet terminated |
| `t.setName` / `getName` | naming, invaluable in logs and dumps |
| `t.setPriority(1..10)` | `MIN_PRIORITY`, `NORM_PRIORITY` (5), `MAX_PRIORITY`; **only a hint** |
| `t.setDaemon(true)` | background thread; must be set **before** `start()` |
| `Thread.currentThread()` | reference to the running thread |

### Interruption is cooperative

Java has no way to safely kill a thread. `Thread.stop()` was deprecated decades ago because it
could leave shared data half-modified. `interrupt()` instead sets a flag:

- A thread doing computation must **check** `isInterrupted()` and return.
- A thread parked in `sleep`, `join` or `wait` throws **`InterruptedException`** instead, and
  **that throw clears the flag**.

Which leads to the single most important convention in this module: when you catch
`InterruptedException` and cannot handle it, **restore the flag**:

```java
catch (InterruptedException e) {
    Thread.currentThread().interrupt();   // let callers up the stack see it
}
```

Swallowing it silently — `catch (Exception e) {}`, as the original course code did — makes a
thread uninterruptible and is a real bug. Every example in this module restores the flag.

### Daemon threads

A daemon thread does not keep the JVM alive: when the last **user** thread finishes, all daemons
are killed wherever they happen to be, with no chance to clean up. The garbage collector runs on
daemon threads. Use them only for work that is genuinely safe to abandon mid-flight.

## Race conditions and `synchronized`

`count++` looks atomic and is not. It is three operations: **read**, **add one**, **write back**.
Two threads can both read `5`, both compute `6`, and both write `6` — one increment is lost.

```
T1: read 5 ....... write 6
T2: ..... read 5 ......... write 6      → count is 6, not 7
```

`Synchronization.java` runs two threads doing 10,000 increments each and reliably prints less
than 20,000 for the unsafe counter.

The region that must not interleave is the **critical section**. `synchronized` protects it using
the object's **monitor lock**: at most one thread holds a given object's monitor at a time.

```java
synchronized void method() { ... }        // locks `this`
synchronized (someObject) { ... }         // locks someObject — a narrower scope
```

Facts worth committing to memory:

- **Two synchronized methods on the same object share one lock.** A thread inside `m1` blocks
  another thread trying to enter `m2`. They are not independently protected.
- **The lock is per object.** Two different instances do not block each other.
- `synchronized` is **reentrant**: a thread already holding a lock can re-acquire it, which is
  what lets one synchronized method call another.
- Locking is released automatically on exit, including on an exception. This is the main
  advantage over explicit locks.
- **Never lock on a new object** (`synchronized (new Object())`) — every thread gets its own,
  so nothing is protected. The original course code contained this bug; it is worth recognising.
- Do not lock on a `String` literal or a boxed primitive — they are pooled or cached, so the lock
  is unintentionally shared with unrelated code.
- `synchronized` also establishes a **happens-before** relationship, so it provides visibility as
  well as mutual exclusion.

### Static synchronization

Static state is shared by every instance, so an instance lock cannot protect it. It needs the
**class lock**:

```java
synchronized (Counter.class) { count++; }
static synchronized void increment() { ... }   // equivalent
```

The class lock and the instance lock are **independent** — a thread holding one does not block a
thread holding the other, as `differentLocksDoNotBlock()` shows. Mixing the two by accident is a
classic way to have "synchronized" code that races anyway.

## `volatile`

A separate problem from atomicity: **visibility**. Each thread may cache a field in a register or
CPU cache, so one thread's write can remain invisible to another indefinitely:

```java
while (!flag) { }   // may spin forever without volatile
```

`volatile` guarantees that reads and writes go to main memory, and prevents the compiler from
reordering around them.

What it does **not** do: make compound operations atomic. `count++` on a `volatile int` still
races, because the read and the write are separate actions. Use `synchronized` or an atomic class
for that.

Use `volatile` for exactly this shape: a flag written by one thread and read by others.

## `wait()` / `notify()`

`ProducerConsumer.java` presents three versions on purpose, because the middle one is the
instructive failure:

1. **No coordination** — the consumer reads an empty or stale box.
2. **`synchronized` plus a busy wait** — correct in intent, and it **deadlocks**. `sleep` and
   spinning do **not** release the monitor, so the waiting thread holds the lock the other thread
   needs to make progress.
3. **`wait()` / `notify()`** — correct. `wait()` *releases the lock* and parks the thread;
   `notify()` wakes one waiter; `notifyAll()` wakes all of them.

The rules, all of which the third version follows:

- Call them only while **holding the monitor**, i.e. inside a `synchronized` block on the same
  object — otherwise `IllegalMonitorStateException`.
- **Always wait in a loop, never in an `if`.** Spurious wakeups are permitted by the
  specification, and with several waiters the condition may already be false again by the time
  you get the lock back:

  ```java
  while (full) { wait(); }   // correct
  if (full) { wait(); }      // broken
  ```
- Prefer `notifyAll()` unless you can prove a single waiter is enough. `notify()` with multiple
  waiters of different kinds can wake the wrong one and hang.

In new code, `BlockingQueue` (`ArrayBlockingQueue`, `LinkedBlockingQueue`) implements exactly
this pattern already, correctly, and should be used instead.

## Explicit locks

`ReentrantLock` does what `synchronized` does, plus what it cannot:

| Capability | `synchronized` | `ReentrantLock` |
|---|---|---|
| Automatic release | ✔ | ✘ — you must `unlock()` |
| Try without blocking | ✘ | `tryLock()` |
| Wait with a timeout | ✘ | `tryLock(time, unit)` |
| Interruptible waiting | ✘ | `lockInterruptibly()` |
| Fairness (FIFO) option | ✘ | `new ReentrantLock(true)` |
| Multiple wait sets | one per object | `newCondition()` |
| Cross-method locking | ✘ | ✔ |

The cost is manual bookkeeping, and the idiom is non-negotiable:

```java
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();   // in finally, or an exception leaks the lock forever
}
```

`tryLock()` returns `false` immediately rather than blocking — useful for "skip it if busy" work
and for deadlock avoidance.

### Read–write locks

A plain lock serialises everything, including readers that cannot conflict with each other.
`ReentrantReadWriteLock` splits that:

- the **read lock** is **shared** — any number of readers concurrently
- the **write lock** is **exclusive** — against readers and other writers

A large win for read-heavy data. Note that upgrading a read lock to a write lock is not allowed
and will deadlock; release and re-acquire instead.

### `StampedLock`

Goes further with an **optimistic read**: take a stamp, read *without any lock*, then validate
that no writer intervened. If one did, fall back to a real read lock:

```java
long stamp = lock.tryOptimisticRead();
int value = this.value;
if (!lock.validate(stamp)) {
    stamp = lock.readLock();
    try { value = this.value; } finally { lock.unlockRead(stamp); }
}
```

Fastest option for read-dominated data, with two caveats: it is **not reentrant**, and it does not
support `Condition`. The fallback path is mandatory, not optional.

## Atomic variables

The atomic classes fix `count++` without any lock, using a hardware **compare-and-set** (CAS)
instruction:

1. read the current value
2. compute the new one
3. swap **only if** the value is still what you read; otherwise retry

```java
AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet();
```

`casByHand()` in `AtomicVariables.java` writes that retry loop out explicitly, which is exactly
what `incrementAndGet` does internally.

`AtomicInteger`, `AtomicLong`, `AtomicBoolean`, `AtomicReference`, and the array variants.
`compareAndSet(expected, new)` is the primitive they are all built on — and it makes
`SeatBooking` correct with no lock at all: exactly one thread can win the swap, so a seat is never
double-booked.

Lock-free means no blocking and no deadlock, and it is faster than locking under low to moderate
contention. Under *heavy* contention the retry loop can waste CPU (`LongAdder` exists for that
case). CAS also only ever protects **one** variable — several related fields still need a lock.

## The executor framework

Creating a thread per task is expensive (each one is an OS resource plus ~1 MB of stack) and
unbounded, so a burst of work can exhaust the machine. A **thread pool** reuses a fixed set of
workers and queues the rest.

```java
ExecutorService executor = Executors.newFixedThreadPool(2);
executor.execute(() -> ...);     // fire and forget
executor.shutdown();             // stop accepting; let running tasks finish
```

| Factory | Behaviour |
|---|---|
| `newFixedThreadPool(n)` | exactly `n` threads, unbounded queue |
| `newCachedThreadPool()` | grows as needed, reuses idle threads for 60s |
| `newSingleThreadExecutor()` | one thread — tasks run in order |
| `newScheduledThreadPool(n)` | delayed and periodic tasks |
| `newVirtualThreadPerTaskExecutor()` | Java 21+, a virtual thread per task |

**Always shut the pool down.** Pool threads are user threads, so a live pool keeps the JVM
running. `shutdown()` is graceful; `shutdownNow()` also interrupts running tasks;
`awaitTermination(timeout, unit)` blocks until they finish.

### `Callable` and `Future`

`Runnable` returns nothing and cannot throw a checked exception. `Callable<T>` does both:

```java
Future<Integer> f = executor.submit(() -> { Thread.sleep(300); return 10; });
f.get();   // blocks until the result is ready
```

`Future` offers `get()`, `get(timeout, unit)`, `cancel(mayInterrupt)`, `isDone()`, `isCancelled()`.

**The exception difference is the practical one.** A failure inside `execute()` surfaces on the
worker thread as an uncaught exception and is easy to miss entirely. A failure inside `submit()`
is **stored in the `Future`** and rethrown, wrapped in `ExecutionException`, when you call
`get()`. So a `submit()` whose `Future` is never inspected swallows the failure completely —
`FutureAndCallable.java` demonstrates both halves. Use `e.getCause()` to reach the real
exception.

For composing asynchronous work, `CompletableFuture` supersedes `Future`.

### Tuning with `ThreadPoolExecutor`

The `Executors` factories are shortcuts over one class. Building it directly exposes what
actually drives the pool:

```java
new ThreadPoolExecutor(
    2,                              // corePoolSize   — kept alive even when idle
    5,                              // maximumPoolSize — ceiling once the queue is full
    10, TimeUnit.SECONDS,           // keepAliveTime  — how long extra threads linger
    new ArrayBlockingQueue<>(2));   // workQueue
```

The order of events for each new task, which is not what most people guess:

1. if fewer than `corePoolSize` threads exist → **create a new thread**
2. else if the queue accepts the task → **queue it**
3. else if fewer than `maximumPoolSize` threads exist → **create a new thread**
4. else → **reject** it via the `RejectedExecutionHandler`

Step 2 preceding step 3 is the important part: with an **unbounded** queue the pool never grows
past the core size, and `maximumPoolSize` is dead configuration. That is precisely what
`newFixedThreadPool` relies on — and why a bounded queue is essential if you want the pool to
expand under load.

Rough sizing: CPU-bound work wants about `numberOfCores` threads; I/O-bound work can use many
more, since the threads spend most of their time blocked.

## Practical guidelines

- Prefer immutable objects — they need no synchronisation at all (`06_oop/ImmutableClass.java`).
- Prefer the `java.util.concurrent` toolkit over hand-rolled `wait`/`notify`:
  `ConcurrentHashMap`, `BlockingQueue`, `CountDownLatch`, `Semaphore`, `CopyOnWriteArrayList`.
- Keep critical sections small, and never do I/O or call unknown code while holding a lock.
- Acquire multiple locks in a **consistent global order** — inconsistent ordering is the classic
  deadlock.
- Never swallow `InterruptedException`.
- Name your threads. It costs nothing and makes every future stack trace readable.
- Concurrency bugs are timing-dependent, so testing cannot prove their absence. Reason about the
  invariants rather than trusting a green run.

## Running the examples

```sh
javac -d out src/13_multithreading/*.java
java -cp out Synchronization
java -cp out ProducerConsumer
java -cp out CustomThreadPool
```

Output that interleaves differently on each run is expected — that is the lesson, not a bug.
