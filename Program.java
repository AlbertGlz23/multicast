import java.io.BufferedReader;
import java.io.InputStreamReader;

class Program {
    public static void main(String[] args) {
        //Intenta unirse al grupo multicast, y si no, crea uno de fondo
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Ingresa tu nombre de usuario...");
            System.out.print("Nombre: ");
            String nombre = reader.readLine();
            MulticastServer server = new MulticastServer(nombre);
        } catch (Exception e) {
            System.out.println("No se pudo unir al grupo multicast");
        }
    }

}
