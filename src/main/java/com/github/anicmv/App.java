package com.github.anicmv;

import com.github.anicmv.commands.TorrentToolsCommand;
import picocli.CommandLine;

/**
 * @author anicmv :)
 * @date 2024/5/26 11:15
 */
public class App {
    public static void main(String[] args) {
        new CommandLine(new TorrentToolsCommand()).execute(args);
    }
}
