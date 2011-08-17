import java.util.Random;

/**
 * @author Bergman
 *
 */
public class Buckets {
   public static void main(String[] args) throws Exception {
      //System.out.println("Tokens: " + Integer.parseInt(args[0]));
      //System.out.println("Threads: " + Integer.parseInt(args[1]));

      int[] cs = new int[5];
      int pos = 0;
      int nnn = 0;

      TokenBucket bucket = new TokenBucket(1000, 150);
      Random rnd = new Random();

      while (true) {
         int consumers = rnd.nextInt(1000);
         for (int i = 0; i < consumers; ++i) {
            bucket.consume();
            System.out.print("x");
            if (nnn++ % 50 == 0)
               System.out.println();
            int npos = (int) ((System.currentTimeMillis() / 1000) % cs.length);
            if (npos != pos)
               cs[npos] = 0;
            pos = npos;
            cs[pos]++;
         }
         System.out.println();
         System.out.println(speed(cs, pos));
         Thread.sleep(rnd.nextInt(1000));
      }
   }

   private static double speed(int[] cs, int pos) {
      int t = 0;
      for (int i = 0; i < cs.length; i++) {
         if (i == pos)
            continue;
         t += cs[i];
      }
      return (double) t / (cs.length - 1);
   }
}
