package com.xosmig.swdesignhw.aush;

public class CommandLineShell {
    private TextUserInterface ui = new TextUserInterface(System.in, System.out);

    void run() throws InterruptedException {
        ui.run();
    }
}
