/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.database.support;

import java.lang.reflect.Constructor;
import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.database.*;
import org.mmbase.util.XMLDatabaseReader;
import org.mmbase.util.logging.*;

/**
 * This class serves as baseclass for database support classes.
 * It implements the {@link org.mmbase.storage.search.SearchQueryHandler
 * SearchQueryHandler} interface by delegating all calls to this interface
 * to a
 * {@link org.mmbase.storage.search.implementation.database.BasicQueryHandler
 * BasicQueryHandler} instance.
 * Call {@link #init(Map,XMLDatabaseReader) init()} to initialize the instance.
 * based on the database configuration XML file..
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author  Rob van Maris
 * @version $Id: BaseJdbc2Node.java,v 1.3 2004-01-27 12:04:47 pierre Exp $
 * @since MMBase-1.7
 */
public abstract class BaseJdbc2Node implements SearchQueryHandler {

    private final static String DEFAULT_SQL_HANDLER =
        "org.mmbase.storage.search.implementation.database.BasicSqlHandler";

    /**
     * Logging instance
     */
    private static Logger log
        = Logging.getLoggerInstance(BaseJdbc2Node.class.getName());

    /**
     * The SearchQueryHandler to delegate to.
     * The SearchQueryHandler interface is implemented by delegating all
     * calls to the interface's methods to this SearchQuery instance.
     */
    private SearchQueryHandler queryHandler = null;

    /** Creates a new instance of BaseJdbc2Node */
    protected BaseJdbc2Node() {}

    /**
     * Initializes the instance, based on the database configuration
     * XML file.
     *
     * @param disallowedValues Map mapping disallowed table/fieldnames
     *        to allowed alternatives.
     * @param parser The parser that reads the database configuration
     *        from an XML file.
     */
    protected void init(Map disallowedValues, XMLDatabaseReader parser) {
        // Initialize sql handler.
        String sqlHandlerName = parser.getSqlHandler();
        if (sqlHandlerName == null || sqlHandlerName.trim().length() == 0) {
            sqlHandlerName = DEFAULT_SQL_HANDLER;
        }
        SqlHandler sqlHandler = null;
        try {
            Class sqlHandlerClass = Class.forName(sqlHandlerName);
            Constructor constr1
                = sqlHandlerClass.getConstructor(new Class[] {Map.class});
            sqlHandler = (SqlHandler)
                constr1.newInstance(new Object[] {disallowedValues});
        } catch (Exception e) {
            log.fatal("Unable to instantiate SqlHandler of type "
                + sqlHandlerName);
            throw new RuntimeException(e.toString());
        }
        log.info("Instantiated SqlHandler of type " + sqlHandlerName);

        // Chained handlers.
        Iterator iHandlers = parser.getChainedSqlHandlers().iterator();
        while (iHandlers.hasNext()) {
            String handlerName = (String) iHandlers.next();
            if (handlerName == null || handlerName.trim().length() == 0) {
                continue;
            }
            try {
                Class handlerClass = Class.forName(handlerName);
                Constructor constr2 =
                    handlerClass.getConstructor(new Class[] {SqlHandler.class});
                sqlHandler = (SqlHandler)
                    constr2.newInstance(new Object[] {sqlHandler});
            } catch (Exception e) {
                log.fatal("Unable to instantiate chained SqlHandler of type "
                    + handlerName);
                throw new RuntimeException(e.toString());
            }
            log.info("Instantiated chained SqlHandler of type "
                + handlerName);
        }

        // initialize query handler.
        queryHandler = new BasicQueryHandler(sqlHandler);
        log.info("Instantiated QueryHandler of type "
            + BasicQueryHandler.class.getName());
    }

    // javadoc is inherited
    public int getSupportLevel(Constraint constraint, SearchQuery query) throws SearchQueryException {
        return queryHandler.getSupportLevel(constraint, query);
    }

    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException {
        return queryHandler.getSupportLevel(feature, query);
    }

    // javadoc is inherited
    public List getNodes(SearchQuery query, MMObjectBuilder builder) throws SearchQueryException {
        return queryHandler.getNodes(query, builder);
    }

}
