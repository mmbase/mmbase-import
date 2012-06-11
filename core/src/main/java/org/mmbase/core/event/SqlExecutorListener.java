/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;
import org.mmbase.util.logging.*;

/**

 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.5
 * @version $Id: WeakNodeEventListener.java 34900 2009-05-01 16:29:42Z michiel $
 */
public class SqlExecutorListener extends org.mmbase.util.SqlExecutor implements SystemEventListener {
    private static final Logger LOG = Logging.getLoggerInstance(SqlExecutorListener.class);

    public SqlExecutorListener() {
        LOG.debug("Instantiated " + this + " because ", new Exception());
    }

    public void notify(SystemEvent se) {
        if (se instanceof SystemEvent.DataSourceAvailable) {
            SystemEvent.DataSourceAvailable av = (SystemEvent.DataSourceAvailable) se;
            setPrefix(av.getPrefix());
            setDataSource(av.getDataSource());
            run();
        } else {
        }
    }
    public int getWeight() {
        return 0;
    }

    @Override
    public String toString() {
        return SqlExecutorListener.class.getName() + "@" + hashCode() + " " + super.toString();
    }

}
