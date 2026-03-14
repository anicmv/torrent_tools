package com.github.anicmv.torrenttools.util;

import java.io.*;
import java.text.*;

public class TreePrinter {
    private static final String RESET = "\u001B[0m", BLUE = "\u001B[34m", GREEN = "\u001B[32m", PURPLE = "\u001B[35m";

    public static void print(String path, boolean color) {
        File root = new File(path);
        if (!root.exists()) {
            System.out.println("Not found: " + path);
            return;
        }
        System.out.println(fmt(root.getName(), color, true));
        if (root.isDirectory()) {
            printTree(root, "", color);
        }
    }

    private static void printTree(File d, String pfx, boolean color) {
        File[] f = d.listFiles();
        if (f == null) {
            return;
        }
        for (int i = 0; i < f.length; i++) {
            boolean last = i == f.length - 1;
            String line = pfx + (last ? "└── " : "├── ");
            if (f[i].isDirectory()) {
                System.out.println(line + fmt(f[i].getName() + " [" + size(dirSize(f[i])) + "]", color, true));
                printTree(f[i], pfx + (last ? "    " : "│   "), color);
            } else {
                System.out.println(line + fmt(f[i].getName() + " [" + size(f[i].length()) + "]", color, false));
            }
        }
    }

    private static long dirSize(File d) {
        long s = 0;
        File[] f = d.listFiles();
        if (f != null) {
            for (File x : f) {
                s += x.isDirectory() ? dirSize(x) : x.length();
            }
        }
        return s;
    }

    private static String fmt(String n, boolean c, boolean dir) {
        if (!c) {
            return n;
        }
        return (dir ? BLUE : GREEN) + n + RESET;
    }

    private static String size(long s) {
        if (s <= 0) {
            return "0 B";
        }
        String[] u = {"B", "KB", "MB", "GB", "TB"};
        int i = (int)(Math.log10(s) / Math.log10(1024));
        i = Math.min(i, u.length - 1);
        return new DecimalFormat("#,##0.#").format(s / Math.pow(1024, i)) + " " + u[i];
    }
}
