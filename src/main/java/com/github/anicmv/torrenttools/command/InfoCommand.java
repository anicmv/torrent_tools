package com.github.anicmv.torrenttools.command;

import com.github.anicmv.torrenttools.meta.TorrentInfo;
import com.github.anicmv.torrenttools.service.TorrentService;
import picocli.CommandLine;
import java.io.File;
import java.io.IOException;

@CommandLine.Command(name = "info", mixinStandardHelpOptions = true,
    header = "General information about bittorrent metafiles.", optionListHeading = "%nOptions:%n")
public class InfoCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = ".torrent file path.")
    private File inputFile;

    @CommandLine.Option(names = {"--raw"}, description = "Print the metafile data formatted as JSON.")
    private boolean raw;

    @Override
    public void run() {
        try {
            TorrentService service = new TorrentService();
            TorrentInfo info = service.loadTorrent(inputFile.getAbsolutePath());
            if (raw) {
                System.out.print(service.toJson(info.metaInfo()));
                return;
            }
            System.out.println("Metafile:         " + info.name());
            System.out.println("Protocol version: " + info.protocolVersion());
            System.out.println("InfoHash:         v1: " + info.infoHash());
            System.out.println("Piece size:       " + info.pieceLength());
            System.out.println("Created by:       " + info.createdBy());
            System.out.println("Created on:       " + info.creationDate());
            System.out.println("Private:          " + (info.privateFlag() != null && info.privateFlag() == 1));
            System.out.println("Name:             " + info.torrentName());
            System.out.println("Source:           " + info.source());
            System.out.println("Comment:          " + info.comment());
            System.out.println("\nAnnounces:        ");
            info.announceList().forEach(a -> System.out.println("  " + a));
            System.out.println("\nFiles:            ");
            if (!info.files().isEmpty()) {
                info.files().forEach(f -> System.out.println("  [" + f.length() + "] " + f.path().get(0)));
            } else {
                System.out.println("[" + info.length() + "] " + info.torrentName());
            }
        } catch (IOException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), e.getMessage(), e);
        }
    }
}
