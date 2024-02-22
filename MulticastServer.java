import org.json.JSONArray;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class MulticastServer {
    public static final int PORT = 6789;
    private final MulticastSocket socket;
    private final InetAddress group;
    private final String name;
    private final boolean isServer;

    private final HashMap<String, Integer> userPoints;
    private final HashMap<String, Boolean> gotCorrectAnswer;
    private JSONObject json;

    public MulticastServer(String name, boolean isServer) throws Exception {
        this.socket = new MulticastSocket(PORT);
        this.group = InetAddress.getByName("224.0.0.1");
        this.isServer = isServer;
        this.userPoints = new HashMap<>();
        this.gotCorrectAnswer = new HashMap<>();
        String randomname = "Anónimo #" + (int) (Math.random() * 10000000);
        this.name = name.isEmpty() || name.isBlank() ? randomname : name;
        joinGroup();
    }

    private void joinGroup() throws Exception {
        socket.joinGroup(group);
        new Thread(this::receiveMessage).start();
        announceJoinLeaveMessage(false);
        if (isServer) {
            new Thread(this::startGameService).start();
        }
    }

    private void startGameService() {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get("preguntas.json")));
            json = new JSONObject(contenido);
            JSONArray preguntas = json.getJSONArray("preguntas");
            for (int i = 0; i < preguntas.length(); i++) {
                JSONObject pregunta = preguntas.getJSONObject(i);
                String enunciado = pregunta.getString("pregunta");
                JSONArray opciones = pregunta.getJSONArray("opciones");
                StringBuilder opcionesString = new StringBuilder();
                for (int j = 0; j < opciones.length(); j++) {
                    opcionesString.append("\n").append((j + 1)).append(". ").append(opciones.getString(j));
                }
                String mensaje = enunciado + "...\n" + opcionesString;
                sendMessage(mensaje);
                Thread.sleep(10000);

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void announceJoinLeaveMessage(boolean isExit) throws Exception {
        String message = name + (isExit ? " ha salido del chat" : " ha entrado al chat");
        DatagramPacket data = new DatagramPacket(message.getBytes(), 0, message.length(), group, PORT);
        socket.send(data);
    }

    public void sendMessage(String message) throws Exception {
        DatagramPacket data = new DatagramPacket(message.getBytes(), 0, message.length() + 1, group, PORT);
        socket.send(data);
    }

    private void receiveMessage() {
        try {
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket data = new DatagramPacket(buffer, buffer.length);
                socket.receive(data);
                String receivedData = new String(data.getData(), data.getOffset(), data.getLength());
                System.out.println(receivedData);
                if (receivedData.equals("killserver") && isServer) {
                    leaveGroup();
                    return;
                }
                if (receivedData.contains("ha iniciado el juego")) {
                    userPoints.clear();
                    gotCorrectAnswer.clear();
                    userPoints.put(name, 0);
                    gotCorrectAnswer.put(name, false);
                }
                if (receivedData.contains("ha seleccionado la opción")) {
                    String username = receivedData.split(",")[0];
                    String option = receivedData.split(",")[1].split(":")[1].trim();
                    if (getCurrentCorrectAnswer().equals(option)) {
                        gotCorrectAnswer.put(username, true);
                    } else {
                        gotCorrectAnswer.put(username, false);
                    }

                    if (option.equals("4")) {
                        addPointToUserIfItGotCorrectAnswer(username);
                        printPoints();
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String getCurrentCorrectAnswer() {
        return "4";
    }

    private void addPointToUserIfItGotCorrectAnswer(String username) {
        if (gotCorrectAnswer.get(username)) {
            userPoints.put(username, userPoints.get(username) + 1);
        }
    }

    private void printPoints() {
        System.out.println("Puntajes:");
        userPoints.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    public void leaveGroup() throws Exception {
        announceJoinLeaveMessage(true);
        socket.leaveGroup(group);
        socket.close();
    }
}