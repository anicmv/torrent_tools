package com.github.anicmv.torrenttools.codec;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

class BEncodeInputStream {
    private final PushbackInputStream in;
    private final Charset charset;

    BEncodeInputStream(InputStream in, Charset charset) {
        this.in = new PushbackInputStream(in);
        this.charset = charset;
    }

    private int read() throws IOException {
        return in.read();
    }
    
    private void unread(int b) throws IOException {
        if (b != -1) {
            in.unread(b);
        }
    }

    String readString() throws IOException {
        StringBuilder len = new StringBuilder();
        int c;
        while ((c = read()) != -1 && c != ':') {
            if (!Character.isDigit(c)) {
                throw new IOException("Invalid: " + (char)c);
            }
            len.append((char)c);
        }
        int n = Integer.parseInt(len.toString());
        byte[] buf = new byte[n];
        if (in.readNBytes(buf, 0, n) != n) {
            throw new IOException("EOF");
        }
        return new String(buf, charset);
    }

    byte[] readBytes() throws IOException {
        StringBuilder len = new StringBuilder();
        int c;
        while ((c = read()) != -1 && c != ':') {
            len.append((char)c);
        }
        int n = Integer.parseInt(len.toString());
        byte[] buf = new byte[n];
        if (in.readNBytes(buf, 0, n) != n) {
            throw new IOException("EOF");
        }
        return buf;
    }

    Long readNumber() throws IOException {
        if (read() != 'i') {
            throw new IOException("Expected 'i'");
        }
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = read()) != -1 && c != 'e') {
            sb.append((char)c);
        }
        return Long.parseLong(sb.toString());
    }

    List<Object> readList() throws IOException {
        if (read() != 'l') {
            throw new IOException("Expected 'l'");
        }
        List<Object> list = new ArrayList<>();
        while (true) {
            int c = read();
            if (c == 'e') {
                break;
            }
            unread(c);
            list.add(readElement());
        }
        return list;
    }

    Map<String, Object> readDictionary() throws IOException {
        if (read() != 'd') {
            throw new IOException("Expected 'd'");
        }
        Map<String, Object> map = new LinkedHashMap<>();
        while (true) {
            int c = read();
            if (c == 'e') {
                break;
            }
            unread(c);
            String key = readString();
            c = read();
            unread(c);
            map.put(key, readElement());
        }
        return map;
    }

    private Object readElement() throws IOException {
        int c = read();
        if (c == -1) {
            throw new IOException("EOF");
        }
        return switch (c) {
            case 'i' -> {
                unread(c);
                yield readNumber();
            }
            case 'l' -> {
                unread(c);
                yield readList();
            }
            case 'd' -> {
                unread(c);
                yield readDictionary();
            }
            default -> {
                if (Character.isDigit(c)) {
                    unread(c);
                    yield readString();
                } else {
                    throw new IOException("Invalid: " + (char)c);
                }
            }
        };
    }
}

enum BType { STRING, NUMBER, LIST, DICTIONARY, UNKNOWN }
