import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.*;
import javax.jms.*;
import javax.jms.Queue;
import org.apache.activemq.ActiveMQConnectionFactory;

public class WebOrderSystem {

    ActiveMQConnectionFactory conFactory;
    Connection con;
    Session session;
    Queue WEB_NEW_ORDER;
    MessageProducer orderProducer;

    public WebOrderSystem() throws JMSException {
        setupConnection();
    }

    public void setupConnection() throws JMSException {
        conFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        conFactory.setTrustAllPackages(true); // i know that this line unsafe
        con = conFactory.createConnection();

        con.start();
        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        WEB_NEW_ORDER = session.createQueue("WEB_NEW_ORDER");
        orderProducer = session.createProducer((Destination) WEB_NEW_ORDER);

    }

    public void sendOrder(String order) throws JMSException {
        // TODO
        ObjectMessage msg = session.createObjectMessage();
        msg.setObject(order);
        orderProducer.send(msg);
    }

    public static void main(String[] args) throws JMSException {
        System.out.println("Running WebOrderSystem ...");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the client order with following Syntax: <First Name, Last Name, Number of " +
                "ordered surfboards, Number of ordered diving suits, Customer-ID>");
        Scanner sc = new Scanner(System.in);
        String input;

        // JmsBrokerClient client = new JmsBrokerClient(clientName);
        WebOrderSystem sysOrder = new WebOrderSystem();

        boolean running = true;
        while (running) {

            while (!sc.hasNextLine()) {
            }
            input = sc.nextLine();
            // do sth with input
            sysOrder.sendOrder(input);

            System.out.println("Enter next Order:");
        }

    }
}
