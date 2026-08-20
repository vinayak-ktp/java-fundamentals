import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * ReentrantLock does what synchronized does, plus:
 *   tryLock()            take the lock only if it is free
 *   tryLock(timeout)     give up after waiting
 *   lockInterruptibly()  abort the wait on interrupt
 *   fairness option      longest waiting thread goes first
 *
 * The cost is manual bookkeeping: unlock() must live in a finally block, or an
 * exception leaks the lock forever.
 */
public class ExplicitLocks {
    public static void main(String[] args) throws InterruptedException {
        Resource resource = new Resource();

        new Thread(resource::blockingAccess).start();
        new Thread(resource::blockingAccess).start();

        Thread.sleep(100);
        new Thread(resource::skipIfBusy).start();
        new Thread(resource::giveUpAfterWaiting).start();
    }

    static class Resource {
        private final Lock lock = new ReentrantLock();

        void blockingAccess() {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " entered");
                pause(500);
                System.out.println(Thread.currentThread().getName() + " left");
            } finally {
                lock.unlock();
            }
        }

        void skipIfBusy() {
            if (!lock.tryLock()) {
                System.out.println("lock was busy, moving on");
                return;
            }
            try {
                System.out.println("got the lock immediately");
            } finally {
                lock.unlock();
            }
        }

        void giveUpAfterWaiting() {
            try {
                if (!lock.tryLock(200, TimeUnit.MILLISECONDS)) {
                    System.out.println("gave up after 200ms");
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            try {
                System.out.println("got the lock within the timeout");
            } finally {
                lock.unlock();
            }
        }

        private void pause(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
