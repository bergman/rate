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
      this.rate = rate;

      scheduleReplenishTask();
   }

   public int getLimit() {
      return limit;
   }

   /**
    * @return rate the number of tokens added to the bucket every second
    */
   public int getRate() {
      return rate;
   }

   /**
    * @return the number of available tokens in this bucket as of right now.
    */
   public int getTokens() {
      return semaphore.availablePermits();
   }

   public void release(int tokens) {
      semaphore.release(tokens);
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }

   /**
    * @param rate the number of tokens added to the bucket every second
    */
   public void setRate(int rate) {
      this.rate = rate;

      scheduleReplenishTask();
   }

   public boolean tryAcquire() {
      return semaphore.tryAcquire();
   }

   private void scheduleReplenishTask() {
      int initialDelay = 0;
      // 1,000,000 microseconds = 1 second
      long period = 1000000 / rate;

      // cancel the old task but let it run through if it's still executing
      if (scheduledTask != null)
         scheduledTask.cancel(false);

      Runnable task = new Runnable() {
         public void run() {
            if (semaphore.availablePermits() < limit)
               semaphore.release();
         }
      };

      scheduledTask = executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MICROSECONDS);
   }
}
