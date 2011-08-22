import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * @author Bergman
 *
 */
public class Buckets {
   public static void main(String[] args) throws Exception {
      Random rnd = new Random();

      Map<String, BucketValues> sites = new HashMap<String, BucketValues>();
      sites.put("foo", new BucketValues(10, 500));
      sites.put("bar", new BucketValues(100, 50));
      sites.put("baz", new BucketValues(500, 1000));

      Map<String, TokenBucket> buckets = new HashMap<String, TokenBucket>();
      for (Entry<String, BucketValues> e : sites.entrySet())
         buckets.put(e.getKey(), new TokenBucket(e.getValue().limit, e.getValue().rate));

      while (true) {
         int consumers = 200 + rnd.nextInt(800);
         System.out.println("\nConsumers: " + consumers);
         for (Entry<String, TokenBucket> e : buckets.entrySet()) {
            TokenBucket bucket = e.getValue();
            BucketValues values = sites.get(e.getKey());

            int gotThrough = 0;
            for (int i = 0; i < consumers; ++i)
               if (bucket.tryAcquire())
                  gotThrough++;

            System.out.printf("r=%4d\tb=%4d\t%4d\t%5.1f %%\n", values.rate, values.limit, gotThrough, (100 * (double) gotThrough / consumers));
         }
         Thread.sleep(1000);
      }
   }

   public static class BucketValues {
      int rate;
      int limit;

      public BucketValues(int rate, int limit) {
         this.rate = rate;
         this.limit = limit;
      }
   }
}
