import socket
import threading

# Dirección IP y puerto del servidor
SERVER_IP = '127.0.0.1'
SERVER_PORT = 5000

# Creamos un socket TCP/IP
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((SERVER_IP, SERVER_PORT))

# Pedimos al cliente que ingrese su nombre
nombre = input("Ingrese su nombre: ")
client_socket.send(nombre.encode())


def receive_messages():
    while True:
        # Recibimos el mensaje del servidor
        mensaje = client_socket.recv(1024).decode()
        print(mensaje)


# Creamos un hilo para recibir mensajes del servidor
receive_thread = threading.Thread(target=receive_messages)
receive_thread.start()

while True:
    # El cliente escribe y envía un mensaje al servidor
    mensaje = input()
    client_socket.send(mensaje.encode())
    if mensaje == 'exit':
        break

# Cerramos la conexión con el servidor
client_socket.close()