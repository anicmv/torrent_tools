package com.github.anicmv.torrenttools.codec;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

class BEncodeOutputStream {
    private final OutputStream out;
    private final Charset charset;
    
    BEncodeOutputStream(OutputStream out, Charset charset) {
        this.out = out;
        this.charset = charset;
    }

    void writeString(String s) throws IOException {
        writeBytes(s.getBytes(charset));
    }
    
    void writeBytes(byte[] b) throws IOException {
        out.write(Integer.toString(b.length).getBytes(StandardCharsets.US_ASCII));
        out.write(':');
        out.write(b);
    }
    
    void writeNumber(long n) throws IOException {
        out.write('i');
        out.write(Long.toString(n).getBytes(StandardCharsets.US_ASCII));
        out.write('e');
    }
    
    void writeList(List<?> list) throws IOException {
        out.write('l');
        for (Object e : list) {
            writeElement(e);
        }
        out.write('e');
    }
    
    void writeDictionary(Map<?, ?> map) throws IOException {
        out.write('d');
        List<Map.Entry<?, ?>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(e -> e.getKey().toString()));
        for (Map.Entry<?, ?> e : entries) {
            writeString(e.getKey().toString());
            writeElement(e.getValue());
        }
        out.write('e');
    }
    
    private void writeElement(Object o) throws IOException {
        if (o instanceof String s) {
            writeString(s);
        } else if (o instanceof Number n) {
            writeNumber(n.longValue());
        } else if (o instanceof Map m) {
            writeDictionary(m);
        } else if (o instanceof List l) {
            writeList(l);
        } else if (o instanceof byte[] b) {
            writeBytes(b);
        } else {
            throw new IllegalArgumentException("Unsupported: " + o.getClass());
        }
    }
}
