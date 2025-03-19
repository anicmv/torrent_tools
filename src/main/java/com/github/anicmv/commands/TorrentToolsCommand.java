package com.github.anicmv.commands;

import com.github.anicmv.commands.sub.CreateCommand;
import com.github.anicmv.commands.sub.EditCommand;
import com.github.anicmv.commands.sub.InfoCommand;
import com.github.anicmv.commands.sub.MagnetCommand;
import picocli.CommandLine;

/**
 * @author anicmv
 * @date 2024/5/26 19:00
 * @description 种子工具命令行
 */
@CommandLine.Command(name = "torrent",
        mixinStandardHelpOptions = true,
        requiredOptionMarker = '*',
        version = "0.0.1",
        header = "TorrentTools CLI%nTools for inspecting, creating and modifying bittorrent metafiles.%n",
        optionListHeading = "%nOptions: %n",
        footer = "%nDeveloped by ❤\uFE0Fanicmv :)",
        subcommandsRepeatable = true,
        commandListHeading = "%nSubCommands: %n",
        subcommands = {
                InfoCommand.class,
                EditCommand.class,
                CreateCommand.class,
                MagnetCommand.class
        })
public class TorrentToolsCommand implements Runnable {

    @Override
    public void run() {
    }

    public static void main(String[] args) {
        new CommandLine(new TorrentToolsCommand()).execute(args);
    }
}
