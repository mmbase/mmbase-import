package org.mmbase.clustering;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * Main class of this class starts up a unicast sender and listener
 * and multicast sender and listener and connects those, effectively
 * allowing for one 'out lyer' server which via this small little
 * program connected to the local multicast network.
 * @author Michiel Meeuwissen
 */

public class Converter {


    public static void main(String[] argv) throws Exception {

        Map<String, String> argMap = readArgv(argv);
		configureLogging(argMap);

        final BlockingQueue<byte[]> uniToMultiNodes =  new LinkedBlockingQueue<byte[]>(64);
        final BlockingQueue<byte[]> multiToUniNodes =  new LinkedBlockingQueue<byte[]>(64);
        final Statistics stats = new Statistics();

        startUnicastReceiver(argMap, uniToMultiNodes);
        startUnicastSender(argMap, multiToUniNodes, stats);

        startMulticastReceiver(argMap, multiToUniNodes);
        startMulticastSender(argMap, uniToMultiNodes,stats);

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




	private static void startUnicastReceiver(Map<String, String> argMap, BlockingQueue<byte[]> uniToMultiNodes) throws IOException {
		final String[] unicast = argMap.get("unicastListen").split(":");
		final String unicastListenHost = unicast[0];
		final int unicastListenPort = Integer.parseInt(unicast[1]);
		final int unicastListenVersion = Integer.parseInt(argMap.get("unicastListenVersion"));


		org.mmbase.clustering.unicast.ChangesReceiver uniCastReceiver = new org.mmbase.clustering.unicast.ChangesReceiver(unicastListenHost, unicastListenPort, uniToMultiNodes, unicastListenVersion);
		uniCastReceiver.setMaxMessageSize(Integer.parseInt(argMap.get("unicastListenMaxMessageSize")));
		uniCastReceiver.start();
	}

	private static void startUnicastSender(Map<String, String> argMap, BlockingQueue<byte[]> multiToUniNodes, Statistics stats) {
		final int unicastSendVersion = Integer.parseInt(argMap.get("unicastSendVersion"));
		org.mmbase.clustering.unicast.ChangesSender uniCastSender = new org.mmbase.clustering.unicast.ChangesSender(null, 4123, 10 * 1000, multiToUniNodes, stats, unicastSendVersion);
		uniCastSender.setOtherMachines(argMap.get("unicastSend"));
		uniCastSender.setCollectTime(Integer.parseInt(argMap.get("unicastSendCollectTime")));
		uniCastSender.setCollectCount(Integer.parseInt(argMap.get("unicastSendCollectCount")));
		uniCastSender.start();
	}


	private static void startMulticastReceiver(Map<String, String> argMap, BlockingQueue<byte[]> multiToUniNodes) throws UnknownHostException {
		int dpsize = 64 * 1024;
		String[] multicast = argMap.get("multicast").split(":");
		final String multicastHost = multicast[0];
		final int multicastPort = Integer.parseInt(multicast[1]);

		org.mmbase.clustering.multicast.ChangesReceiver multiCastReceiver = new org.mmbase.clustering.multicast.ChangesReceiver(multicastHost, multicastPort, dpsize, multiToUniNodes);
		multiCastReceiver.start();
	}

	private static void startMulticastSender(Map<String, String> argMap, BlockingQueue<byte[]> uniToMultiNodes, Statistics stats) throws UnknownHostException {
		int multicastTimeToLive = 1;
		String[] multicast = argMap.get("multicast").split(":");
		final String multicastHost = multicast[0];
		final int multicastPort = Integer.parseInt(multicast[1]);

		org.mmbase.clustering.multicast.ChangesSender multiCastSender
				= new org.mmbase.clustering.multicast.ChangesSender(multicastHost, multicastPort, multicastTimeToLive, uniToMultiNodes, stats);
		multiCastSender.start();
		//multiCastSender.getSocket().setLoopbackMode(true);
	}


	private static void configureLogging(Map<String, String> argMap) {
		try {
			java.lang.reflect.Method m = org.mmbase.util.logging.SimpleTimeStampImpl.class.getMethod("configure", String.class, String.class);
			m.invoke(null, "org.mmbase.clustering", argMap.get("log"));
		} catch (Exception t) {
			System.err.println(t.getMessage());
		}
	}

	private static Map<String, String> readArgv(String[] argv) {
		Map<String, String> argMap = new LinkedHashMap<String, String>();
		//argMap.put("unicastListen", InetAddress.getLocalHost().getHostName() + ":4123");
		argMap.put("unicastListen", "*:4123");
		argMap.put("unicastListenVersion", "2");
		argMap.put("unicastListenMaxMessageSize", "" + (5 * 1024 * 1024));
		argMap.put("unicastSend", "otherhost:4123:mmbase:2");
		argMap.put("unicastSendCollectTime", "5");
		argMap.put("unicastSendCollectCount", "50");
		argMap.put("unicastSendVersion", "2");
		argMap.put("multicast", org.mmbase.clustering.multicast.Multicast.HOST_DEFAULT + ":" + org.mmbase.clustering.multicast.Multicast.PORT_DEFAULT);
		argMap.put("log", "stdout,debug");


		for (String arg : argv) {
			String[] split = arg.split("=", 2);
			if (split.length == 2) {
				if (argMap.containsKey(split[0])) {
					argMap.put(split[0], split[1]);
				} else {
					System.err.println("Unrecognized option '" + arg + "'. Options are " + argMap);
					System.exit(1);
				}
			} else {
				System.err.println("Unrecognized option '" + arg + "'. Options are " + argMap);
				System.exit(2);
			}
		}
		return argMap;

	}
}
