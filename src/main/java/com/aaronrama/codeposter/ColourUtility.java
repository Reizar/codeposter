package com.aaronrama.codeposter;

/**
 * Created by aaron on 15/02/17.
 */

import java.awt.Color;

public class ColourUtility {

    public static int normalize(int val) {
        if (val > 255) {
            return 255;
        } else if (val < 0) {
            return 0;
        } else {
            return val;
        }
    }
}
