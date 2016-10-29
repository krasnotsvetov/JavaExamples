package ru.ifmo.ctddev.krasnotsvetov.udpapp;

/**
 * Created by Krasnotsvetov on 09.05.2016.
 */
public class Main {
    public static void main(String[] args) {

        HelloUDPServer server = new HelloUDPServer();
        server.start(7777, 10);
        HelloUDPClient client = new HelloUDPClient();
        client.start("127.0.0.1", 7777, "test_0", 100, 10);

        while (true) {

        }
    }

}
