import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.jms.Queue;
import java.io.IOException;
import java.util.*;

public class InventorySystem {
    ActiveMQConnectionFactory conFactory;
    Connection con;
    Session session;
    Queue INVENTORY_CHECK;
    Queue CHECKED_ORDER;
    Queue INVENTORY_COMMIT;
    Queue SYSTEM_RESULT;
    MessageConsumer consumer_INVENTORY_CHECK;
    MessageConsumer consumer_INVENTORY_COMMIT;
    Map<String, Integer> items = Collections.synchronizedMap(new HashMap<String, Integer>());// thread safe map

    public InventorySystem(int avaliableDivingSuits, int avaliableSurfboards) throws JMSException {
        items.put("avaliableDivingSuits", avaliableDivingSuits);
        items.put("avaliableSurfboards", avaliableSurfboards);
        setupConnection();
    }

    void setupConnection() throws JMSException {
        conFactory = new ActiveMQConnectionFactory();
        conFactory.setTrustAllPackages(true);
        con = conFactory.createConnection();

        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        INVENTORY_CHECK = session.createQueue("INVENTORY_CHECK");
        CHECKED_ORDER = session.createQueue("CHECKED_ORDER");
        INVENTORY_COMMIT = session.createQueue("INVENTORY_COMMIT");
        SYSTEM_RESULT = session.createQueue("SYSTEM_RESULT");
        consumer_INVENTORY_CHECK = session.createConsumer(INVENTORY_CHECK);
        consumer_INVENTORY_CHECK.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    ObjectMessage modMessage = session.createObjectMessage("");
                    setMessageProperties(modMessage, getMessageProperties(message));
                    int requestedDivingSuits = Integer.parseInt(message.getStringProperty("NumberOfDivingSuits"));
                    int requestedSurfboards = Integer.parseInt(message.getStringProperty("NumberOfSurfboards"));

                    if (requestedDivingSuits <= items.get("avaliableDivingSuits")
                            && requestedSurfboards <= items.get("avaliableSurfboards")) {
                        modMessage.setStringProperty("Valid", String.valueOf(true));
                        modMessage.setStringProperty("validationResult", "requested items are available");
                    } else {
                        modMessage.setStringProperty("Valid", String.valueOf(false));
                        modMessage.setStringProperty("validationResult", "requested items are unavailable");
                    }

                    MessageProducer producer_CHECKED_ORDER = session.createProducer(CHECKED_ORDER);
                    producer_CHECKED_ORDER.send(modMessage);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        consumer_INVENTORY_COMMIT = session.createConsumer(INVENTORY_COMMIT);
        consumer_INVENTORY_COMMIT.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    updateVariables(message);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        con.start();
    }

    public synchronized void updateVariables(Message message) throws JMSException {
        ObjectMessage modMessage = session.createObjectMessage("");
        setMessageProperties(modMessage, getMessageProperties(message));
        int requestedDivingSuits = Integer.parseInt(message.getStringProperty("NumberOfDivingSuits"));
        int requestedSurfboards = Integer.parseInt(message.getStringProperty("NumberOfSurfboards"));

        MessageProducer producer_SYSTEM_RESULT = session.createProducer(SYSTEM_RESULT);

        if (requestedDivingSuits <= items.get("avaliableDivingSuits")
                && requestedSurfboards <= items.get("avaliableSurfboards")) {
            items.put("avaliableDivingSuits", items.get("avaliableDivingSuits") - requestedDivingSuits);
            items.put("avaliableSurfboards", items.get("avaliableSurfboards") - requestedSurfboards);
            System.out.println("*******************************");
            System.out.println("At the moment avaliable DivingSuits: " + items.get("avaliableDivingSuits"));
            System.out.println("At the moment avaliable Surfboards: " + items.get("avaliableSurfboards"));
            System.out.println("*******************************");
            producer_SYSTEM_RESULT.send(modMessage);
        } else {
            modMessage.setStringProperty("Valid", String.valueOf(false));
            // client reached this listner only because it had a good Creditscore
            modMessage.setStringProperty("validationResult",
                    "Client passed BillingSystem_CHECK & requested items are unavailable");
            producer_SYSTEM_RESULT.send(modMessage);
        }

    }

    void quit() throws JMSException {
        con.close();
    }

    private static HashMap<String, Object> getMessageProperties(Message msg) throws JMSException {
        HashMap<String, Object> properties = new HashMap<String, Object>();
        Enumeration srcProperties = msg.getPropertyNames();
        while (srcProperties.hasMoreElements()) {
            String propertyName = (String) srcProperties.nextElement();
            properties.put(propertyName, msg.getObjectProperty(propertyName));
        }
        return properties;
    }

    private static void setMessageProperties(Message msg, HashMap<String, Object> properties) throws JMSException {
        if (properties == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            Object value = entry.getValue();
            msg.setObjectProperty(propertyName, value);
        }
    }

    public static void main(String[] args) {
        try {
            int avaliableDivingSuits;
            int avaliableSurfboards;

            Scanner sc = new Scanner(System.in);
            System.out.println("Running InventorySystem ...");
            System.out.println("Set avaliable DivingSuits : ");
            while (!sc.hasNextLine()) {
            }
            avaliableDivingSuits = Integer.parseInt(sc.nextLine());

            System.out.println("Set avaliable Surfboards : ");
            while (!sc.hasNextLine()) {
            }
            avaliableSurfboards = Integer.parseInt(sc.nextLine());

            InventorySystem invSYSTEM = new InventorySystem(avaliableDivingSuits, avaliableSurfboards);

            //////////////////////////////////////////////////////////////////////////////////////////

            System.in.read();
            invSYSTEM.quit();

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
