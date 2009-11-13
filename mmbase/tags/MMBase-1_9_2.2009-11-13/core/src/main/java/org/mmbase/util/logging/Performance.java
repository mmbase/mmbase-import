/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging;

import org.mmbase.util.ResourceLoader;
import org.apache.log4j.Category;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This program is meant to show the performance of the
 * org.mmbase.util.logging classes. You can also easily check the
 * performance of a configuration with it. Simply feed it the configuration file, and it will 
 * try to log (info priority) to the class A0123456789.B0123456789.C0123456789.
 * It has several other command line options (starting with -).
 *
 **/


public class Performance {

    static final double SECOND = 1000;  // one second in milliseconds.
    
    static int     repeats          = 1000;
    static boolean isdebugenabled   = false;
    static boolean nosystem         = false;
    static boolean log4j            = false;
    static String  description      = null;
    static boolean delay            = false;
    static int     delaytime        = 10;
    static int     burstLen         = 3;
    static int     warmingup        = 200;

    static double doCaseLog4j(String s) {
        DOMConfigurator.configure(s);
        int i;
        Category cat = Category.getInstance("A0123456789.B0123456789.C0123456789");
        for (i = 0; i < warmingup; i++) {
            cat.info("warming up.");
        }
        long before = System.currentTimeMillis();
        for (i = 0; i < repeats; i++) {
            cat.info("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
        }
        long after = System.currentTimeMillis();
        return (double)1000*(after - before)/ repeats;

    }

    static double doCaseNoSystem() {
        int i;
        for (i = 0; i < warmingup; i++) {
            System.err.println("warming up.");
        }
        long before = System.currentTimeMillis();
        for (i = 0; i < repeats; i++) {
            System.err.println("INFO abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
        }
        long after = System.currentTimeMillis();
        return (double)1000*(after - before)/ repeats;
    }
        
    static double doCase(Logger log) {
        if (delay) {
            return doCaseDelayed(log);
        }
        int i;
        for (i = 0; i < warmingup; i++) {
            log.info("warming up.");
        }
        long before = System.currentTimeMillis();
        for (i = 0; i < repeats; i++) {
            log.info("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");            
        }
        long after = System.currentTimeMillis();
        return (double)1000*(after - before)/ repeats;
    }

    static double doCaseDelayed(Logger log) {
        int j = 0;
        int i;
        try {
        // Warming up makes the results better reproducable.
        for (i = 0; i < warmingup; i++) {
            log.info("warming up.");
            if(++j == burstLen) {
                j = 0;                               
                Thread.sleep(delaytime);                
            }
        }
        long before = System.currentTimeMillis();        
        for (i = 0; i < repeats; i++) {
            log.info("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");            
            if(++j == burstLen) {
                j = 0;                               
                Thread.sleep(delaytime);                
            }
        }
        long after = System.currentTimeMillis();
        j = 0;
        // sleep a little in between.
        Thread.sleep(3000);

        long before_ref = System.currentTimeMillis();
        // do the same but without logging (trace will not be logged, and we've seen that it's time is neglectable).
        for (i = 0; i < repeats; i++) {
            log.trace("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");            
            if(j++ == burstLen) {
                j = 0;               
                Thread.sleep(delaytime);
            }
        }

        long after_ref = System.currentTimeMillis();

        //System.out.println("(with: " + (after - before) + " without " + (after_ref - before_ref) + " ) ");
        return (double)1000*(after - before - (after_ref - before_ref))/ repeats;
        } catch (Exception e) {}
        return 0;
    }

    static double doCaseIfDebug(Logger log) {
        int i;
        for (i = 0; i < warmingup; i++) {
            if (log.isDebugEnabled()) {
                log.debug("warming up.");
            }
        }
        long before = System.currentTimeMillis();
        for (i = 0; i < repeats; i++) {
            if (log.isDebugEnabled()) {
                log.debug("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
            }
        }
        long after = System.currentTimeMillis();
        return (double)1000*(after - before)/ repeats;
    }

    static double Case(String configuration) {
        if(log4j) {
            return doCaseLog4j(configuration);
        }
        if(nosystem) {
            return doCaseNoSystem();
        }

        Logging.configure(ResourceLoader.getConfigurationRoot(), configuration);
        Logger log = Logging.getLoggerInstance("A0123456789.B0123456789.C0123456789");

        if(isdebugenabled) {
            return doCaseIfDebug(log);
        } else {
            return doCase(log);
        }

    }

    static void  printCase(String configuration) {
        String s = (description == null ? "" : description) + " (" + configuration + ") : ";
        for (int i = s.length(); i< 50; i++) {
            s += " "; // damn, sprintf would be nice..
        }   
        System.out.print(s);

        double benchmark = Case(configuration);
        
        System.out.println(benchmark + " us/logging"); // we follow the example of log4j and report in microseconds (us)
    }

    public static void main(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].charAt(0) == '-') { // an command line option
                if(args[i].substring(1).equals("repeats")){
                    repeats = Integer.valueOf(args[++i]).intValue();
                }
                if(args[i].substring(1).equals("isdebugenabled")){
                    isdebugenabled = ! isdebugenabled;
                }
                if(args[i].substring(1).equals("nosystem")){                   
                    nosystem = ! nosystem;
                }
                if(args[i].substring(1).equals("log4j")){
                    log4j = ! log4j;
                }
                if(args[i].substring(1).equals("desc")){
                    description = args[++i];
                }
                if(args[i].substring(1).equals("delay")){
                    delay = ! delay;
                }
                if(args[i].substring(1).equals("delaytime")){
                    delaytime = Integer.valueOf(args[++i]).intValue();
                }
                if(args[i].substring(1).equals("burstlen")){
                    burstLen = Integer.valueOf(args[++i]).intValue();
                }
            } else {
                warmingup = repeats / 10;
                printCase(args[i]);                   
            }
        }
        Logging.shutdown();
    }
}
