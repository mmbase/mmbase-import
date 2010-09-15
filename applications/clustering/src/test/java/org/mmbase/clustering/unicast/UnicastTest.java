package org.mmbase.clustering.unicast;
import org.mmbase.clustering.Statistics;
import org.mmbase.util.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.*;

public class UnicastTest {


    protected List<byte[]> getTestSet() {
        // Make up a bunch of random byte arrays
        Random random = new Random(2);
        List<byte[]> testSet = new ArrayList<byte[]>();

        for (int i = 0; i < 100; i++) {
            byte[] message = new byte[1 + i * random.nextInt(2000)];
            random.nextBytes(message);
            testSet.add(message);
        }
        return testSet;
    }

    @BeforeClass
    public static void setup() {
        try {
            java.lang.reflect.Method m = org.mmbase.util.logging.SimpleTimeStampImpl.class.getMethod("configure", String.class, String.class);
            m.invoke(null, "org.mmbase.clustering", "trace");
        } catch (Exception t) {
            System.err.println(t.getMessage());
        }
    }
    @Test
    public void streams() throws IOException {
        final ChangesReceiver receiver = new ChangesReceiver(null, -1, null, 2);
        //receiver.setMaxMessageSize(90000);
        final ChangesSender   sender   = new ChangesSender(new HashMap<String, String>(), -1, 100, null, new Statistics(), 2);


        List<byte[]> testSet = getTestSet();

        // write them to one giant byte array in memory
        System.out.println("Test set: " + testSet);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        sender.writeVersion2(buffer, testSet);


        // Read the byte array in again
        LinkedList<byte[]> queue = new LinkedList<byte[]>();
        InputStream in = new ByteArrayInputStream(buffer.toByteArray());
        receiver.readStreamVersion2(in, queue);

        // And check if the result is the same as what went in

        assertEquals(testSet.size(), queue.size());
        for (int i = 0 ; i < testSet.size(); i++) {
            assertArrayEquals(testSet.get(i), queue.get(i));
        }

    }


    @Test
    public void connections() throws IOException, InterruptedException {
        final int port = 1234;
        final InetSocketAddress address = new InetSocketAddress("localhost", port);
        final ChangesReceiver receiver = new ChangesReceiver(null, -1, null, 2);
        //receiver.setMaxMessageSize(90000);
        final ChangesSender   sender   = new ChangesSender(new HashMap<String, String>(), -1, 10000, null, new Statistics(), 2);
        final LinkedList<byte[]> queue = new LinkedList<byte[]>();
        final List<byte[]> testSet = getTestSet();
        final ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(address);
        final List<Integer> state = new ArrayList<Integer>();

        Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Listening on " + address);
                        synchronized(state) {
                            Socket socket = serverSocket.accept();
                            System.out.println("Received connection " + socket);
                            receiver.readStreamVersion2(socket.getInputStream(), queue);
                            System.out.println("Ready reading " + queue.size());
                            state.add(1);
                            state.notifyAll();
                        }
                    } catch (Exception e) {
                        System.err.println("ERROR: " + e.getMessage());
                    }
                }
            });
        thread.setDaemon(true);
        thread.start();


        System.out.println("Sending to " + address + " " + testSet.size() + " messages");
        sender.sendVersion2(address, testSet);
        System.out.println("Ready sending");
        synchronized(state) {
            while(state.size() == 0) {
                state.wait();
                System.out.println("" + state.size());
            }
        }

        assertEquals(testSet.size(), queue.size());
        for (int i = 0 ; i < testSet.size(); i++) {
            assertArrayEquals(testSet.get(i), queue.get(i));
        }



    }

}