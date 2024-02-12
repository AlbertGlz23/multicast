import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class EnterKeyListener implements KeyListener {

    private MulticastSocket socket;
    private InetAddress group;

    public EnterKeyListener(MulticastSocket socket, InetAddress group) {
        this.socket = socket;
        this.group = group;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String message = br.readLine();
                DatagramPacket data = new DatagramPacket(message.getBytes(), 0, message.length(), group, MulticastServer.PORT);
                socket.send(data);
            } catch (Exception ex) {
                System.out.println("Error sending message: " + ex.getMessage());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}