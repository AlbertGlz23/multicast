import java.net.*;
import java.io.*;

public class MulticastCliente {

    public MulticastCliente() {
    }
    
    public static void main(String args[]) throws Exception {
    	
	String grp="224.0.0.1";
	
	MulticastSocket cliente =new MulticastSocket(6789);
	InetAddress group =InetAddress.getByName(grp);
	//getByName- returns IP address of given host
 	
	//Se une al grupo
	cliente.joinGroup(group);

 	/* Recibe Datos y los Ecribe en Pantalla hasta que Recibe "FIN" */
 	System.out.println("Esperando Nuevas Noticias\n");
 	while(true) {
 		byte buf[] = new byte[1024];
 		DatagramPacket data =new DatagramPacket(buf, buf.length);
 		cliente.receive(data);
 		String msg =new String(data.getData()).trim();
 		if (msg.equals("FIN") || msg.equals("fin") ) break;
 		System.out.println(msg);
 	}
 	System.out.println("El Servidor Decidio Finalizar, Hasta Luego\n");
 	cliente.close();
 }
    
    
    
}

