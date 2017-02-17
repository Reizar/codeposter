package com.aaronrama.codeposter;

/**
 * Created by aaron on 15/02/17.
 */

import java.awt.Color;

public class ColourUtility {

    public static String rgbToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", normalize(r), normalize(g), normalize(b));
    }

    // Have disabled alpha for a bit as it was causing issues.
    public static String rgbaToHex(int r, int g, int b, int a) {
        return String.format("#%02x%02x%02x", normalize(r), normalize(g), normalize(b));
    }

    private static int normalize(int val) {
        if (val > 255) {
            return 255;
        } else if (val < 0) {
            return 0;
        } else {
            return val;
        }
    }
}
