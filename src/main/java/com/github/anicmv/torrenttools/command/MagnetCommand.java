package com.github.anicmv.torrenttools.command;

import com.github.anicmv.torrenttools.service.TorrentService;
import picocli.CommandLine;
import java.io.File;
import java.io.IOException;

@CommandLine.Command(name = "magnet", mixinStandardHelpOptions = true,
    header = "Generate a magnet URI from a torrent file.", optionListHeading = "%nOptions:%n")
public class MagnetCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = ".torrent file path.")
    private File inputFile;

    @Override
    public void run() {
        if (!inputFile.exists()) {
            throw new CommandLine.ParameterException(new CommandLine(this), "File not found: " + inputFile);
        }
        try {
            TorrentService service = new TorrentService();
            System.out.print(service.generateMagnetUri(inputFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), e.getMessage(), e);
        }
    }
}
