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

      Map<String, RateLimit> sites = new HashMap<String, RateLimit>();
      sites.put("foo", new RateLimit(200, 1000));
      sites.put("bar", new RateLimit(400, 1000));
      sites.put("baz", new RateLimit(800, 1000));

      Map<String, TokenBucket> buckets = new HashMap<String, TokenBucket>();
      for (Entry<String, RateLimit> e : sites.entrySet())
         buckets.put(e.getKey(), new TokenBucket(e.getValue().limit, e.getValue().rate));

      long totalDropped = 0;
      long totalConsumers = 0;
      while (true) {
         int consumers = rnd.nextInt(1000);
         System.out.println();
         System.out.println("Consumers: " + consumers);
         for (Entry<String, TokenBucket> e : buckets.entrySet()) {
            TokenBucket bucket = e.getValue();
            RateLimit values = sites.get(e.getKey());

            int before = bucket.getTokens();
            int gotThrough = 0;

            for (int i = 0; i < consumers; ++i)
               if (bucket.tryAcquire())
                  gotThrough++;
               else
                  totalDropped++;
            System.out.printf("r=%4d\tb=%4d\t%4d\t%3.0f %%\tbefore & after: %4d\t%4d\n", values.rate, values.limit, consumers - gotThrough,
                  (100 * (double) gotThrough / consumers), before, bucket.getTokens());
         }
         totalConsumers += consumers * sites.size();
         System.out.printf("Dropped: %3.0f %%\n", (double) 100 * totalDropped / totalConsumers);
         Thread.sleep(500 + rnd.nextInt(500));
      }
   }

   public static class RateLimit {
      int rate;
      int limit;

      public RateLimit(int rate, int limit) {
         this.rate = rate;
         this.limit = limit;
      }
   }
}
