package com.xosmig.swdesignhw.aush;

import com.xosmig.swdesignhw.aush.textui.TextUserInterface;

public class CommandLineShell {
    private TextUserInterface ui = new TextUserInterface(System.in, System.out);

    void run() throws InterruptedException {
        ui.run();
    }
}
