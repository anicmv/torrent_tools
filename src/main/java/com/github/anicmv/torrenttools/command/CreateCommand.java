package com.github.anicmv.torrenttools.command;

import com.github.anicmv.torrenttools.service.TorrentCreatorService;
import picocli.CommandLine;
import java.io.File;

@CommandLine.Command(name = "create", mixinStandardHelpOptions = true,
    header = "Create a torrent file for the specified file.", optionListHeading = "%nOptions:%n")
public class CreateCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "The file to create a torrent for.")
    private File inputFile;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output file path. [default: <name>.torrent]")
    private String outputPath;

    @CommandLine.Option(names = {"-a", "--announce"}, defaultValue = "https://example.com",
        description = "Announce URL(s), comma-separated.")
    private String announceUrl;

    @CommandLine.Option(names = {"-c", "--comment"}, defaultValue = "TorrentTools", description = "Comment.")
    private String comment;

    @CommandLine.Option(names = {"-n", "--name"}, description = "Torrent name.")
    private String torrentName;

    @CommandLine.Option(names = {"-l", "--piece-size"}, description = "Piece size (e.g., 256K, 1M). Auto if not set.")
    private String pieceLength;

    @CommandLine.Option(names = {"-p", "--private"}, defaultValue = "false", description = "Private flag.")
    private boolean privateFlag;

    @CommandLine.Option(names = {"-s", "--source"}, defaultValue = "anicmv :)", description = "Source.")
    private String source;

    @CommandLine.Option(names = {"--created-by"}, defaultValue = "TorrentTools", description = "Created by.")
    private String createdBy;

    @CommandLine.Option(names = {"--publisher"}, defaultValue = "anicmv :)", description = "Publisher.")
    private String publisher;

    @CommandLine.Option(names = {"-d", "--creation-date"}, description = "Creation date (ISO-8601 or POSIX).")
    private String creationDate;

    @CommandLine.Option(names = {"--no-created-by"}, description = "Omit created by.")
    private boolean noCreatedBy;

    @CommandLine.Option(names = {"--no-creation-date"}, description = "Omit creation date.")
    private boolean noCreationDate;

    @CommandLine.Option(names = {"--no-publisher"}, description = "Omit publisher.")
    private boolean noPublisher;

    @CommandLine.Option(names = {"--no-source"}, description = "Omit source.")
    private boolean noSource;

    @Override
    public void run() {
        TorrentCreatorService.Options opt = new TorrentCreatorService.Options();
        opt.inputFile = inputFile;
        opt.outputPath = outputPath;
        opt.announceUrl = announceUrl;
        opt.comment = comment;
        opt.torrentName = torrentName;
        opt.pieceLengthStr = pieceLength;
        opt.privateFlag = privateFlag;
        opt.source = source;
        opt.createdBy = createdBy;
        opt.publisher = publisher;
        opt.creationDate = creationDate;
        opt.noCreatedBy = noCreatedBy;
        opt.noCreationDate = noCreationDate;
        opt.noPublisher = noPublisher;
        opt.noSource = noSource;
        new TorrentCreatorService().create(opt);
    }
}
