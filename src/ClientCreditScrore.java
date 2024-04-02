import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClientCreditScrore {

    private static ClientCreditScrore instance = null;

    // all potential replies are stored here . to get the value of the key use
    // hmap.get(replyNumber)
    Map<String, Integer> clientsCredit = Collections.synchronizedMap(new HashMap<String, Integer>());

    ClientCreditScrore() {
        // hard coding random clients with a Creditscore
        // to read more about Creditscore follow the link
        // https://www.experian.com/blogs/ask-experian/credit-education/score-basics/what-is-a-good-credit-score/
        clientsCredit.put("Naseem Shawarba", 850);
        clientsCredit.put("Bad Boy", 350);
        clientsCredit.put("Franky HD", 500);
        clientsCredit.put("Ice Burg", 740);
        clientsCredit.put("Nian Smith", 670);
        clientsCredit.put("Atom Smith", 700);
        clientsCredit.put("Nils Arm", 650);
        clientsCredit.put("Si Hu", 305);
        clientsCredit.put("Na Nie", 620);
        clientsCredit.put("Na Siem", 840);
        clientsCredit.put("Ava Mi", 788);
        clientsCredit.put("Alexander Burg", 576);
        clientsCredit.put("Arnold Green", 621);
        clientsCredit.put("Ni Yo", 645);
        clientsCredit.put("Addam Abraham", 526);

    }

    public static synchronized ClientCreditScrore getInstance() {
        if (instance == null) {
            instance = new ClientCreditScrore();
        }
        return instance;
    }

    public Map<String, Integer> getClientsCredit() {
        return clientsCredit;
    }

    public void setClientsCredit(HashMap<String, Integer> clientsCredit) {
        this.clientsCredit = clientsCredit;
    }

    public synchronized void addClientCredit(String clientName, Integer clientScore) {
        clientsCredit.put(clientName, clientScore);
    }
}
