package com.example.frontend.network;
import java.io.*;
import java.net.Socket;

public class ServerClient {

    private static ServerClient instance;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private ServerClient() {}

    public void connect() throws Exception {

        String host = FrontendConfig.get("server.host");
        int port = FrontendConfig.getInt("server.port");

        socket = new Socket(host, port);

        out = new PrintWriter(socket.getOutputStream(), true);

        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        System.out.println(in.readLine());
    }

    public String sendRequest(String json) throws Exception {
        out.println(json);
        return in.readLine();
    }

    public static synchronized ServerClient getInstance() {
        if (instance == null) {
            instance = new ServerClient();
        }
        return instance;
    }
}