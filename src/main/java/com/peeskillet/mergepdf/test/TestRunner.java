package com.peeskillet.mergepdf.test;

import com.peeskillet.mergepdf.Main;

public class TestRunner {

    public static void main(String... args) throws Exception {
        Main.main(new String[]{
                "data/one.pdf",
                "data/two.pdf",
                "data/three.pdf",
                "data/more",
                "-o", "output/merged.pdf",
                "-v"
        });
    }
}
