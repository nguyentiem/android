package com.pdfreader.scanner.pdfviewer.utils;

import java.util.Random;

public class RandomUtils {
    public static boolean randomRange(int percentage) {
        Random random = new Random();
        int randomNumber = random.nextInt(99) + 1;
        return randomNumber <= percentage;
    }
}
