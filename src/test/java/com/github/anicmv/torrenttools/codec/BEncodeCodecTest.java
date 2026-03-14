package com.github.anicmv.torrenttools.codec;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BEncodeCodecTest {
    private final BEncodeCodec codec = new BEncodeCodec();

    @Test
    void testEncodeDecodeString() throws IOException {
        String orig = "Hello, World!";
        assertEquals(orig, codec.decode(codec.encode(orig), String.class));
    }

    @Test
    void testEncodeDecodeNumber() throws IOException {
        Long orig = 12345L;
        assertEquals(orig, codec.decode(codec.encode(orig), Long.class));
    }

    @Test
    void testEncodeDecodeList() throws IOException {
        List<Object> orig = Arrays.asList("test", 42L, "another");
        assertEquals(orig, codec.decode(codec.encode(orig), List.class));
    }

    @Test
    void testEncodeDecodeDictionary() throws IOException {
        Map<String, Object> orig = new LinkedHashMap<>();
        orig.put("name", "test");
        orig.put("count", 100L);
        @SuppressWarnings("unchecked")
        Map<String, Object> dec = codec.decode(codec.encode(orig), Map.class);
        assertEquals(orig.get("name"), dec.get("name"));
        assertEquals(orig.get("count"), dec.get("count"));
    }
}
