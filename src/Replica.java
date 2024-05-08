import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;


public class Replica {
    private final static String EXCHANGE_NAME_WRITE = "write_exchange";
    private final static String EXCHANGE_NAME_READER_TO_REPLICA = "reader_to_replica_exchange";
    private final static String EXCHANGE_NAME_REPLICA_TO_READER = "replica_to_reader_exchange";


    public static void main(String[] argv) throws Exception {

        String replicaNumber = argv[0];
        String fileName = "replica" + replicaNumber + ".txt";

        System.out.println("Replica " + replicaNumber + " is running");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.exchangeDeclare(EXCHANGE_NAME_WRITE, "fanout");
            channel.exchangeDeclare(EXCHANGE_NAME_READER_TO_REPLICA, "fanout");
            channel.exchangeDeclare(EXCHANGE_NAME_REPLICA_TO_READER, "fanout");

            String queueNameWrite = channel.queueDeclare().getQueue();
            String queueNameRead = channel.queueDeclare().getQueue();

            channel.queueBind(queueNameWrite, EXCHANGE_NAME_WRITE, "");
            channel.queueBind(queueNameRead, EXCHANGE_NAME_READER_TO_REPLICA, "");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            while (true) {
                channel.basicConsume(queueNameWrite, true, deliverCallbackWrite(fileName), consumerTag -> {
                });
                channel.basicConsume(queueNameRead, true, deliverCallbackRead(fileName, channel), consumerTag -> {
                });
            }
        }
    }


    private static DeliverCallback deliverCallbackWrite(String fileName) {
        FileWriter fileWriter = new FileWriter();

        return (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "' from Write Client");
            try {
                fileWriter.writeToFile(message, fileName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static DeliverCallback deliverCallbackRead(
            String fileName,
            Channel channel
    ) {
        FileReader FileReader = new FileReader();

        return (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "' from Reader Client");

            String lastMessage = "";
            try {
                lastMessage = FileReader.readLastLine(fileName);
                System.out.println("lastMessage = " + lastMessage);
                channel.basicPublish(
                        EXCHANGE_NAME_REPLICA_TO_READER,
                        "",
                        null,
                        lastMessage.getBytes(StandardCharsets.UTF_8)
                );
                System.out.println(" [x] Sent '" + lastMessage + "' to Reader Client");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}
