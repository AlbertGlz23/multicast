import java.io.BufferedReader;
import java.io.InputStreamReader;

class Program {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--server")) {
            startServer();
        } else {
            startClient();
        }
    }

    public static void startClient() {
        //Intenta unirse al grupo multicast, y si no, crea uno de fondo
        MulticastServer server;
        String nombre;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Ingresa tu nombre de usuario...");
            System.out.print("Nombre: ");
            nombre = reader.readLine();
            server = new MulticastServer(nombre, false);
        } catch (Exception e) {
            System.out.println("No se pudo unir al grupo multicast");
            return;
        }
        //Envía mensajes al grupo multicast
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String selectedNumber = reader.readLine();
                switch (selectedNumber) {
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                        String message = nombre + ", ha seleccionado la opción:" + selectedNumber;
                        server.sendMessage(message);
                        break;
                    case "empezar":
                        server.sendMessage(nombre + " ha iniciado el juego");
                        break;
                    case "salir":
                        server.leaveGroup();
                        return;
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void startServer() {
        MulticastServer server;
        String nombre = "Croupier";
        System.out.println("Server started");
        try{
            new MulticastServer(nombre, true);
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }



    }
}