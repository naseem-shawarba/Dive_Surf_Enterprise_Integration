import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.Message;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import javax.jms.Connection;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ResultSystem {

    public static class calculateValidationResultAggregation implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }

            boolean validOldExchange = Boolean
                    .parseBoolean((String) oldExchange.getIn().getHeader("Valid", String.class));
            boolean validNewExchange = Boolean
                    .parseBoolean((String) newExchange.getIn().getHeader("Valid", String.class));
            boolean valid = validNewExchange && validOldExchange;

            oldExchange.getIn().setHeader("Valid", String.valueOf(valid));
            oldExchange.getIn().setHeader("validationResult", oldExchange.getIn().getHeader("validationResult") + " & "
                    + newExchange.getIn().getHeader("validationResult"));

            return oldExchange;
        }
    }

    private static Processor transalteCCMSG = new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {
            String[] parts = exchange.getIn().getBody(String.class).split(", ");
            // here is the pattern
            // <Customer-ID, Full Name, Number of ordered surfboards, Number of ordered
            // diving suits>
            String customerID = parts[0];
            String firstName = parts[1].split(" ")[0];
            String lastName = parts[1].split(" ")[1];
            String numberOfDivingSuits = parts[3];
            String numberOfSurfboards = parts[2];
            String overallItems = String
                    .valueOf(Integer.parseInt(numberOfDivingSuits) + Integer.parseInt(numberOfSurfboards));
            String orderID = String.valueOf(IDGenerator.getInstance().generateNewID());
            String valid = "";
            String validationResult = "";

            exchange.getIn().setHeader("CustomerID", customerID);
            exchange.getIn().setHeader("FirstName", firstName);
            exchange.getIn().setHeader("LastName", lastName);
            exchange.getIn().setHeader("OverallItems", overallItems);
            exchange.getIn().setHeader("NumberOfDivingSuits", numberOfDivingSuits);
            exchange.getIn().setHeader("NumberOfSurfboards", numberOfSurfboards);
            exchange.getIn().setHeader("OrderID", orderID);
            exchange.getIn().setHeader("Valid", valid);
            exchange.getIn().setHeader("validationResult", validationResult);

        }
    };

    private static Processor transalteWebMSG = new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {
            String[] parts = exchange.getIn().getBody(String.class).split(", ");
            // here is the pattern
            // <First Name, Last Name, Number of ordered surfboards, Number of ordered
            // diving suits, Customer-ID>

            String customerID = parts[4];
            String firstName = parts[0];
            String lastName = parts[1];
            String numberOfDivingSuits = parts[3];
            String numberOfSurfboards = parts[2];
            String overallItems = String
                    .valueOf(Integer.parseInt(numberOfDivingSuits) + Integer.parseInt(numberOfSurfboards));
            String orderID = String.valueOf(IDGenerator.getInstance().generateNewID());
            String valid = "";
            String validationResult = "";

            exchange.getIn().setHeader("CustomerID", customerID);
            exchange.getIn().setHeader("FirstName", firstName);
            exchange.getIn().setHeader("LastName", lastName);
            exchange.getIn().setHeader("OverallItems", overallItems);
            exchange.getIn().setHeader("NumberOfDivingSuits", numberOfDivingSuits);
            exchange.getIn().setHeader("NumberOfSurfboards", numberOfSurfboards);
            exchange.getIn().setHeader("OrderID", orderID);
            exchange.getIn().setHeader("Valid", valid);
            exchange.getIn().setHeader("validationResult", validationResult);

        }
    };
    // well translate the headers to user friendly output
    private static Processor result = new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {

            // print all required properties

            exchange.getIn().setBody(exchange.getIn().getHeaders().toString());
            String msgINFO = "CustomerID=" + exchange.getIn().getHeader("CustomerID") + ", "
                    + "FirstName = " + exchange.getIn().getHeader("FirstName") + ", "
                    + "LastName = " + exchange.getIn().getHeader("LastName") + ", "
                    + "OverallItems = " + exchange.getIn().getHeader("OverallItems") + ", "
                    + "NumberOfDivingSuits = " + exchange.getIn().getHeader("NumberOfDivingSuits") + ", "
                    + "NumberOfSurfboards = " + exchange.getIn().getHeader("NumberOfSurfboards") + ", "
                    + "OrderID = " + exchange.getIn().getHeader("OrderID") + ", "
                    + "Valid = " + exchange.getIn().getHeader("Valid") + ", "
                    + "validationResult = " + exchange.getIn().getHeader("validationResult");
            exchange.getIn().setBody(msgINFO);

        }
    };

    // printDebug is used to chech the message while aggregating or splitting
    // messages
    private static Processor printDebug = new Processor() {
        @Override
        public void process(Exchange exchange) throws Exception {

            // print all required properties

            System.out.println("CustomerID " + exchange.getIn().getHeader("CustomerID"));
            System.out.println("FirstName " + exchange.getIn().getHeader("FirstName"));
            System.out.println("LastName " + exchange.getIn().getHeader("LastName"));
            System.out.println("OverallItems " + exchange.getIn().getHeader("OverallItems"));
            System.out.println("NumberOfDivingSuits " + exchange.getIn().getHeader("NumberOfDivingSuits"));
            System.out.println("NumberOfSurfboards " + exchange.getIn().getHeader("NumberOfSurfboards"));
            System.out.println("OrderID " + exchange.getIn().getHeader("OrderID"));
            System.out.println("Valid " + exchange.getIn().getHeader("Valid"));
            System.out.println("validationResult " + exchange.getIn().getHeader("validationResult"));

        }
    };

    public static int randomInt(int upperbound) {
        Random rand = new Random(); // instance of random class
        int int_random = rand.nextInt(upperbound);
        return int_random;
    }

    public static void main(String[] args) throws Exception {

        ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory();
        conFactory.setTrustAllPackages(true);
        Connection con = conFactory.createConnection();

        final Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue WEB_NEW_ORDER = session.createQueue("WEB_NEW_ORDER");
        Queue CC_NEW_ORDER = session.createQueue("CC_NEW_ORDER");
        Queue NEW_ORDER = session.createQueue("NEW_ORDER");
        Queue CHECKED_ORDER = session.createQueue("CHECKED_ORDER");
        Queue BILLING_CHECK = session.createQueue("BILLING_CHECK");
        Queue INVENTORY_CHECK = session.createQueue("INVENTORY_CHECK");
        Queue inventory = session.createQueue("INVENTORY_COMMIT");// update the varialbles
        Queue SYSTEM_RESULT = session.createQueue("SYSTEM_RESULT");

        DefaultCamelContext contxt = new DefaultCamelContext();
        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
        activeMQComponent.setTrustAllPackages(true);
        contxt.addComponent("activemq", activeMQComponent);

        contxt.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // define all rooting logic

                // this is the Channel-Adapter
                from("file:Orders?noop=true")
                        .to("activemq:queue:CC_NEW_ORDER");

                from("activemq:queue:CC_NEW_ORDER")
                        .split(body().tokenize("\n"))
                        .process(transalteCCMSG)
                        .to("activemq:queue:NEW_ORDER");

                from("activemq:queue:WEB_NEW_ORDER").process(transalteWebMSG)
                        .to("activemq:queue:NEW_ORDER");

                from("activemq:queue:NEW_ORDER")
                        .multicast()
                        .to("activemq:queue:BILLING_CHECK", "activemq:queue:INVENTORY_CHECK");

                from("activemq:queue:CHECKED_ORDER")
                        .aggregate(header("OrderID"), new calculateValidationResultAggregation())
                        .completionSize(2)
                        // .process(printDebug) //for debugging
                        .choice()
                        .when(header("Valid").endsWith("true"))
                        .to("activemq:queue:INVENTORY_COMMIT") // message valid but variable in Inventory still not
                                                               // updated
                        .otherwise()
                        .to("activemq:queue:SYSTEM_RESULT"); // message is invalid

                from("activemq:queue:SYSTEM_RESULT")

                        // .process(printDebug) //for debugging
                        .process(result)
                        .choice()
                        .when(header("Valid").endsWith("true")) // message is valid and Inverntory is updated
                        .to("stream:out") // // message is valid and Inverntory is updated
                        .otherwise()
                        .to("stream:err"); // invalid orders

            }

        });

        contxt.start();
        con.start();
        System.out.println("Running ResultSystem ...");
        System.in.read();
        contxt.stop();
        con.close();
    }
}
