package com.github.anicmv.torrenttools.service;

import cn.hutool.json.JSONUtil;
import com.github.anicmv.torrenttools.codec.BEncodeCodec;
import com.github.anicmv.torrenttools.meta.TorrentInfo;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class TorrentService {
    private final BEncodeCodec codec = new BEncodeCodec();

    public TorrentInfo loadTorrent(String path) throws IOException {
        byte[] content = Files.readAllBytes(Path.of(path));
        Map<String, Object> metaInfo = codec.decode(content, Map.class);
        String infoHash = calcInfoHash(metaInfo);
        String magnet = genMagnet(metaInfo, infoHash);
        return buildInfo(new File(path), metaInfo, infoHash, magnet);
    }

    public String generateMagnetUri(String path) throws IOException {
        return loadTorrent(path).magnetUri();
    }

    public String toJson(Map<String, Object> metaInfo) {
        return JSONUtil.toJsonPrettyStr(metaInfo);
    }

    private String calcInfoHash(Map<String, Object> meta) throws IOException {
        Object info = meta.get("info");
        if (!(info instanceof Map)) {
            throw new IOException("Invalid torrent");
        }
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(codec.encode(info));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }

    private String genMagnet(Map<String, Object> meta, String hash) {
        @SuppressWarnings("unchecked")
        Map<String, Object> info = (Map<String, Object>) meta.get("info");
        String name = Optional.ofNullable(info.get("name")).map(Object::toString).orElse("unknown");
        String announce = Optional.ofNullable(meta.get("announce")).map(Object::toString).orElse("");
        StringBuilder sb = new StringBuilder("magnet:?xt=urn:btih:").append(hash)
            .append("&dn=").append(URLEncoder.encode(name, StandardCharsets.UTF_8));
        if (!announce.isEmpty()) {
            sb.append("&tr=").append(URLEncoder.encode(announce, StandardCharsets.UTF_8));
        }
        Object list = meta.get("announce-list");
        if (list instanceof List<?> l) {
            Set<String> trackers = new HashSet<>();
            for (Object o : l) {
                if (o instanceof List<?> t) {
                    t.forEach(x -> {
                        if (x instanceof String s) {
                            trackers.add(s);
                        }
                    });
                } else if (o instanceof String s) {
                    trackers.add(s);
                }
            }
            trackers.remove(announce);
            trackers.forEach(t -> sb.append("&tr=").append(URLEncoder.encode(t, StandardCharsets.UTF_8)));
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private TorrentInfo buildInfo(File f, Map<String, Object> meta, String hash, String magnet) {
        Map<String, Object> info = (Map<String, Object>) meta.get("info");
        if (info == null) {
            info = Collections.emptyMap();
        }
        List<String> announceList = new ArrayList<>();
        Object list = meta.get("announce-list");
        if (list instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof List<?> t) {
                    t.forEach(x -> {
                        if (x instanceof String s) {
                            announceList.add(s);
                        }
                    });
                } else if (o instanceof String s) {
                    announceList.add(s);
                }
            }
        }
        List<TorrentInfo.FileInfo> files = new ArrayList<>();
        Object filesObj = info.get("files");
        if (filesObj instanceof List<?> fl) {
            for (Object o : fl) {
                if (o instanceof Map<?, ?> m) {
                    Long len = getLong(m.get("length"));
                    List<String> path = new ArrayList<>();
                    Object p = m.get("path");
                    if (p instanceof List<?> pl) {
                        pl.forEach(x -> path.add(x != null ? x.toString() : ""));
                    }
                    files.add(new TorrentInfo.FileInfo(len, path));
                }
            }
        }
        return new TorrentInfo.Builder()
            .metaInfo(meta).infoHash(hash).magnetUri(magnet).name(f.getName())
            .announce(getString(meta.get("announce"))).announceList(announceList)
            .creationDate(getLong(meta.get("creation date")))
            .comment(getString(meta.get("comment"))).createdBy(getString(meta.get("created by")))
            .pieceLength(getLong(info.get("piece length"))).privateFlag(getLong(info.get("private")))
            .source(getString(info.get("source"))).torrentName(getString(info.get("name")))
            .length(getLong(info.get("length"))).files(files).build();
    }

    private String getString(Object o) {
        return o != null ? o.toString() : "";
    }
    
    private Long getLong(Object o) {
        return o instanceof Number n ? n.longValue() : 0L;
    }
}
