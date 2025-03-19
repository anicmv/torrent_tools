package com.github.anicmv.commands.sub;

import com.github.anicmv.options.CreateCommandOptions;
import com.github.anicmv.utils.TorrentUtils;
import picocli.CommandLine;

/**
 * @author anicmv :)
 * @date 2024/5/26 17:05
 * @description Create a torrent file for the specified file.
 */
@CommandLine.Command(name = "create",
        mixinStandardHelpOptions = true,
        version = "0.0.1",
        header = "Create a torrent file for the specified file.",
        optionListHeading = "%nOptions: %n")
public class CreateCommand implements Runnable {

    @CommandLine.Mixin
    private CreateCommandOptions options;

    @Override
    public void run() {
        TorrentUtils.create(options);
    }
}
