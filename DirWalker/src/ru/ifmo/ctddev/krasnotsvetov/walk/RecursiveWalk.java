package ru.ifmo.ctddev.krasnotsvetov.walk;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RecursiveWalk extends  Walk {


    @Override
    public void makeWalk(String path, BufferedWriter out) {
        dfsDirectories(path, out);
    }

    public static void main (String args []) {
        new RecursiveWalk().start(args);
    }

    private void dfsDirectories(String pathName, BufferedWriter out) {
        Path path = Paths.get(pathName);
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry: stream) {
                    dfsDirectories(entry.toString(), out);
                }
            } catch (IOException exception) {
                print(failedHash + " " + pathName + "\n", out);
            }
        } else {
            try {
                print(super.getMD5(pathName) + " " + pathName + "\n", out);
            } catch (HashCalculateException exception) {
                print(failedHash + " " + pathName + "\n", out);
            }
        }
    }
}
