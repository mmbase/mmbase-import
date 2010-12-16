/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.net.*;
import java.io.*;
import java.util.*;


/**
 * Just to test the command server
 *
 * @author Michiel Meeuwissen
 */


public class MagickClient {

    public static int cnumber = 0;


    protected static class Job implements Runnable {
        private final int number = cnumber++;
        final List<String> command;
        final List<String> env;
        final String file;
        public Job(String file, List<String> c, List<String> env) {
            this.file = file;
            this.command = c;
            this.env = env;
        }

        @Override
        public void run()  {
            try {
                System.out.println(number + " command = " + command);
                InputStream is = new FileInputStream(file);
                OutputStream result = new FileOutputStream("/tmp/testresult" + number);

                try {
                    Socket socket = new Socket("localhost" , 1679);
                    OutputStream os = socket.getOutputStream();
                    os.write(0); // version
                    final ObjectOutputStream stream = new ObjectOutputStream(os);
                    stream.writeObject((String []) command.toArray(new String[] {}));
                    stream.writeObject(new String[] {});

                    CommandServer.Copier copier = new CommandServer.Copier(is, os, ".file -> socket");
                    Thread listen = new Thread(copier);
                    listen.start();

                    CommandServer.Copier copier2 = new CommandServer.Copier(socket.getInputStream(), result, ";socket -> cout");
                    Thread listen2 = new Thread(copier2);
                    listen2.start();

                    System.out.println(number + " Waiting for send");
                    copier.waitFor();
                    socket.shutdownOutput();

                    System.out.println(number + " Waiting for response");
                    copier2.waitFor();

                    socket.close();
                    System.out.println(number + " Result in /tmp/testresult" + number);
                } catch (java.net.ConnectException ce) {
                    System.err.println(ce.getMessage());
                    PipedInputStream input = new PipedInputStream();
                    OutputStream out = new PipedOutputStream(input);
                    out.write(1);
                    CommandServer.Copier copier = new CommandServer.Copier(input, out, ";file -> cout");
                    Thread listen = new Thread(copier);
                    listen.start();


                    Runnable run = new CommandServer.Command(input, result, System.err, "stdin/stdout", null);
                    run.run();
                }


                result.close();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            } catch (InterruptedException ie) {
                throw new RuntimeException(ie);
            }
        }
    }


    public static void main(String[] args) {
        final List<String> command = new ArrayList<String>(Arrays.asList(args).subList(1, args.length));
        final List<String> env     = new ArrayList<String>();
        final String file = args[0];

        for (int i = 1; i <= 50; i++) {
            Thread t = new Thread(new Job(file, command, env));
            t.start();
        }


    }

}
