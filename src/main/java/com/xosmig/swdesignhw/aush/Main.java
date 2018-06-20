package com.xosmig.swdesignhw.aush;

public class Main {
    public static void main(String[] args) {
        try {
            new CommandLineShell().run();
        } catch (InterruptedException e) {
            // unreachable
            e.printStackTrace();
            System.exit(2);
        }
    }
}
