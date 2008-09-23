package com.finalist.newsletter.publisher.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

/**
 * @author nikko
 * 
 */
public class CacheFreshTask extends TimerTask {
   private DefaultCache cache;

   /**
    * @param cache this is a map info bean
    */
   public CacheFreshTask(DefaultCache cache) {
      this.cache = cache;
   }

   /**
    * Start Thread
    * @see java.util.TimerTask#run()
    */
   public void run() {
      synchronized (cache.getDatas()) {
         Iterator < Map.Entry < String , CacheInfo > > iterator = cache.getDatas().entrySet().iterator();
         while (iterator.hasNext()) {
            Map.Entry < String , CacheInfo > entry = iterator.next();
            CacheInfo ci = entry.getValue();
            if (ci.getTotalSeconds() != ICache.FOREVER) {
               ci.setSecondsRemain(ci.getSecondsRemain() - 1);
               if (ci.getSecondsRemain() <= 0) {
                  iterator.remove();
               }
            }
         }
      }
   }

}
