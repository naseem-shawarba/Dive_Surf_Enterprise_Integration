import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IDGenerator {
    private static IDGenerator instance = null;

    Map<Integer, String> usedIDs = Collections.synchronizedMap(new HashMap<Integer, String>());// thread safe map

    IDGenerator() {

    }

    public static synchronized IDGenerator getInstance() {
        if (instance == null) {
            instance = new IDGenerator();
        }
        return instance;
    }

    public synchronized int generateNewID() {
        Random rand = new Random(); // instance of random class
        int int_random = rand.nextInt(99999);
        while (usedIDs.containsKey(int_random)) {
            int_random = rand.nextInt(99999);
        }
        usedIDs.put(int_random, "");
        return int_random;

    }

}
