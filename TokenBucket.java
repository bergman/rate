import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author Bergman
 *
 */
public class TokenBucket {

   private final Semaphore tokens = new Semaphore(0);
   private int b;
   private int r;
   private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

   public TokenBucket(int limit, int rate) {
      b = limit;
      r = rate;

      tokens.release(b);
      Runnable task = new Runnable() {
         public void run() {
            if (tokens.availablePermits() < b)
               tokens.release();
         }
      };

      executor.scheduleAtFixedRate(task, 0, 1000000 / r, TimeUnit.MICROSECONDS);
   }

   public void consume() {
      tokens.acquireUninterruptibly();
   }

   public boolean tryConsume() {
      return tokens.tryAcquire();
   }

}
