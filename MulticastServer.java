import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServer {
    public static final int PORT = 6789;

    public MulticastServer(String name) {
        System.out.println("Bienvenido " + name);
        System.out.println("Escribe 'salir' para terminar la conversación");

        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName("224.0.0.1");
            socket.joinGroup(group);

            new Thread(() -> recibirMensajes(socket)).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            boolean exit = false;
            String message = "";
            while (!exit) {
                String input = br.readLine();
                if (input.equals("exit")) {
                    message = "El usuario: (" + name + ") ha salido de la conversación ";
                    exit = true;
                } else {
                    message = name + ": " + input;
                }
                DatagramPacket data = new DatagramPacket(message.getBytes(), 0, message.length(), group, PORT);
                socket.send(data);
            }
            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void recibirMensajes(MulticastSocket socket) {
        try {
            while (true) {
                byte[] buffer = new byte[255];
                DatagramPacket data = new DatagramPacket(buffer, buffer.length);
                socket.receive(data);
                System.out.println(new String(data.getData(), 0, data.getLength()));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}