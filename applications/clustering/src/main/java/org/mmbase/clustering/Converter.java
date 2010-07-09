package org.mmbase.clustering;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;
/**
 * Main class of this class starts up a unicast sender and listener
 * and multicast sender and listener and connects those, effectively
 * allowing for one 'out lyer' server which via this small little
 * program connected to the local multicast network.
 * @author Michiel Meeuwissen
 */

public class Converter {


    public static void main(String[] argv) throws Exception {


        org.mmbase.util.logging.SimpleTimeStampImpl.configure("org.mmbase.clustering", "stdout,debug");

        Map<String, String> argMap = new HashMap<String, String>();
        argMap.put("unicastListen", InetAddress.getLocalHost().getHostName() + ":4123");
        argMap.put("unicastSend", "otherhost:4123:mmbase");

        argMap.put("multicast", org.mmbase.clustering.multicast.Multicast.HOST_DEFAULT + ":" + org.mmbase.clustering.multicast.Multicast.PORT_DEFAULT);
        for (String arg : argv) {
            String[] split = arg.split("=", 2);
            if (split.length == 2) {
                if (argMap.containsKey(split[0])) {
                    argMap.put(split[0], split[1]);
                } else {
                    System.err.println("Unrecognized option " + arg + " Options are " + argMap);
                    System.exit(1);
                }
            } else {
                System.err.println("Unrecognized option " + arg + " Options are " + argMap);
                System.exit(2);
            }
        }


        final BlockingQueue<byte[]> uniToMultiNodes =  new LinkedBlockingQueue<byte[]>(64);
        final BlockingQueue<byte[]> multiToUniNodes =  new LinkedBlockingQueue<byte[]>(64);

        String[] unicast = argMap.get("unicastListen").split(":");
        final String unicastListenHost = unicast[0];
        final int unicastListenPort    = Integer.parseInt(unicast[1]);
        final int unicastListenVersion = 2;


        final List<org.mmbase.clustering.unicast.ChangesSender.OtherMachine> unicastSenders
            = new ArrayList<org.mmbase.clustering.unicast.ChangesSender.OtherMachine>();
        {
            String[] unicastHost = argMap.get("unicastSend").split(",");
            for (String unicastString : unicastHost) {
                if (unicastString.length() > 0) {
                    String[] unicastSend = unicastString.split(":", 3);
                    unicastSenders.add(new org.mmbase.clustering.unicast.ChangesSender.OtherMachine(unicastSend[0], unicastSend.length > 2 ? unicastSend[2] : null, Integer.parseInt(unicastSend[1]), 2));
                }
            }
        }


        int dpsize = 64 * 1024;
        String[] multicast = argMap.get("multicast").split(":");
        final String multicastHost = multicast[0];
        final int multicastPort    = Integer.parseInt(multicast[1]);
        int multicastTimeToLive = 1;

        Statistics stats = new Statistics();

        Runnable uniCastReceiver   = new org.mmbase.clustering.unicast.ChangesReceiver(unicastListenHost, unicastListenPort, uniToMultiNodes, 2);
        Runnable uniCastSender     = new org.mmbase.clustering.unicast.ChangesSender(null, 4123, 10 * 1000, multiToUniNodes, stats, 2) {
                @Override
                protected Iterable<OtherMachine> getOtherMachines() {
                    return unicastSenders;
                }
                @Override
                protected int remove(OtherMachine mach) {
                    return 0;
                }
            };


        Runnable multiCastReceiver   = new org.mmbase.clustering.multicast.ChangesReceiver(multicastHost, multicastPort, dpsize, multiToUniNodes);
        org.mmbase.clustering.multicast.ChangesSender multiCastSender
            = new org.mmbase.clustering.multicast.ChangesSender(multicastHost, multicastPort, multicastTimeToLive, uniToMultiNodes, stats);
        multiCastSender.getSocket().setLoopbackMode(true);

        synchronized(Converter.class) {


            System.out.println("Waiting for interrupt");
            try {
                Converter.class.wait();
                System.out.println("INTERRUPTED");
            } catch (InterruptedException ie) {
                System.out.println(ie.getMessage());
            }
        }
    }
}