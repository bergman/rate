import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author Bergman
 *
 */
public class TokenBucket {
   private final static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
   private final Semaphore semaphore = new Semaphore(0);
   private int limit;
   private int rate;
   private ScheduledFuture<?> scheduledTask;

   /**
    * @param limit the number of tokens this bucket can hold
    * @param rate the number of tokens added to the bucket every second
    */
   public TokenBucket(int limit, int rate) {
      semaphore.release(limit);
      this.limit = limit;
      setRate(rate);
   }

   public int getLimit() {
      return limit;
   }

   public int getRate() {
      return rate;
   }

   public void release(int tokens) {
      semaphore.release(tokens);
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }

   public void setRate(int rate) {
      this.rate = rate;

      Runnable task = new Runnable() {
         public void run() {
            if (semaphore.availablePermits() < limit)
               semaphore.release();
         }
      };

      int initialDelay = 0;
      long period = 1000000 / rate;

      if (scheduledTask != null)
         scheduledTask.cancel(false);

      scheduledTask = executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MICROSECONDS);
   }

   public boolean tryAcquire() {
      return semaphore.tryAcquire();
   }
}
