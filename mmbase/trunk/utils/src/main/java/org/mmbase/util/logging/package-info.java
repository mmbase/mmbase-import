package org.mmbase.util.logging;
/**
 * The MMBase logging infrastructure. The main classes of this
 * package are {@link Logger}, which provides the interface for logging
 * implementations (because the precise implementation is
 * pluggable), and {@link Logging}, which takes care of the configuration
 * and setting up of the logging system.
 * <p>
 * Another import class is {@link Level} which describes the possible
 * logging levels currently known by MMBase.
 * <p>
 * {@link SimpleImpl} is the most basic implementation of {@link Logger} but a
 * more sophisticated one, based on log4j, can be found in a
 * subpackage.
 * @version $Id$
 * @since   MMBase-1.4
 */
