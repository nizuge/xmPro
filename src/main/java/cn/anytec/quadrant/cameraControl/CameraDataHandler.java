package cn.anytec.quadrant.cameraControl;

import cn.anytec.quadrant.cameraData.FDCameraData;
import cn.anytec.quadrant.cameraData.IdentifiedPerson;
import cn.anytec.quadrant.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(CameraDataHandler.class);
    private static ExecutorService threadPool = Executors.newFixedThreadPool(Constant.CAMERA_AMOUNT);
    private static ExecutorService saveImgthreadPool = Executors.newFixedThreadPool(5);
    private static Map<String,Boolean> cam_threadStatusMap = new HashMap<>();
    private static volatile Map<String,List<IdentifiedPerson>> identifiedPersons = new HashMap<>();
    private static volatile Map<String,Boolean> knownFaceList = new HashMap<>();

    public static void notifyFace(FDCameraData data){
        if(!cam_threadStatusMap.containsKey(data.mStrMac)||!cam_threadStatusMap.get(data.mStrMac)){
            cam_threadStatusMap.put(data.mStrMac,true);
            Thread thread = new Thread(new CameraDataRunable(data));
            thread.setDaemon(true);
            threadPool.execute(thread);

        }
    }

    public static void setThreadStatus(String camMac,boolean status){
        cam_threadStatusMap.put(camMac,status);
    }

    public static boolean containsMac(String camMac){
        if(identifiedPersons.containsKey(camMac))
            return true;
        return false;
    }

    public static void setMac(String camMac){
        identifiedPersons.put(camMac,new ArrayList<IdentifiedPerson>());
    }

    public static List<IdentifiedPerson> getIdentifiedList(String mac){
        return identifiedPersons.get(mac);
    }

    public static void notifyImg(FDCameraData data){
        List list = CameraDataHandler.getIdentifiedList(data.mStrMac);
        if(list == null||list.size() == 0)
            return;
        Thread thread = new Thread(new SaveImgRunable(data));
        thread.setDaemon(true);
        saveImgthreadPool.execute(thread);

    }

    protected static void enrollFace(String meta){
        logger.info("拍摄到人脸："+meta);
        knownFaceList.put(meta,true);
    }
    protected static void logoutFace(String meta){
        logger.info("人脸消失："+meta);
        knownFaceList.put(meta,false);
    }
    protected static boolean canMakeVideo(String meta){
        if(knownFaceList.containsKey(meta))
            return !knownFaceList.get(meta);
        logger.error("已识别人脸没有创建对应文件夹！");
        return false;
    }

}
