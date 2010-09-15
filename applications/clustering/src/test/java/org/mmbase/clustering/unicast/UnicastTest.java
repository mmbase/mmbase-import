package org.mmbase.clustering.unicast;
import org.mmbase.clustering.Statistics;
import org.mmbase.util.*;
import java.util.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.*;

public class UnicastTest {

    @Test
    public void basic() throws IOException {
        ChangesReceiver receiver = new ChangesReceiver(null, -1, null, 2);
        //receiver.setMaxMessageSize(90000);
        ChangesSender   sender   = new ChangesSender(new HashMap<String, String>(),
                                                     -1, 100, null, new Statistics(), 2);

        // not test if we can send
        // make up some bytes
        Random random = new Random(2);
        List<byte[]> testSet = new ArrayList<byte[]>();

        for (int i = 0; i < 100; i++) {
            byte[] message = new byte[1 + i * 1000];
            random.nextBytes(message);
            testSet.add(message);
        }
        System.out.println("Test set: " + testSet);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        sender.writeVersion2(buffer, testSet);

        LinkedList<byte[]> queue = new LinkedList<byte[]>();
        InputStream in = new ByteArrayInputStream(buffer.toByteArray());
        receiver.readStreamVersion2(in, queue);

        assertEquals(testSet.size(), queue.size());
        for (int i = 0 ; i < testSet.size(); i++) {
            byte[] array1 = testSet.get(i);
            byte[] array2 = queue.get(i);
            assertTrue(Arrays.equals(array1, array2));
        }

    }

}