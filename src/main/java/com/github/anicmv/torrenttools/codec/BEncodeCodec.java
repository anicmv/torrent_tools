package com.github.anicmv.torrenttools.codec;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class BEncodeCodec {
    private final Charset charset;
    
    public BEncodeCodec() {
        this(StandardCharsets.UTF_8);
    }
    
    public BEncodeCodec(Charset charset) {
        this.charset = charset;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] bytes, Class<T> type) throws IOException {
        BEncodeInputStream in = new BEncodeInputStream(new ByteArrayInputStream(bytes), charset);
        if (type == Map.class) {
            return (T) in.readDictionary();
        }
        if (type == List.class) {
            return (T) in.readList();
        }
        if (type == Long.class) {
            return (T) in.readNumber();
        }
        if (type == String.class) {
            return (T) in.readString();
        }
        if (type == byte[].class) {
            return (T) in.readBytes();
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    public byte[] encode(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BEncodeOutputStream bos = new BEncodeOutputStream(out, charset);
        if (obj instanceof String s) {
            bos.writeString(s);
        } else if (obj instanceof Number n) {
            bos.writeNumber(n.longValue());
        } else if (obj instanceof Map m) {
            bos.writeDictionary(m);
        } else if (obj instanceof List l) {
            bos.writeList(l);
        } else if (obj instanceof byte[] b) {
            bos.writeBytes(b);
        } else {
            throw new IllegalArgumentException("Unsupported: " + obj.getClass());
        }
        return out.toByteArray();
    }

    public void save(File file, Map<String, Object> data) throws IOException {
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(file)) {
            new BEncodeOutputStream(out, charset).writeDictionary(data);
        }
    }
}
