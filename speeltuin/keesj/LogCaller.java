import java.io.*;
import java.util.*;

public class LogCaller {
    public static String getCaller() {
        Exception e = new Exception("stacktrace");
        StackTraceElement[] stack = e.getStackTrace();
        //0 == this excpetion
        //1 == this method
        //2 == what we are looking for
        StackTraceElement element = stack[2];
        return element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber();
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
