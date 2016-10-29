package ru.ifmo.ctddev.krasnotsvetov.udpapp;


import info.kgeorgiy.java.advanced.hello.HelloServer;
import info.kgeorgiy.java.advanced.hello.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Krasnotsvetov on 09.05.2016.
 */


/**
 * Server that receives request and response on them
 * Server can be created from the command line with two parameters:
 * port and number of treads
 * Server can be started with method {@code start}
 * To use method start user has to provide two integers, number of port and number of threads
 * To close the server there is a method {@code close}
 */
public class HelloUDPServer implements HelloServer {

    private int BUFFER_SIZE = 1024;
    private DatagramSocket reciever;
    private ExecutorService service;
    private boolean isStart = false;

    /**
     * Method to create server from command line.
     * First argument is port, second is number of threads.
     * @param args
     */
    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            int threads = Integer.parseInt(args[1]);
            HelloUDPServer h = new HelloUDPServer();
            h.start(port, threads);
        } catch (NumberFormatException e) {
            return;
        }
    }


    /**
     * Method to start server
     * @param port number of port to start server on
     * @param threads number of threads to process request on
     */
    @Override
    public void start(int port, int threads) {
        if (port < 0 || port > 65535) {
            return;
        }
        if (isStart) {
            System.err.println("server has started already. Stop server if you want to start again");
            return;
        }
        isStart = true;
        service = Executors.newFixedThreadPool(threads);
        try {
            reciever = new DatagramSocket(port);
            for (int i = 0; i < threads; i++) {
                service.submit(() -> {
                   try (DatagramSocket sendingSocket = new DatagramSocket()) {
                       DatagramPacket receivingPacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                       BUFFER_SIZE = sendingSocket.getReceiveBufferSize();
                       while (!Thread.interrupted() && !reciever.isClosed()) {
                           reciever.receive(receivingPacket);

                           byte[] temp = "Hello, ".getBytes(Util.CHARSET);
                           byte[] answerClient = receivingPacket.getData();
                           byte[] answerFinal = new byte[temp.length + answerClient.length];
                           String send = new String("Hello, ".getBytes(Util.CHARSET), 0, "Hello, ".getBytes(Util.CHARSET).length) + new String(receivingPacket.getData(), 0, receivingPacket.getLength(), Util.CHARSET
                           );
                           for (int t = 0; t < temp.length; t++) {
                               answerFinal[t] = temp[t];
                           }
                           for (int t = 0; t < answerClient.length; t++) {
                               answerFinal[temp.length + t] = answerClient[t];
                           }
                           //System.out.println(new String(answerFinal));

                           answerFinal = send.getBytes();
                           sendingSocket.send(new DatagramPacket(answerFinal, answerFinal.length, receivingPacket.getAddress(), receivingPacket.getPort()));
                       }
                   } catch (SocketException e) {

                   } catch (IOException e) {

                   }
                });
            }

        } catch (SocketException e) {

        }
    }

    /**
     * Method to stop server
     */
    @Override
    public synchronized void close() {
        service.shutdownNow();
        reciever.close();
        isStart = false;
    }
}
