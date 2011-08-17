import java.util.Random;

/**
 * @author Bergman
 *
 */
public class Buckets {
   public static void main(String[] args) throws Exception {
      //System.out.println("Tokens: " + Integer.parseInt(args[0]));
      //System.out.println("Threads: " + Integer.parseInt(args[1]));
      TokenBucket bucket = new TokenBucket(10, 1);
      Random rnd = new Random();
      while (true) {
         int consumers = rnd.nextInt(100);
         for (int i = 0; i < consumers; ++i) {
            bucket.consume();
         }
         Thread.sleep(rnd.nextInt(500));
      }
   }
}
