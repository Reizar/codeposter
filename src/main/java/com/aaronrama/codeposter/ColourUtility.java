package com.aaronrama.codeposter;

/**
 * Created by aaron on 15/02/17.
 */

import java.awt.Color;

public class ColourUtility {

    public static String rgbToHex(int r, int g, int b) {
        return  "#" + Integer.toString(r, 16) +
                Integer.toString(g, 16) +
                Integer.toString(b, 16);
    }

    public static String rgbaToHex(int r, int g, int b, int a) {
        return String.format("#%02x%02x%02x",r, g, b);

    }
}
