package nl.vpro.redactie.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.Relation;
import org.mmbase.util.logging.Logging;
import org.springframework.web.servlet.ModelAndView;

/**
 * This class handles flushing of 3voor12 specific relation caches.
 * @author ebunders
 *
 */
public class DrieVoorTwaalfCacheHandlerInterceptor extends CacheHandlerInterceptor {
    private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(DrieVoorTwaalfCacheHandlerInterceptor.class);

    public DrieVoorTwaalfCacheHandlerInterceptor(){
        //de relation flush handling
        handlings.add(new Handling(CacheFlushHint.TYPE_RELATION){
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                Relation relation = (Relation) hint.getProperty("relation");
                if (relation == null) {
                    throw new RuntimeException("relation is null for relation type cache flush hint");
                }
                OscacheFlush.flushRelation(request, relation);
                log.debug("flushed relation");
            }
        });

        //en nu de request handling
        handlings.add(new Handling(CacheFlushHint.TYPE_REQUEST){
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                OscacheFlush.flush(request);
                log.debug("flushed request");
            }
        });
    }
}
