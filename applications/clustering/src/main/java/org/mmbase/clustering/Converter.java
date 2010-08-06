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



        Map<String, String> argMap = new HashMap<String, String>();
        //argMap.put("unicastListen", InetAddress.getLocalHost().getHostName() + ":4123");
        argMap.put("unicastListen", "*:4123");
        argMap.put("unicastSend", "otherhost:4123:mmbase");
        argMap.put("unicastSendCollectTime", "5");
        argMap.put("unicastSendCollectCount", "50");
        argMap.put("multicast", org.mmbase.clustering.multicast.Multicast.HOST_DEFAULT + ":" + org.mmbase.clustering.multicast.Multicast.PORT_DEFAULT);
        argMap.put("log", "stdout,debug");


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

        org.mmbase.util.logging.SimpleTimeStampImpl.configure("org.mmbase.clustering", argMap.get("log"));


        final BlockingQueue<byte[]> uniToMultiNodes =  new LinkedBlockingQueue<byte[]>(64);
        final BlockingQueue<byte[]> multiToUniNodes =  new LinkedBlockingQueue<byte[]>(64);

        String[] unicast = argMap.get("unicastListen").split(":");
        final String unicastListenHost = unicast[0];
        final int unicastListenPort    = Integer.parseInt(unicast[1]);
        final int unicastListenVersion = 2;


        int dpsize = 64 * 1024;
        String[] multicast = argMap.get("multicast").split(":");
        final String multicastHost = multicast[0];
        final int multicastPort    = Integer.parseInt(multicast[1]);
        int multicastTimeToLive = 1;

        Statistics stats = new Statistics();

        org.mmbase.clustering.unicast.ChangesReceiver uniCastReceiver = new org.mmbase.clustering.unicast.ChangesReceiver(unicastListenHost, unicastListenPort, uniToMultiNodes, 2);
        uniCastReceiver.start();


        org.mmbase.clustering.unicast.ChangesSender uniCastSender     = new org.mmbase.clustering.unicast.ChangesSender(null, 4123, 10 * 1000, multiToUniNodes, stats, 2);
        uniCastSender.setOtherMachines(argMap.get("unicastSend"));
        uniCastSender.setCollectTime(Integer.parseInt(argMap.get("unicastSendCollectTime")));
        uniCastSender.setCollectCount(Integer.parseInt(argMap.get("unicastSendCollectCount")));
        uniCastSender.start();

        org.mmbase.clustering.multicast.ChangesReceiver multiCastReceiver = new org.mmbase.clustering.multicast.ChangesReceiver(multicastHost, multicastPort, dpsize, multiToUniNodes);
        multiCastReceiver.start();

        org.mmbase.clustering.multicast.ChangesSender multiCastSender
            = new org.mmbase.clustering.multicast.ChangesSender(multicastHost, multicastPort, multicastTimeToLive, uniToMultiNodes, stats);
        multiCastSender.start();
        //multiCastSender.getSocket().setLoopbackMode(true);

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