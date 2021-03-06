package org.literacyapp.authentication.helper;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import org.literacyapp.authentication.animaloverlay.AnimalOverlay;
import org.literacyapp.authentication.fallback.StudentAuthenticationActivity;
import org.literacyapp.util.RootHelper;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;

/**
 * Created by sladomic on 27.12.16.
 */

public class DetectionHelper {
    private static final Scalar RED_COLOR = new Scalar(255, 0, 0, 255);
    private static final int MAX_TIME_BEFORE_FALLBACK = 15000;
    private static final int SCREEN_BRIGHTNESS_INCREASE_RATE = 20;
    private static final int SCREEN_BRIGHTNESS_MAX = 255;
    private static final float IMAGE_BRIGHTNESS_THRESHOLD = 0.5f;
    private static final int SCREEN_BRIGHTNESS_DEFAULT = 85;
    private static final int SCREEN_BRIGHTNESS_MODE_DEFAULT = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
    private static final int DISPLAY_TEMPERATURE_NIGHT_DEFAULT = 2500;
    private static final int DISPLAY_TEMPERATURE_NIGHT_BRIGHTER = 4500;

    public static boolean isFaceInsideFrame(AnimalOverlay animalOverlay, Mat img, Rect face){
        if (animalOverlay != null){
            Point frameTopLeft = new Point(animalOverlay.getFrameStartX(), animalOverlay.getFrameStartY());
            Point frameBottomRight = new Point(animalOverlay.getFrameEndX(), animalOverlay.getFrameEndY());
            Rect frame = new Rect(frameTopLeft, frameBottomRight);
            if ((face.tl().x >= frame.tl().x) && (face.tl().y >= frame.tl().y) && (face.br().x <= frame.br().x) && (face.br().y <= frame.br().y)){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static void drawArrowFromFaceToFrame(AnimalOverlay animalOverlay, Mat img, Rect face){
        Rect mirroredFace = MatOperation.getMirroredFaceForFrontCamera(img, face);
        Point pointFace = new Point(mirroredFace.tl().x + mirroredFace.width / 2, mirroredFace.tl().y + mirroredFace.height / 2);
        Point pointFrame = new Point(animalOverlay.getFrameStartX() + (animalOverlay.getFrameEndX() - animalOverlay.getFrameStartX()) / 2, animalOverlay.getFrameStartY() + (animalOverlay.getFrameEndY() - animalOverlay.getFrameStartY()) / 2);
        Imgproc.arrowedLine(img, pointFace, pointFrame, RED_COLOR, 20, Imgproc.LINE_8, 0, 0.2);
    }

    public static synchronized boolean shouldFallbackActivityBeStarted(long startTimeFallback, long currentTime){
        if (startTimeFallback + MAX_TIME_BEFORE_FALLBACK < currentTime){
            return true;
        } else {
            return false;
        }
    }

    public static synchronized void startFallbackActivity(Context context, String classNameForLogging){
        Log.i(DetectionHelper.class.getName(), "startFallbackActivity");

        Intent intent = new Intent(context, StudentAuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // http://answers.opencv.org/question/24260/how-to-determine-an-image-with-strong-or-weak-illumination-in-opencv/?answer=24342#post-id-24342
    private static double getImageBrightness(Mat img){
        Mat temp = new Mat();
        List<Mat> color = new ArrayList<Mat>(3);
        Mat lum = new Mat();
        temp = img;

        Core.split(temp, color);

        if(color.size() > 0){
            Core.multiply(color.get(0), new Scalar(0.299), color.get(0));
            Core.multiply(color.get(1), new Scalar(0.587), color.get(1));
            Core.multiply(color.get(2), new Scalar(0.114), color.get(2));

            Core.add(color.get(0),color.get(1),lum);
            Core.add(lum, color.get(2), lum);

            Scalar sum = Core.sumElems(lum);

            return sum.val[0]/((1<<8 - 1)*img.rows() * img.cols()) * 2;
        } else {
            return 1;
        }
    }

    /**
     * Check if the image brightness is above the threshold. If not, increase the screen brightness
     * @param context
     * @param img
     */
    public static synchronized void setIncreasedScreenBrightness(Context context, Mat img){
        double currentImageBrightness = getImageBrightness(img);
        if (currentImageBrightness < IMAGE_BRIGHTNESS_THRESHOLD){
            setScreenBrightnessModeManual(context);
            setIncreasedScreenBrightness(context, currentImageBrightness);
            setIncreasedDisplayTemperatureNight();
        }
    }

    /**
     * Set Screen Brightness Mode to manual mode if not already set
     * @param context
     */
    private static synchronized void setScreenBrightnessModeManual(Context context){
        int screenBrightnessMode = getScreenBrightnessMode(context);
        if (screenBrightnessMode != Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL){
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Log.i(DetectionHelper.class.getName(), "setScreenBrightnessModeManual: Screen brightness mode set to manual.");
        }
    }

    /**
     * Increase screen brightness by a certain rate
     * @param context
     * @param currentImageBrightness
     */
    private static synchronized void setIncreasedScreenBrightness(Context context, double currentImageBrightness){
        try {
            int currentScreenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            int increasedScreenBrightness = currentScreenBrightness + SCREEN_BRIGHTNESS_INCREASE_RATE;
            if (increasedScreenBrightness <= SCREEN_BRIGHTNESS_MAX){
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, increasedScreenBrightness);
                Log.i(DetectionHelper.class.getName(), "setIncreasedScreenBrightness: Screen brightness has been increased: currentImageBrightness: " + currentImageBrightness + " currentScreenBrightness: " + currentScreenBrightness + " increasedScreenBrightness: " + increasedScreenBrightness);
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.e(DetectionHelper.class.getName(), null, e);
        }
    }

    /**
     * Increase display temperature night if not already increased
     */
    private static synchronized void setIncreasedDisplayTemperatureNight(){
        try {
            int displayTemperatureNight = getDisplayTemperatureNight();
            if (displayTemperatureNight <= DISPLAY_TEMPERATURE_NIGHT_BRIGHTER){
                RootHelper.runAsRoot(new String[] {"settings --cm put system display_temperature_night " + DISPLAY_TEMPERATURE_NIGHT_BRIGHTER});
                Log.i(DetectionHelper.class.getName(), "setIncreasedDisplayTemperatureNight: display_temperature_night set to " + DISPLAY_TEMPERATURE_NIGHT_BRIGHTER);
            }
        } catch (IOException e) {
            Log.e(DetectionHelper.class.getName(), null, e);
        } catch (InterruptedException e) {
            Log.e(DetectionHelper.class.getName(), null, e);
        }
    }

    public static synchronized int getScreenBrightness(Context context){
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(DetectionHelper.class.getName(), null, e);
            return SCREEN_BRIGHTNESS_DEFAULT;
        }
    }

    public static synchronized int getScreenBrightnessMode(Context context){
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(DetectionHelper.class.getName(), null, e);
            return SCREEN_BRIGHTNESS_MODE_DEFAULT;
        }
    }

    public static synchronized int getDisplayTemperatureNight(){
        try {
            String display_temperature_night = RootHelper.runAsRoot(new String[] {"settings --cm get system display_temperature_night"});
            return Integer.valueOf(display_temperature_night);
        } catch (IOException e) {
            Log.e(DetectionHelper.class.getName(), null, e);
            return DISPLAY_TEMPERATURE_NIGHT_DEFAULT;
        } catch (InterruptedException e) {
            Log.e(DetectionHelper.class.getName(), null, e);
            return DISPLAY_TEMPERATURE_NIGHT_DEFAULT;
        }
    }

    public static synchronized void setDefaultScreenBrightnessAndMode(Context context, int screenBrightnessMode, int screenBrightness, int displayTemperatureNight){
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, screenBrightnessMode);
        Log.i(DetectionHelper.class.getName(), "setDefaultScreenBrightnessAndMode: SCREEN_BRIGHTNESS_MODE set to " + screenBrightnessMode);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightness);
        Log.i(DetectionHelper.class.getName(), "setDefaultScreenBrightnessAndMode: SCREEN_BRIGHTNESS set to " + screenBrightness);
        try {
            RootHelper.runAsRoot(new String[] {"settings --cm put system display_temperature_night " + displayTemperatureNight});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(DetectionHelper.class.getName(), "setDefaultScreenBrightnessAndMode: display_temperature_night set to " + displayTemperatureNight);
    }
}
