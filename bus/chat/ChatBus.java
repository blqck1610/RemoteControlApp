package bus.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatBus {
    private Socket socket;

    public ChatBus(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(Message objMessage) throws IOException {
        ObjectOutputStream dos = new ObjectOutputStream(this.socket.getOutputStream());
        dos.writeObject(objMessage);
    }

    public Message recvMessage() throws IOException, ClassNotFoundException {
        Message objMessage = null;
        ObjectInputStream dis = new ObjectInputStream(this.socket.getInputStream());
        objMessage = (Message) dis.readObject();
        return objMessage;
    }

    public Socket getSocket() {
        return this.socket;
    }
}

