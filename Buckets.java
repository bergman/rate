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
      if (args.length != 2)
         System.out.println("parameters: rate consumers");
      int maxConsumers = Integer.parseInt(args[1]);
      int rate = Integer.parseInt(args[0]);
      Random rnd = new Random();

      Map<String, RateLimit> sites = new HashMap<String, RateLimit>();
      sites.put("foo", new RateLimit(rate, 200000));
      sites.put("bar", new RateLimit(rate, 200000));
      sites.put("baz", new RateLimit(rate, 200000));

      Map<String, TokenBucket> buckets = new HashMap<String, TokenBucket>();
      for (Entry<String, RateLimit> e : sites.entrySet())
         buckets.put(e.getKey(), new TokenBucket(e.getValue().limit, e.getValue().rate));

      long totalDropped = 0;
      long totalConsumers = 0;
      while (true) {
         int consumers = rnd.nextInt(maxConsumers);
         System.out.println();
         System.out.println("Consumers: " + consumers);
         System.out.printf("\trate\tlimit\tdrop\tclear\tbefore\tafter\n");
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
            System.out.printf("%s\t%4d\t%4d\t%4d\t%3.0f %%\t%4d\t%4d\n", e.getKey(), values.rate, values.limit, consumers - gotThrough,
                  (100 * (double) gotThrough / consumers), before, bucket.getTokens());
         }
         totalConsumers += consumers * sites.size();
         System.out.printf("Dropped: %3.0f %%\n", (double) 100 * totalDropped / totalConsumers);
         Thread.sleep(rnd.nextInt(500));
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
