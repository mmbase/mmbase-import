/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

import java.io.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Michiel Meeuwissen
 */
public class CommandExecutorTest {


    String getJava() {
	String java_home = System.getenv("JAVA_HOME");
	return java_home == null ? "java" : java_home + File.separator + "bin" + File.separator + "java";
    }


    @Test
    public void stdout() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        CommandExecutor.execute(out, err, new CommandExecutor.Method(), getJava(), "-cp", "target" + File.separator + "test-classes",  CommandExecutorTest.class.getName(), "stdout", "hello");
        assertEquals("hello", out.toString().trim());
        assertEquals("", err.toString());
    }
    @Test
    public void stderr() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        CommandExecutor.execute(out, err, new CommandExecutor.Method(), getJava(), "-cp", "target" + File.separator + "test-classes", CommandExecutorTest.class.getName(), "stderr", "hello");
        assertEquals("", out.toString().trim());
        assertEquals("hello", err.toString().trim());
    }


    public static void main(String[] argv) {
        assert argv.length == 2;
        if (argv[0].equals("stdout")) {
            System.out.println(argv[1]);
        } else if (argv[0].equals("stderr")) {
            System.err.println(argv[1]);
        } else {
            throw new RuntimeException("First argument should be stdout or stdout");
        }
    }

}
