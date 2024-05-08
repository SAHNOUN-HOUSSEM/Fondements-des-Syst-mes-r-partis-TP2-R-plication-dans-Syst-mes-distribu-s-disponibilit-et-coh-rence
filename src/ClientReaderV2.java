import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientReaderV2 {
    private final static String EXCHANGE_NAME_READER_TO_REPLICA = "reader_to_replica_exchange";
    private final static String EXCHANGE_NAME_REPLICA_TO_READER = "replica_to_reader_exchange";


    public static void main(String[] argv) throws Exception {
        Scanner scanner = new Scanner(System.in);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.exchangeDeclare(EXCHANGE_NAME_READER_TO_REPLICA, "fanout");


            channel.exchangeDeclare(EXCHANGE_NAME_REPLICA_TO_READER, "fanout");

            String queueName = channel.queueDeclare().getQueue();
            readLast(channel, queueName);


            String message = "";
            System.out.print("To exit, type 'exit'\n");
            System.out.print("Pour envoyer une requete, tapez 'Read Last'\n");

            while (true) {
                System.out.print("Enter un choix: \n");
                message = scanner.nextLine();

                if (message.equals("exit")) {
                    break;
                }

                if (message.equals("Read Last")) {
                    channel.basicPublish(EXCHANGE_NAME_READER_TO_REPLICA, "", null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println(" [x] Sent Read Last request");

                    System.out.println(" [*] Waiting for message.");
                }
            }
        }
    }
    
    public static void readLast(Channel channel, String queueName) throws Exception{
        channel.queueBind(queueName, EXCHANGE_NAME_REPLICA_TO_READER, "");
        channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "' from Replica");
        }, consumerTag -> {});
    }

}
