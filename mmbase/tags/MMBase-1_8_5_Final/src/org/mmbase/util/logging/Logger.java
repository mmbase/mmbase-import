/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

/**
 * The `Logger' interface for MMBase.
 *
 * It was designed for use with Log4j, but it is of course easy to
 * implement it differently as well.
 * <p>
 * Implementations should also supply a getLoggerInstance static
 * method, and can supply a configure static method.
 * </p>
 * </p>
 * For example:
 * <code>
 * <pre>
 * <tt>
 * <b><font color=#0000FF>import</font></b> org.mmbase.util.logging.Logging;
 * <b><font color=#0000FF>import</font></b> org.mmbase.util.logging.Logger;
 *
 * <b><font color=#0000FF>public</font></b> <b><font color=#0000FF>class</font></b> Foo {
 *     <b><font color=#0000FF>static</font></b> Logger log = Logging.getLoggerInstance(Foo.<b><font color=#0000FF>class</font></b>.getName());
 *     <b><font color=#0000FF>public</font></b> <font color=#009900>void</font> bar() {
 *         ...
 *         log.info(<font color=#FF0000>"Hello world!"</font>);
 *         ...
 *         <b><font color=#0000FF>if</font></b>(log.isDebugEnabled()) {
 *             log.debug(<font color=#FF0000>"Oops, that's not quite right!"</font>);
 *         }
 *         ...
 *     }
 * }
 * </tt>
 * </pre>
 * </code>
 * </p>
 *
 * @author Michiel Meeuwissen
 *
 **/

public interface Logger {

    // these static methods should also be implemented:
    // public static void configure(String s);  // well, this one is optional
    // public static Logger getLoggerInstance(String name);

    /**
     * Logs the message m with trace priority. For detailled debugging.
     * @see #debug(Object)
     */
    void trace(Object m);

    /**
     * @since MMBase-1.8
     */

    void trace(Object m, Throwable t);

    /**
     * Logs the message m with debug priority. Everything a
     * non-developer never wants to see, but you do, to * keep track
     * of what is happening. There can be a lot of them in the code,
     * so it is important that you well protect them with
     * `isDebugEnabled's, to minimize overhead.
     */
    void debug(Object m);

    /**
     * @since MMBase-1.8
     */

    void debug(Object m, Throwable t);


    /**
     * Logs the message m with service priority. An interested system
     * administrator might want to see these things. For examples all
     * queries to the database could be logged with `service'
     * priority. Or if a image is calculated, that could be logged as
     * a `service'. One would get a fairly good idea what MMBase is
     * doing if `service' is switched on.
     */
    void service(Object m);

    /**
     * @since MMBase-1.8
     */

    void service(Object m, Throwable t);


    /**
     * Logs the message m with info priority. As `service', but
     * focussed on things system administrators are usually most
     * interested in, like authorisation issues. For example changes on
     * the database could be logged, such that one can see in the logs
     * what happened.
     */
    void info(Object m);

    /**
     * @since MMBase-1.8
     */
    void info(Object m, Throwable t);


    /**
     * Logs the message m with warn priority. Something strange
     * happened, but it is not necessarily an error.
     */
    void warn(Object m);

    /**
     * @since MMBase-1.8
     */
    void warn(Object m, Throwable t);


    /**
     * Logs the message m with error priority. Something is definitely
     * wrong. An inconsistency was detected. It might be unpredictable
     * what will happen.
     */
    void error(Object m);

    /**
     * @since MMBase-1.8
     */
    void error(Object m, Throwable t);


    /**
     * Logs the message m with fatal priority. The progam could not
     * function any more. Normally, you would throw an exception,
     * which then will be logged with fatal priority. I've made an
     * arangement in `Logger' that logs uncatched exceptions with
     * fatal priority, but nevertheless it's better to always catch
     * all exceptions in a more regulated way.
     */
    void fatal(Object m);

    /**
     * @since MMBase-1.8
     */
    void fatal(Object m, Throwable t);

    /**
     * Returns true if for this category (Logger), a call to trace
     * would do something.
     */
    public boolean isTraceEnabled();


    /**
     * Returns true if for this category (Logger), a call to debug (or
     * trace) would do something.
     */
    public boolean isDebugEnabled();
    // public boolean isInfoEnabled();

    /**
     * Returns true if for this category (Logger), a call to service
     * (or debug or trace) would do something.
     */
    public boolean isServiceEnabled();


    /**
     * If you want to override the level in the configuration file
     * fixed for this category, you can do it with this method. This
     * could be usefull for example to switch on all debug logging
     * when something has gone wrong.
     * @param p The level of the priority. One of the constants
     * Level.TRACE, Level.DEBUG, Level.SERVICE, Level.INFO,
     * Level.WARN, Level.ERROR or Level.FATAL.
     */

    public void setLevel(Level p);

}
