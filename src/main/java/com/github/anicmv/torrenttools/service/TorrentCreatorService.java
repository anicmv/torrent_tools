package com.github.anicmv.torrenttools.service;

import com.github.anicmv.torrenttools.codec.BEncodeCodec;
import com.github.anicmv.torrenttools.util.TreePrinter;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class TorrentCreatorService {
    private final BEncodeCodec codec = new BEncodeCodec();

    public static class Options {
        public File inputFile;
        public String outputPath, announceUrl, comment, torrentName, pieceLengthStr;
        public boolean privateFlag;
        public String source, createdBy, publisher, creationDate;
        public boolean noCreatedBy, noCreationDate, noPublisher, noSource;
    }

    public void create(Options opt) {
        if (opt.inputFile == null || !opt.inputFile.exists()) {
            throw new IllegalArgumentException("Input file not found");
        }
        TreePrinter.print(opt.inputFile.getAbsolutePath(), true);
        try {
            Map<String, Object> info = buildInfo(opt);
            Map<String, Object> meta = buildMeta(opt);
            meta.put("info", info);
            File out = getOutputFile(opt);
            codec.save(out, meta);
            System.out.println("Torrent created: " + out.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getOutputFile(Options opt) {
        String name = opt.torrentName != null ? opt.torrentName : opt.inputFile.getName();
        if (opt.outputPath != null) {
            if (opt.outputPath.endsWith(".torrent")) {
                File f = new File(opt.outputPath);
                if (opt.torrentName == null) {
                    opt.torrentName = f.getName().replace(".torrent", "");
                }
                return f;
            }
            File d = new File(opt.outputPath);
            if (!d.exists()) {
                d.mkdirs();
            }
            return new File(d, name + ".torrent");
        }
        return new File(opt.inputFile.getParentFile(), name + ".torrent");
    }

    private Map<String, Object> buildInfo(Options opt) throws Exception {
        Map<String, Object> info = new LinkedHashMap<>();
        File f = opt.inputFile;
        long size = f.isDirectory() ? dirSize(f) : f.length();
        int pieceLen = parsePieceSize(opt.pieceLengthStr, size);
        info.put("piece length", pieceLen);
        info.put("pieces", hashPieces(f, pieceLen));
        info.put("name", opt.torrentName != null ? opt.torrentName : f.getName());
        if (f.isFile()) {
            info.put("length", f.length());
        } else {
            info.put("files", collectFiles(f, f));
        }
        if (opt.privateFlag) {
            info.put("private", 1);
        }
        if (!opt.noSource && opt.source != null) {
            info.put("source", opt.source);
        }
        return info;
    }

    private Map<String, Object> buildMeta(Options opt) {
        Map<String, Object> meta = new LinkedHashMap<>();
        if (opt.announceUrl != null) {
            List<String> urls = Arrays.asList(opt.announceUrl.split(","));
            meta.put("announce", urls.get(0).trim());
            List<List<String>> tiers = new ArrayList<>();
            for (String u : urls) {
                tiers.add(Collections.singletonList(u.trim()));
            }
            meta.put("announce-list", tiers);
        }
        if (!opt.noCreatedBy && opt.createdBy != null) {
            meta.put("created by", opt.createdBy);
        }
        if (!opt.noPublisher && opt.publisher != null) {
            meta.put("publisher", opt.publisher);
        }
        if (!opt.noCreationDate) {
            long ts = opt.creationDate != null ? parseDate(opt.creationDate) : Instant.now().getEpochSecond();
            meta.put("creation date", ts);
        }
        if (opt.comment != null) {
            meta.put("comment", opt.comment);
        }
        return meta;
    }

    private long parseDate(String s) {
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toEpochSecond(ZoneOffset.of("Z"));
        } catch (Exception e) {
            try {
                return Long.parseLong(s);
            } catch (Exception ex) {
                return Instant.now().getEpochSecond();
            }
        }
    }

    private int parsePieceSize(String s, long size) {
        if (s == null || "auto".equalsIgnoreCase(s)) {
            if (size < 1024*1024) {
                return 16*1024;
            }
            if (size < 10*1024*1024) {
                return 32*1024;
            }
            if (size < 100*1024*1024) {
                return 64*1024;
            }
            if (size < 512*1024*1024) {
                return 128*1024;
            }
            if (size < 1024*1024*1024L) {
                return 256*1024;
            }
            if (size < 4L*1024*1024*1024) {
                return 512*1024;
            }
            return 1024*1024;
        }
        s = s.trim().toUpperCase();
        int mult = 1;
        if (s.endsWith("K")) {
            mult = 1024;
            s = s.substring(0, s.length()-1);
        } else if (s.endsWith("M")) {
            mult = 1024*1024;
            s = s.substring(0, s.length()-1);
        }
        int ps = Integer.parseInt(s) * mult;
        if (ps < 16384 || ps > 67108864 || (ps & (ps-1)) != 0) {
            throw new IllegalArgumentException("Invalid piece size");
        }
        return ps;
    }

    private long dirSize(File d) {
        long s = 0;
        File[] f = d.listFiles();
        if (f != null) {
            for (File x : f) {
                s += x.isDirectory() ? dirSize(x) : x.length();
            }
        }
        return s;
    }

    private List<Map<String, Object>> collectFiles(File base, File d) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        File[] f = d.listFiles();
        if (f != null) {
            Arrays.sort(f);
            for (File x : f) {
                if (x.isFile()) {
                    Map<String, Object> e = new LinkedHashMap<>();
                    e.put("length", x.length());
                    String rel = base.toPath().relativize(x.toPath()).toString();
                    e.put("path", Arrays.asList(rel.split("[/\\\\]")));
                    list.add(e);
                } else if (x.isDirectory()) {
                    list.addAll(collectFiles(base, x));
                }
            }
        }
        return list;
    }

    private byte[] hashPieces(File f, int pieceLen) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<File> files = f.isDirectory() ? allFiles(f) : Collections.singletonList(f);
        byte[] buf = new byte[pieceLen];
        int off = 0;
        for (File x : files) {
            try (FileInputStream in = new FileInputStream(x)) {
                int r;
                while ((r = in.read(buf, off, pieceLen - off)) != -1) {
                    off += r;
                    if (off == pieceLen) {
                        out.write(sha1.digest(buf));
                        sha1.reset();
                        off = 0;
                    }
                }
            }
        }
        if (off > 0) {
            out.write(sha1.digest(buf, 0, off));
        }
        return out.toByteArray();
    }

    private List<File> allFiles(File d) {
        List<File> list = new ArrayList<>();
        File[] f = d.listFiles();
        if (f != null) {
            Arrays.sort(f);
            for (File x : f) {
                if (x.isFile()) {
                    list.add(x);
                } else {
                    list.addAll(allFiles(x));
                }
            }
        }
        return list;
    }
}
