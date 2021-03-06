package org.literacyapp.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by sladomic on 26.11.16.
 */

public class AiHelper {

    public static File getAiDirectory() {
        File aiDirectory = new File(Environment.getExternalStorageDirectory() + "/.literacyapp/ai");
        if (!aiDirectory.exists()) {
            aiDirectory.mkdirs();
        }
        return aiDirectory;
    }

    public static File getModelDirectory() {
        File modelDirectory = new File(getAiDirectory(), "model");
        if (!modelDirectory.exists()) {
            modelDirectory.mkdir();
        }
        return modelDirectory;
    }
}
