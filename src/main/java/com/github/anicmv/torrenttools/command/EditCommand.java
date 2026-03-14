package com.github.anicmv.torrenttools.command;

import com.github.anicmv.torrenttools.service.TorrentEditorService;
import picocli.CommandLine;
import java.io.File;

@CommandLine.Command(name = "edit", mixinStandardHelpOptions = true,
    header = "Edit the torrent file.", optionListHeading = "%nOptions:%n")
public class EditCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "The torrent file to edit.")
    private File inputFile;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output file path.")
    private String outputPath;

    @CommandLine.Option(names = {"-a", "--announce"}, description = "Announce URL(s).")
    private String announceUrl;

    @CommandLine.Option(names = {"-c", "--comment"}, description = "Comment.")
    private String comment;

    @CommandLine.Option(names = {"-n", "--name"}, description = "Torrent name.")
    private String torrentName;

    @CommandLine.Option(names = {"-p", "--private"}, description = "Private flag (yes/no).")
    private String privateFlag;

    @CommandLine.Option(names = {"-s", "--source"}, description = "Source.")
    private String source;

    @CommandLine.Option(names = {"--create-by"}, description = "Created by.")
    private String createBy;

    @CommandLine.Option(names = {"--publisher"}, description = "Publisher.")
    private String publisher;

    @CommandLine.Option(names = {"-d", "--creation-date"}, description = "Creation date.")
    private String creationDate;

    @CommandLine.Option(names = {"--no-created-by"}, description = "Omit created by.")
    private boolean noCreatedBy;

    @CommandLine.Option(names = {"--no-creation-date"}, description = "Omit creation date.")
    private boolean noCreationDate;

    @CommandLine.Option(names = {"--no-publisher"}, description = "Omit publisher.")
    private boolean noPublisher;

    @CommandLine.Option(names = {"--no-source"}, description = "Omit source.")
    private boolean noSource;

    @CommandLine.Option(names = {"--no-announce"}, description = "Omit announce.")
    private boolean noAnnounce;

    @Override
    public void run() {
        if (!inputFile.exists()) {
            throw new CommandLine.ParameterException(new CommandLine(this), "File not found: " + inputFile);
        }
        TorrentEditorService.Options opt = new TorrentEditorService.Options();
        opt.inputFile = inputFile;
        opt.outputPath = outputPath;
        opt.announceUrl = announceUrl;
        opt.comment = comment;
        opt.torrentName = torrentName;
        opt.privateFlag = privateFlag;
        opt.source = source;
        opt.createBy = createBy;
        opt.publisher = publisher;
        opt.creationDate = creationDate;
        opt.noCreatedBy = noCreatedBy;
        opt.noCreationDate = noCreationDate;
        opt.noPublisher = noPublisher;
        opt.noSource = noSource;
        opt.noAnnounce = noAnnounce;
        new TorrentEditorService().edit(opt);
    }
}
