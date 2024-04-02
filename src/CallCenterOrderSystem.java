import javax.swing.text.StyledEditorKit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

public class CallCenterOrderSystem extends Thread {

    // this thread generates a textfile containing new orders every 2 minutes
    // everytime a new file 'with deferent Name" will be created
    // all files are stored in the folder <Orders>
    public void run() {

        String orderFolderPath = String.format("Orders");
        File recieverFolder = new File(String.format(orderFolderPath));
        recieverFolder.mkdirs();
        System.out.println("Generating Orders...");

        while (true) {
            try {
                Thread.sleep(120000);// 120000
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String fileName = String.format("%s/%s.txt", orderFolderPath, randomString(10));
            File file = new File(fileName);

            try {
                if (file.createNewFile()) {
                    // System.out.println("File is created!"+fileName);
                } else {
                    // System.out.println("File already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // creat orders
            String Order = "";
            List<String> orderList = new ArrayList<String>();
            // int ordersToCreat = randomInt(50)+1;
            int ordersToCreat = 5; // set only for creating the video
            for (int i = 0; i < ordersToCreat; i++) {
                // <Customer-ID, Full Name, Number of ordered surfboards, Numberof ordered
                // diving suits>
                orderList.add(String.format("%s%d, %s %s, %d, %d", Order, randomInt(99999), randomString(5),
                        randomString(6), randomInt(200), randomInt(200)));
            }
            String orderListstr = String.join("\n", orderList);
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.write(orderListstr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("New file " + fileName + " was generated!");

        }
    }

    public static int randomInt(int upperbound) {
        Random rand = new Random(); // instance of random class

        // generate random values from 0-24
        int int_random = rand.nextInt(upperbound);
        return int_random;
    }

    public static String randomString(int len) {
        String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();

    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Running CallCenterOrderSystem ...");
        CallCenterOrderSystem orderFilesGenerator = new CallCenterOrderSystem();

        // orderFileGenerator-thread will creat a file every 2 min
        orderFilesGenerator.start();
        // make main-thread wait for orderFilesGenerator-Thread
        orderFilesGenerator.join();

    }
}
