import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;



public class Replica {
    private final static String EXCHANGE_NAME_WRITE = "write_exchange";
    private final static String EXCHANGE_NAME_READ = "read_exchange";
    private final static String EXCHANGE_NAME_REPLICA_TO_READER = "replica_to_reader_exchange";


    public static void main(String[] argv) throws Exception {
        FileWriter fileWriter = new FileWriter();


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
            channel.exchangeDeclare(EXCHANGE_NAME_READ, "fanout");

            String queueNameWrite = channel.queueDeclare().getQueue();
            String queueNameRead = channel.queueDeclare().getQueue();

            channel.queueBind(queueNameWrite, EXCHANGE_NAME_WRITE, "");
            channel.queueBind(queueNameRead, EXCHANGE_NAME_READ, "");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            while (true) {

                DeliverCallback deliverCallbackWrite = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println(" [x] Received '" + message + "' from Write Client");
                    try {
                        fileWriter.writeToFile(message, fileName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
                channel.basicConsume(queueNameWrite, true, deliverCallbackWrite, consumerTag -> {
                });

                DeliverCallback deliverCallbackRead = (consumerTag, delivery) -> {
                    System.out.println(" [x] Read request from Read Client ");
                    String message = new String(delivery.getBody(), "UTF-8");
                    if (message.equals("Read Last")) {
                        String lastMessage = fileWriter.readLastLine(fileName);
                        System.out.println(lastMessage);
                        try {
                            sendMessageToReader(lastMessage, channel);
                            System.out.println(" [x] Sent '" + message + "' to Reader Client");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                channel.basicConsume(queueNameRead, true, deliverCallbackRead, consumerTag -> {
                });
            }
        }
    }

    private static void sendMessageToReader(String message, Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE_NAME_REPLICA_TO_READER, "fanout");
        channel.basicPublish(EXCHANGE_NAME_REPLICA_TO_READER , "", null, message.getBytes("UTF-8"));
    }
}
