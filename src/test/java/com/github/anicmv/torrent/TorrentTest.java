package com.github.anicmv.torrent;

import com.github.anicmv.torrent.generator.TorrentMetaInfoGenerator;
import com.github.anicmv.torrent.reader.TorrentReader;
import com.github.anicmv.torrent.entity.Torrent;
import com.github.anicmv.torrent.entity.TorrentMetaInfo;
import org.junit.Test;

import java.io.IOException;

/**
 * @author anicmv
 * @date 2024/6/3 20:38
 * @description TODO
 */
public class TorrentTest {
    @Test
    public void t1() throws Exception {
        String path = "/Users/username/Downloads/1.torrent";
        Torrent torrent = TorrentReader.build(path);
        System.out.println(torrent);
    }

    @Test
    public void t2() throws IOException {
        String path = "/Users/username/Downloads/1.torrent";
        Torrent torrent = TorrentReader.build(path);
        TorrentMetaInfo generate = new TorrentMetaInfoGenerator(torrent).generate();
        System.out.println(generate);
    }
}
