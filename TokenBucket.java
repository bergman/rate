import java.util.concurrent.Semaphore;

/**
 * @author Bergman
 *
 */
public class TokenBucket {
   private final int S = 10;
   private final Semaphore tokens = new Semaphore(0);
   private int b;
   private int r;
   private int tpS;

   public TokenBucket(int limit, int rate) {
      b = limit;
      r = rate;
      tpS = (r * S) / 1000;
      if (tpS <= 0)
         throw new IllegalArgumentException("rate must be >= " + (1000 / S) + " to satisfy (rate * " + S + ")/1000 >= 1");

      tokens.release(b);
      new Thread(new Runnable() {
         public void run() {
            while (true) {
               int t = Math.min(b - tokens.availablePermits(), tpS);
               tokens.release(t);
               try {
                  Thread.sleep(S);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      }).start();
   }

   public void consume() {
      tokens.acquireUninterruptibly();
   }

   public boolean tryConsume() {
      return tokens.tryAcquire();
   }
}
