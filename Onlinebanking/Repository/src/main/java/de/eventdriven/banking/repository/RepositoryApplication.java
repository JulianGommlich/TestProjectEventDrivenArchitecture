package de.eventdriven.banking.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import de.eventdriven.banking.utils.Account;
import de.eventdriven.banking.utils.EventRequest;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
public class RepositoryApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }

    @Service
    static class RepositoryManager {

        private final StreamBridge streamBridge;

        @Autowired
        public RepositoryManager(StreamBridge streamBridge) {
            this.streamBridge = streamBridge;
        }

        @Bean
        public Consumer<Account> repositoryIn() {
            return account -> {
                LOGGER.info("account received");
                //saveNewAccount(account);
                streamBridge.send("repositoryOut-in-0", account);
            };
        }

        @Bean
        public Consumer<EventRequest> repositoryInDeposit() {
            return eventRequest -> {
                LOGGER.info("event recieved");
                MongoCursor<Document> cursor = findAccount(eventRequest.getAccountNumber());
                if (cursor.hasNext()) {
                    Document document = cursor.next();
                    Account account = new Account(
                            document.get("accountNumber").toString(),
                            document.get("pinCode").toString(),
                            document.get("accountant").toString(),
                            Double.parseDouble(document.get("amount").toString()));
                    depositMoney(account, eventRequest.getAmount());
                    streamBridge.send("repositoryOutDeposit-in-0", "Money deposited");
                }
                else {
                    streamBridge.send("repositoryOutDeposit-in-0", "Update not possible");
                }
            };
        }

        private void saveNewAccount(Account account) {
            MongoClient client = new MongoClient(
                    new MongoClientURI("mongodb+srv://admin:test1234@cluster0.2fvse.mongodb.net/myFirstDatabase?retryWrites=true&w=majority"));
            MongoCollection<Document> accounts = client.getDatabase("OnlineBankingDatabase").getCollection("Accounts");
            accounts.insertOne(new Document("accountNumber", account.getAccountNumber()).append("pinCode", account.getPinCode()).append("accountant", account.getAccountant()));
        }

        private MongoCursor<Document> findAccount(String accountNumber) {
            MongoClient client = new MongoClient(
                    new MongoClientURI("mongodb+srv://admin:test1234@cluster0.2fvse.mongodb.net/myFirstDatabase?retryWrites=true&w=majority"));
            MongoCollection<Document> accounts = client.getDatabase("OnlineBankingDatabase").getCollection("Accounts");
            FindIterable<Document> findIterable = accounts.find(new BasicDBObject("accountNumber", accountNumber));
            return findIterable.cursor();
        }

        private void depositMoney(Account account, Double depositAmount) {
            MongoClient client = new MongoClient(
                    new MongoClientURI("mongodb+srv://admin:test1234@cluster0.2fvse.mongodb.net/myFirstDatabase?retryWrites=true&w=majority"));
            MongoCollection<Document> accounts = client.getDatabase("OnlineBankingDatabase").getCollection("Accounts");
            //accounts.updateOne(eq("accountNumber", account.getAccountNumber()))
        }

    }
}
