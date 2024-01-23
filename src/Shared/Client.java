package Shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    private OutputStream out = null;
    private BufferedReader in = null;
    private ReadCallback readCallback;

    public Client(int port, ReadCallback readCallback) {
        try {
            Socket clientSocket = new Socket("localhost", port);

            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            new Thread(new ClientThread(readCallback)).start();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String msg) {
        try {
            out.write(msg.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class ClientThread extends Thread {
        private ReadCallback readCallback = null;

        public ClientThread(ReadCallback callback) {
            this.readCallback = callback;
        }

        @Override
        public void run() {

        }
    }
}
