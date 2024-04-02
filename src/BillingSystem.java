import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class BillingSystem {
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
            ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory();
            conFactory.setTrustAllPackages(true);
            Connection con = conFactory.createConnection();

            final Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue BILLING_CHECK = session.createQueue("BILLING_CHECK");
            Queue CHECKED_ORDER = session.createQueue("CHECKED_ORDER");
            MessageConsumer consumer = session.createConsumer(BILLING_CHECK);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        ObjectMessage modMessage = session.createObjectMessage("");
                        setMessageProperties(modMessage, getMessageProperties(message));
                        String clientFullName = message.getStringProperty("FirstName") + " "
                                + message.getStringProperty("LastName");
                        if (ClientCreditScrore.getInstance().getClientsCredit().containsKey(clientFullName)) {
                            if (ClientCreditScrore.getInstance().getClientsCredit().get(clientFullName) < 669) {
                                modMessage.setStringProperty("Valid", String.valueOf(false));
                                modMessage.setStringProperty("validationResult", "Client has low Credit Score");

                            } else {
                                modMessage.setStringProperty("Valid", String.valueOf(true));
                                modMessage.setStringProperty("validationResult", "Client has good Credit Score");
                            }

                        } else {
                            modMessage.setStringProperty("Valid", String.valueOf(true));
                            modMessage.setStringProperty("validationResult",
                                    "Client doesnt have CreditScore registered");
                        }
                        MessageProducer producer = session.createProducer(CHECKED_ORDER);
                        producer.send(modMessage);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("Running BillingSystem ...");

            con.start();
            System.in.read();
            con.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
