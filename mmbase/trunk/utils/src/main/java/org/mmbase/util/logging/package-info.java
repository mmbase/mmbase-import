/**
 * The MMBase logging infrastructure. The main classes of this
 * package are {@link org.mmbase.util.logging.Logger}, which provides the interface for logging
 * implementations (because the precise implementation is
 * pluggable), and {@link org.mmbase.util.logging.Logging}, which takes care of the configuration
 * and setting up of the logging system.
 * <p>
 * Another import class is {@link org.mmbase.util.logging.Level} which describes the possible
 * logging levels currently known by MMBase.
 * <p>
 * {@link org.mmbase.util.logging.SimpleImpl} is the most basic implementation of {@link org.mmbase.util.logging.Logger} but a
 * more sophisticated one, based on log4j (@link org.mmbase.util.logging.log4j.Log4jImpl}), can be found in a
 * subpackage.
 * @version $Id$
 * @since   MMBase-1.4
 */
package org.mmbase.util.logging;