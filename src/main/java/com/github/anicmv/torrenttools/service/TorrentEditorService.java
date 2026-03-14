package com.github.anicmv.torrenttools.service;

import com.github.anicmv.torrenttools.codec.BEncodeCodec;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class TorrentEditorService {
    private final BEncodeCodec codec = new BEncodeCodec();

    public static class Options {
        public File inputFile;
        public String outputPath, announceUrl, comment, torrentName, privateFlag;
        public String source, createBy, publisher, creationDate;
        public boolean noCreatedBy, noCreationDate, noPublisher, noSource, noAnnounce;
    }

    @SuppressWarnings("unchecked")
    public void edit(Options opt) {
        try {
            byte[] data = Files.readAllBytes(opt.inputFile.toPath());
            Map<String, Object> meta = codec.decode(data, Map.class);
            Map<String, Object> info = (Map<String, Object>) meta.get("info");
            if (info == null) {
                info = new LinkedHashMap<>();
                meta.put("info", info);
            }

            if (opt.announceUrl != null) {
                List<String> urls = Arrays.asList(opt.announceUrl.split(","));
                meta.put("announce", urls.get(0).trim());
                List<List<String>> tiers = new ArrayList<>();
                for (String u : urls) {
                    tiers.add(Collections.singletonList(u.trim()));
                }
                meta.put("announce-list", tiers);
            }
            if (opt.comment != null) {
                meta.put("comment", opt.comment);
            }
            if (opt.publisher != null) {
                meta.put("publisher", opt.publisher);
            }
            if (opt.createBy != null) {
                meta.put("created by", opt.createBy);
            }
            if (opt.creationDate != null) {
                meta.put("creation date", parseDate(opt.creationDate));
            }
            if (opt.torrentName != null) {
                info.put("name", opt.torrentName);
            }
            if (opt.source != null) {
                info.put("source", opt.source);
            }
            if (opt.privateFlag != null) {
                if ("yes".equalsIgnoreCase(opt.privateFlag)) {
                    info.put("private", 1);
                } else if ("no".equalsIgnoreCase(opt.privateFlag)) {
                    info.remove("private");
                }
            }
            if (opt.noAnnounce) {
                meta.remove("announce");
                meta.remove("announce-list");
            }
            if (opt.noCreatedBy) {
                meta.remove("created by");
            }
            if (opt.noCreationDate) {
                meta.remove("creation date");
            }
            if (opt.noPublisher) {
                meta.remove("publisher");
            }
            if (opt.noSource) {
                info.remove("source");
            }

            File out = opt.outputPath != null ?
                (opt.outputPath.endsWith(".torrent") ? new File(opt.outputPath) : new File(new File(opt.outputPath), opt.inputFile.getName()))
                : opt.inputFile;
            if (out.getParentFile() != null) {
                out.getParentFile().mkdirs();
            }
            codec.save(out, meta);
            System.out.println("Torrent edited: " + out.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long parseDate(String s) {
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toEpochSecond(ZoneOffset.of("Z"));
        } catch (Exception e) {
            try {
                return Long.parseLong(s);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Invalid date: " + s);
            }
        }
    }
}
