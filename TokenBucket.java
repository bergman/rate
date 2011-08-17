import java.util.concurrent.Semaphore;

/**
 * 
 */

/**
 * @author Bergman
 *
 */
public class TokenBucket {
   private final Semaphore tokens = new Semaphore(0);
   private int b;
   private int r;
   String chars = "█▇▆▅▄▃▂▁";

   public TokenBucket(int limit, int rate) {
      b = limit;
      r = rate;
      tokens.release(b);
      new Thread(new Runnable() {
         public void run() {
            int printouts = 0;
            while (true) {
               if (tokens.availablePermits() < b && System.currentTimeMillis() % r == 0) {
                  tokens.release();
                  try {
                     System.out.print(chars.charAt((chars.length() - 1) / tokens.availablePermits()));
                     if (printouts++ % 25 == 0)
                        System.out.println();
                  } catch (ArithmeticException e) {
                     System.out.print(" ");
                     if (printouts++ % 25 == 0)
                        System.out.println();
                  }
               }
               try {
                  Thread.sleep(10);
               } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }
         }
      }).start();
   }

   public void consume() {
      tokens.acquireUninterruptibly();
   }
}
