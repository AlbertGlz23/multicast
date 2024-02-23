import socket
import threading

# Dirección IP y puerto del servidor
SERVER_IP = '127.0.0.1'
SERVER_PORT = 5000

# Creamos un socket TCP/IP
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((SERVER_IP, SERVER_PORT))
server_socket.listen(5)

# Lista de clientes conectados y sus respectivos nombres
clientes = []
nombres_clientes = {}


def broadcast(mensaje, origen=None):
    # Envía un mensaje a todos los clientes excepto al cliente de origen (si se proporciona)
    for client_socket in clientes:
        if client_socket != origen:
            try:
                client_socket.send(mensaje.encode())
            except socket.error:
                # Si ocurre un error al enviar el mensaje, el cliente se desconectó
                handle_disconnection(client_socket)


def handle_client(client_socket, client_address):
    # Recibimos el nombre del cliente
    nombre = client_socket.recv(1024).decode()
    nombres_clientes[client_socket] = nombre
    print(f"Nuevo cliente conectado: {nombre} - Dirección: {client_address}")

    # Enviamos un mensaje de bienvenida al cliente recién conectado
    mensaje_bienvenida = f"Bienvenido, {nombre}!"
    client_socket.send(mensaje_bienvenida.encode())

    # Notificamos a todos los clientes sobre el nuevo cliente conectado
    mensaje_conexion = f"{nombre} se ha conectado."
    broadcast(mensaje_conexion)

    while True:
        try:
            # Recibimos el mensaje del cliente
            mensaje = client_socket.recv(1024).decode()
            if mensaje == 'exit':
                # Si el mensaje es 'exit', se cierra la conexión y se notifica
                client_socket.close()
                mensaje_salida = f"{nombre} ha salido."
                broadcast(mensaje_salida)
                print(f"Cliente desconectado: {nombre} - Dirección: {client_address}")
                break
            # Enviamos el mensaje a todos los clientes conectados
            broadcast(f"{nombre}: {mensaje}", client_socket)
        except socket.error:
            break
        try:
            # Recibimos el mensaje del cliente
            mensaje = client_socket.recv(1024).decode()
            if mensaje == '':
                client_socket.close()
                break
            # Enviamos el mensaje a todos los clientes conectados
            broadcast(f"{nombre}: {mensaje}", client_socket)
            print(f"Cliente desconectado: {nombre} - Dirección: {client_address}")
        except socket.error:
            # Si ocurre un error al recibir el mensaje, el cliente se desconectó
            break

    # Eliminamos al cliente de las listas y cerramos la conexión
    del nombres_clientes[client_socket]
    clientes.remove(client_socket)
    client_socket.close()

    # Notificamos a todos los clientes sobre la desconexión
    mensaje_desconexion = f"{nombre} se ha desconectado."
    broadcast(mensaje_desconexion)


def handle_disconnection(client_socket):
    # Maneja la desconexión inesperada de un cliente
    nombre = nombres_clientes[client_socket]
    del nombres_clientes[client_socket]
    clientes.remove(client_socket)
    client_socket.close()

    # Notificamos a todos los clientes sobre la desconexión
    mensaje_desconexion = f"{nombre} se ha desconectado inesperadamente."
    broadcast(mensaje_desconexion)


def accept_clients():
    while True:
        # Aceptamos la conexión entrante del cliente
        try:
            client_socket, client_address = server_socket.accept()
            # Agregamos el cliente a la lista
            clientes.append(client_socket)
            # Creamos un hilo para manejar al cliente
            client_thread = threading.Thread(target=handle_client, args=(client_socket, client_address))
            client_thread.start()
        except socket.error:
            # Si ocurre un error al aceptar la conexión, continuamos esperando nuevas conexiones
            continue


# Iniciamos el servidor
print("Servidor iniciado. Esperando conexiones...")

# Creamos un hilo para aceptar clientes
accept_thread = threading.Thread(target=accept_clients)
accept_thread.start()