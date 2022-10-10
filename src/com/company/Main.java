package com.company;


import java.io.*;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.*;

public class Main {
    private static int countChars(String str, char ch) {
        int counter = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                counter++;
            }
        }

        return counter;
    }
    public static void main(String[] args) throws IOException {
        String name = "";
        try {
            name = args[0];
        } catch (IndexOutOfBoundsException e) {
            System.exit(0);
        }

        Scanner scanner = new Scanner(System.in);
        String path = "";
        String[] commands = {};

        ZipEntry[] entry = new ZipEntry[Integer.MAX_VALUE / 1024];
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(name);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            int count = 0;
            while (entries.hasMoreElements()) {
                entry[count] = entries.nextElement();
                count++;
            }

        } catch (Exception ex) {
            System.err.println(ex.toString() + "\n");
        }
        String[] entryNames = new String[entry.length - 1];
        for (int i = 0; i < entry.length - 1; i++) {
            if (entry[i] == null) break;
            entryNames[i] = entry[i].getName();
            if (entryNames[i].charAt(entryNames[i].length() - 1) == '/') {
                entryNames[i] = entryNames[i].substring(0, entryNames[i].length() - 1);
            }

        }
        commands = Interface(scanner, commands, path);
        while (!commands[0].equals("exit")) {
            if (commands[0].equals("pwd")) {
                if (commands.length > 1) {
                    System.out.println("too many arguments");
                }
                pwd(path);
            } else if (commands[0].equals("ls")) {
                if (commands.length > 1) {
                    System.out.println("too many arguments");
                }
                ls(path, entryNames);
            } else if (commands[0].equals("cd")) {
                if (commands.length > 2) {
                    System.out.println("too many arguments");
                }
                path = cd(path, commands[1], entry);
            } else if (commands[0].equals("cat")) {
                if (commands.length > 2) {
                    System.out.println("too many arguments");
                }
                cat(path, commands[1], entry,zipFile);
            } else {
                System.out.println(commands[0] + ": command not found");
            }
            commands = Interface(scanner, commands, path);

        }
    }

    private static void cat(String path, String fileName, ZipEntry[] entry, ZipFile zipFile) throws IOException {
        int count1 = countChars(path, '/');
        path += "/";

        for (ZipEntry i : entry) {
            if (i == null) break;
            if (path.equals("/")) {
                if (!(i.getName().equals(path))) {
                    if ((!i.isDirectory()) && count1 == countChars(i.getName(), '/')) {
                        String name = i.getName();
                        if (name.equals(fileName)) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(i)));
                            String line = null;
                            while ((line=bufferedReader.readLine())!=null){
                                System.out.println(line);
                            }
                            return;
                        }
                    }
                }
            } else {
                if (i.getName().contains(path) && !(i.getName().equals(path))) {
                    if (!i.isDirectory() && count1 == countChars(i.getName(), '/') - 1) {
                        String name = i.getName().substring(i.getName().lastIndexOf("/")+1, i.getName().length());
                        if (name.equals(fileName)) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(i)));
                            String line = null;
                            while ((line=bufferedReader.readLine())!=null){
                                System.out.println(line);
                            }
                            return;

                        }
                    }
                }
            }
        }
        System.out.println("No such file");
    }

    private static String cd(String path, String pathDir, ZipEntry[] entry) {
        String fpath = path;
        int count1 = countChars(path, '/');
        if (pathDir.equals("~")) return "";
        else if (pathDir.equals("..")) {
            if (path.lastIndexOf("/") != -1) {
                return path.substring(0, path.lastIndexOf("/"));
            }
            return "";
        } else {
            path += "/";
            for (ZipEntry i : entry) {
                if (i == null) break;
                if (path.equals("/")) {

                    if (!(i.getName().equals(path)) && i.isDirectory()) {
                        if (count1 == countChars(i.getName(), '/') - 1) {
                            String path2 = i.getName();
                            path2 = path2.substring(0, path2.length() - 1);
                            if (path2.equals(pathDir)) return fpath + pathDir;
                        }
                    }
                } else {
                    if (i.getName().contains(path) && !(i.getName().equals(path))) {
                        if (i.isDirectory() && count1 == countChars(i.getName(), '/') - 2) {
                            String path2 = i.getName();
                            path2 = path2.substring(0, path2.lastIndexOf('/'));
                            path2 = path2.substring(path2.lastIndexOf('/') + 1, path2.length());
                            if (path2.equals(pathDir)) return path + pathDir;
                        }
                    }
                }
            }
        }
        System.out.println("No such directory " + pathDir);
        return fpath;
    }

    private static void ls(String path, String[] entryNames) {
        int count1 = countChars(path, '/');
        path += "/";
        for (String i : entryNames) {
            if (i == null) break;
            if (path.equals("/")) {
                if (!(i.equals(path))) {
                    if (count1 == countChars(i, '/')) {
                        System.out.println(i);
                    }
                }
            } else {
                if (i.contains(path) && !(i.equals(path))) {
                    if (count1 == (countChars(i, '/') - 1)) {
                        System.out.println(i);
                    }
                }
            }

        }


    }


    private static void pwd(String path) {
        System.out.println((path == "") ? "/root" : "/root/" + path + "/");
    }

    public static String[] Interface(Scanner scanner, String[] commands, String path) {
        System.out.print("root/" + path + " ");
        commands = scanner.nextLine().split(" ");
        return commands;
    }


}

