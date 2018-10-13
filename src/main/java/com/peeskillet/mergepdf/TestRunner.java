package com.peeskillet.mergepdf;

public class TestRunner {

    public static void main(String... args) throws Exception {
        Main.main(new String[]{
                "data/one.pdf",
                "data/two.pdf",
                "data/three.pdf",
                "-o", "output/merged.pdf",
                "-v"
        });
    }
}
