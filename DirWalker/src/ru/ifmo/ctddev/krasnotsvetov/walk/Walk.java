package ru.ifmo.ctddev.krasnotsvetov.walk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Walk {

    protected class HashCalculateException extends  Exception {
        public HashCalculateException(String string) {
            super(string);
        }
    }
    protected final static String failedHash = String.format("%032x", 0x0);

    public static void main (String args []) {
        new Walk().start(args);
    }

    public  void start(String args []) {
        if (args == null || args.length  != 2) {
            System.out.println("wrong arguments");
            return;
        }
        String inputName = args[0];
        String outputName = args[1];
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputName), "UTF8"));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputName), "UTF8"))) {
            String path;
            while((path = in.readLine()) != null) {
                makeWalk(path, out);
            }
        } catch (IOException exception) {
            System.out.println("Can't work with file: " + exception.getMessage());
        }
    }

    public void makeWalk(String path, BufferedWriter out) {
        try {
            String result =  getMD5(path);
            result += " " + path + "\n";
            print(result, out);

        } catch (HashCalculateException exception) {
            print(failedHash + " " + path + "\n", out);
        }
    }

    protected void print(String res, BufferedWriter out) {
        try {
            out.write(res);
        } catch (IOException exception) {
            System.out.println("Can't work with file: " + exception.getMessage());
        }
    }

    public String getMD5(String path) throws HashCalculateException{
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] bytes = new byte[1024];
            try (InputStream is = Files.newInputStream(Paths.get(path))) {
                int numBytes = 0;
                while ((numBytes = is.read(bytes)) != -1) {
                    md.update(bytes, 0, numBytes);
                }
            } catch (IOException exception) {
                throw new HashCalculateException("Can't open file: " + exception.getMessage());
            }
            byte[] digest = md.digest();
            return byteArrayToHexString(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new HashCalculateException("MD5 does not support");
        }
    }
    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Byte b : bytes) {
            stringBuilder.append(String.format("%02X", b));
        }
        return stringBuilder.toString().toUpperCase();
    }
}
