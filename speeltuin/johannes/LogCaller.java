/**
 * This class returns to you the calling method. This can
 * be extremely helpful during debugging, because you can print
 * wherefrom your method was called.
 * Note that this code is VERY VERY VERY DIRTY!!!. It was only
 * tested with the 1.4.1 JVM for Sun on Linux, and will probably
 * not work on any other JVM.
 */

import java.io.*;
import java.util.*;

public class LogCaller {


    /**
     * Redirect the 'dumpStack' call of the current thread
     * to a private PrintStream instead of stderr (this might
     * be not thread-safe!). Then parse the stacktrace; this probably
     * only works on JVM 1.4.1, since the layout of a stacktrace
     * may vary between JVM's.
     */
    public static String getCaller() {
        PrintStream oldErr = System.err;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream newstream = new PrintStream(bos, true);
        System.setErr(newstream);
        Thread.currentThread().dumpStack();
        System.setErr(oldErr);
        String trace = bos.toString();
        StringTokenizer st = new StringTokenizer(trace, "\n");
        st.nextToken();
        st.nextToken();
        st.nextToken();
        st.nextToken();
        
        String retval = st.nextToken().trim();
        st = new StringTokenizer(retval, " ");
        st.nextToken();

        return st.nextToken();
    }

    // DEBUG CODE
    private static void a() {
        b();
    }

    private static void b() {
        System.out.println("I was called from '" + LogCaller.getCaller() + "'");
    }

    public static void main(String[] args) {
        a();
        b();
    }
}
