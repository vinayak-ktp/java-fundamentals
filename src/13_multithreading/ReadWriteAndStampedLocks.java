import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/*
 * A plain lock serialises everything, even readers that cannot conflict.
 *
 * ReentrantReadWriteLock: the read lock is shared between any number of
 * readers, the write lock is exclusive against everyone.
 *
 * StampedLock goes further with an optimistic read: take a stamp, read without
 * locking at all, then validate. If a writer interfered, fall back to a real
 * read lock. Fastest for read heavy data, but it is not reentrant.
 */
public class ReadWriteAndStampedLocks {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("-- read write lock");
        ReadWriteResource readWrite = new ReadWriteResource();
        exercise(readWrite::read, readWrite::write);

        Thread.sleep(2000);

        System.out.println("-- stamped lock");
        StampedResource stamped = new StampedResource();
        exercise(stamped::read, stamped::write);
    }

    static void exercise(Runnable read, java.util.function.IntConsumer write) {
        for (int i = 0; i < 3; i++) {
            new Thread(read).start();
        }
        for (int value : new int[]{5, 7, 9}) {
            new Thread(() -> write.accept(value)).start();
        }
    }

    static class ReadWriteResource {
        private int value;

        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final Lock readLock = rwLock.readLock();     // shared
        private final Lock writeLock = rwLock.writeLock();   // exclusive

        void read() {
            readLock.lock();
            try {
                pause();
                System.out.println(Thread.currentThread().getName() + " reads " + value);
            } finally {
                readLock.unlock();
            }
        }

        void write(int newValue) {
            writeLock.lock();
            try {
                pause();
                value = newValue;
                System.out.println(Thread.currentThread().getName() + " writes " + value);
            } finally {
                writeLock.unlock();
            }
        }

        private void pause() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class StampedResource {
        private int value;
        private final StampedLock lock = new StampedLock();

        void read() {
            long stamp = lock.tryOptimisticRead();
            int current = value;

            pause();

            // A writer may have run since the stamp was taken
            if (!lock.validate(stamp)) {
                stamp = lock.readLock();
                try {
                    current = value;
                } finally {
                    lock.unlockRead(stamp);
                }
            }

            System.out.println(Thread.currentThread().getName() + " reads " + current);
        }

        void write(int newValue) {
            long stamp = lock.writeLock();
            try {
                pause();
                value = newValue;
                System.out.println(Thread.currentThread().getName() + " writes " + value);
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        private void pause() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
