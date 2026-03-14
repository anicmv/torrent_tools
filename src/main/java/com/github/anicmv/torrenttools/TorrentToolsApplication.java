package com.github.anicmv.torrenttools;

import com.github.anicmv.torrenttools.command.*;
import picocli.CommandLine;

@CommandLine.Command(
    name = "torrent",
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    header = "TorrentTools CLI%nTools for inspecting, creating and modifying bittorrent metafiles.%n",
    optionListHeading = "%nOptions:%n",
    footer = "%nDeveloped by ❤️ anicmv :)",
    subcommands = {InfoCommand.class, EditCommand.class, CreateCommand.class, MagnetCommand.class}
)
public class TorrentToolsApplication implements Runnable {
    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new TorrentToolsApplication()).execute(args));
    }
}
