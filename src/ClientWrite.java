import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class ClientWrite {
    private final static String EXCHANGE_NAME = "write_exchange";

    public static void main(String[] argv) throws Exception {
        Scanner scanner = new Scanner(System.in);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()
        ) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = "";
            System.out.print("To exit, type 'exit'\n");

            while (true) {
                System.out.print("Enter a message: ");
                message = scanner.nextLine();
                if (message.equals("exit")) {
                    break;
                }
                channel.basicPublish(EXCHANGE_NAME , "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }
           }
    }
}
