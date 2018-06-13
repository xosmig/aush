package com.xosmig.swdesignhw.aush;

import com.xosmig.swdesignhw.aush.textui.TextUserInterface;

public class CommandLineShell {
    private TextUserInterface ui = new TextUserInterface(System.in, System.out);

    /**
     * Runs a new bash-like command line shell.
     */
    void run() throws InterruptedException {
        ui.run();
    }
}
