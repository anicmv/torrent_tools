package com.github.anicmv.torrenttools.meta;

import java.util.*;

public record TorrentInfo(
    Map<String, Object> metaInfo, String infoHash, String magnetUri, String name,
    String protocolVersion, String announce, List<String> announceList, Long creationDate,
    String comment, String createdBy, Long pieceLength, String pieces, Long privateFlag,
    String source, String torrentName, Long length, List<FileInfo> files
) {
    public record FileInfo(Long length, List<String> path) {}

    public static class Builder {
        private Map<String, Object> metaInfo = Collections.emptyMap();
        private String infoHash, magnetUri, name, protocolVersion = "v1", announce = "";
        private List<String> announceList = Collections.emptyList();
        private Long creationDate;
        private String comment = "", createdBy = "", pieces = "", source = "", torrentName = "";
        private Long pieceLength, privateFlag, length = 0L;
        private List<FileInfo> files = Collections.emptyList();

        public Builder metaInfo(Map<String, Object> v) {
            this.metaInfo = v;
            return this;
        }
        
        public Builder infoHash(String v) {
            this.infoHash = v;
            return this;
        }
        
        public Builder magnetUri(String v) {
            this.magnetUri = v;
            return this;
        }
        
        public Builder name(String v) {
            this.name = v;
            return this;
        }
        
        public Builder protocolVersion(String v) {
            this.protocolVersion = v;
            return this;
        }
        
        public Builder announce(String v) {
            this.announce = v;
            return this;
        }
        
        public Builder announceList(List<String> v) {
            this.announceList = v;
            return this;
        }
        
        public Builder creationDate(Long v) {
            this.creationDate = v;
            return this;
        }
        
        public Builder comment(String v) {
            this.comment = v;
            return this;
        }
        
        public Builder createdBy(String v) {
            this.createdBy = v;
            return this;
        }
        
        public Builder pieceLength(Long v) {
            this.pieceLength = v;
            return this;
        }
        
        public Builder pieces(String v) {
            this.pieces = v;
            return this;
        }
        
        public Builder privateFlag(Long v) {
            this.privateFlag = v;
            return this;
        }
        
        public Builder source(String v) {
            this.source = v;
            return this;
        }
        
        public Builder torrentName(String v) {
            this.torrentName = v;
            return this;
        }
        
        public Builder length(Long v) {
            this.length = v;
            return this;
        }
        
        public Builder files(List<FileInfo> v) {
            this.files = v;
            return this;
        }
        
        public TorrentInfo build() {
            return new TorrentInfo(metaInfo, infoHash, magnetUri, name, protocolVersion,
                announce, announceList, creationDate, comment, createdBy, pieceLength,
                pieces, privateFlag, source, torrentName, length, files);
        }
    }
}
