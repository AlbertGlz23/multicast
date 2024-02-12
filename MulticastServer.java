import java.net.*;
import java.io.*;


public class MulticastServer {

    public MulticastServer() {
    }
    
     public static void main(String args[]) {
    
    try { 
	   // Direcci�n IP de multicast
	   String grp="224.0.0.1";
	   
      InetAddress grupo = InetAddress.getByName(grp);
      MulticastSocket socket = new MulticastSocket(6789);

      // Se une al grupo
      socket.joinGroup(grupo);


  	  String msg = "";
		System.out.println("Escribir los Mensajes Para El Grupo:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true){
 			msg = br.readLine();
 			DatagramPacket data = new DatagramPacket(msg.getBytes(), 0, msg.length(), grupo, 6789);
 			socket.send(data);
 			if (msg.equals("FIN") || msg.equals("fin") ) break;
			}

      // Si se escribr  "FIN" termina
      socket.leaveGroup(grupo);
    } 
    	catch (SocketException e) {
      		System.out.println("Socket:" + e.getMessage());
    	} 
    	catch (IOException e) {
      	System.out.println("IO:" + e.getMessage());
    	}
  }
    
    
}

