package ru.ifmo.ctddev.krasnotsvetov.udpapp;

import info.kgeorgiy.java.advanced.hello.HelloClient;
import info.kgeorgiy.java.advanced.hello.Util;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Krasnotsvetov on 09.05.2016.
 */


/**
 * Client that sends request and receives responses
 * Client cab be created and start send messages from the command line with five parameters
 * url/ip-address, port, request, number of threads, number of request per thread
 * Client also begins start send messages with {@code start} method
 */
public class HelloUDPClient implements HelloClient{


    public class Animal {

    }

    public class Dog extends Animal {

    }

    public  class Bird extends  Animal {

    }

    private static final int TIMEOUT = 50;

    /**
     * Method to start the client from command line
     * Client can be created from command line with 5 parameters:
     * url/ip-address
     * port
     * request
     * number of threads
     * number of request per thread
     * @param args
     */
    public static void main(String[] args) {



        if (args == null || args.length == 0 || args.length > 5) {
            return;
        }

        String url = args[0];
        String request = args[2];
        int port = 7777;
        int threads = 1;
        int requestAmount = 1;
        try {
            port = Integer.parseInt(args[1]);
            threads = Integer.parseInt(args[3]);
            requestAmount = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            return;
        }

        new HelloUDPClient().start(url, port, request, requestAmount, threads);
    }

    /**
     * Method to start sending requests
     * @param url address to send request to
     * @param port port to connect to
     * @param request the prefix of the request
     * @param requestAmount number of requests per thread
     * @param threads number of threads to send request
     */
    @Override
    public void start(String url, int port, String request, int requestAmount , int threads) {

        if (port < 0 || port > 65535 || threads < 1) {
            return;
        }
        final InetAddress address;
        try {
             address = InetAddress.getByName(url);
        } catch (UnknownHostException e) {
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            final int tID = i;
            executor.submit(() -> {
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setSoTimeout(TIMEOUT);
                    for (int id = 0; id < requestAmount; id++) {
                        String ans = request + tID + "_" + id;
                        byte[] sendInfo = ans.getBytes(Util.CHARSET);
                        int length = sendInfo.length;
                        DatagramPacket sendPacket = new DatagramPacket(sendInfo, length, address, port);
                        DatagramPacket receivePacket = new DatagramPacket(new byte[length + 8], length + 8);
                        String sendString = new String("Hello, ".getBytes(Util.CHARSET), 0, "Hello, ".getBytes(Util.CHARSET).length) + ans;
                        String receiveString = "";
                        while (!sendString.equals(receiveString)) {
                            try {
                                socket.send(sendPacket);
                                socket.receive(receivePacket);
                                receiveString = new String(receivePacket.getData(), 0, receivePacket.getLength(), Util.CHARSET);

                            } catch (IOException e) {

                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdownNow();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
