package cn.anytec.quadrant.util;

public class Constant {
    private static ConfigManager sdk = ConfigManager.getInstance();
    public static final String TOKEN = sdk.getParameter("TOKEN");
    public static final String SDK_IP = sdk.getParameter("SDK_IP");
    public static final String IDENTIFY_THRESHOLD = sdk.getParameter("IDENTIFY_THRESHOLD");

    public static final int CAMERA_AMOUNT = Integer.valueOf(sdk.getParameter("CAMERA_AMOUNT"));
    public static final String IMG_HOME = sdk.getParameter("IMG_HOME");

    public static final long STRATEGY_VIDEO_MAX = Long.valueOf(sdk.getParameter("STRATEGY_VIDEO_MAX"));
    public static final long STRATEGY_VIDEO_MIN = Long.valueOf(sdk.getParameter("STRATEGY_VIDEO_MIN"));
    public static final int STRATEGY_VIDEO_SCALE = Integer.valueOf(sdk.getParameter("STRATEGY_VIDEO_SCALE"));
    public static final String STRATEGY_VIDEO_FPS = sdk.getParameter("STRATEGY_VIDEO_FPS");

}
