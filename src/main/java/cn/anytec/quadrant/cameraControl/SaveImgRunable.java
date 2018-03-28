package cn.anytec.quadrant.cameraControl;

import cn.anytec.quadrant.cameraData.FDCameraData;
import cn.anytec.quadrant.cameraData.IdentifiedPerson;
import cn.anytec.quadrant.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SaveImgRunable implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(SaveImgRunable.class);

    private FDCameraData data;
    private static volatile Map<String,Map<String,String>> camList = new HashMap<>();

    public SaveImgRunable(FDCameraData fdCameraData){
        this.data = fdCameraData;
    }

    @Override
    public void run() {
        String dataTime = System.currentTimeMillis()+".jpeg";
        Map<String,String> baklist = camList.get(data.mStrMac);
        if(baklist == null){
            baklist = new HashMap();
        }
        List<IdentifiedPerson> identifiedPersons = CameraDataHandler.getIdentifiedList(data.mStrMac);
        logger.debug("缓存的人物数:"+baklist.size());
        Map<String,String> newList = new HashMap<>();
        Iterator<IdentifiedPerson> iterator = identifiedPersons.iterator();

        while (iterator.hasNext()){
            IdentifiedPerson person = iterator.next();
            if(System.currentTimeMillis()-person.getInitTime() > Constant.STRATEGY_VIDEO_MAX){
                logger.info(person.getMeta()+"的视频录制超时重置");
                iterator.remove();
                CameraDataHandler.logoutFace(person.getMeta());
                continue;
            }

            String meta = person.getMeta();
            synchronized (baklist){
                if(!baklist.containsKey(meta)){
                    String dir = meta+File.separator+data.mStrMac+File.separator+System.currentTimeMillis();
                    baklist.put(meta,dir);
                    logger.info("新的识别");
                    newList.put(meta,dir);
                    logger.info("存储路径："+newList.get(meta));
                    continue;
                }
            }
            newList.put(meta,baklist.get(meta));
        }
        camList.put(data.mStrMac,newList);
        for (String key:newList.keySet()){
            String dir = newList.get(key);
            try{
                File file = new File(Constant.IMG_HOME+dir);
                if(!file.exists()){
                    boolean flag = file.mkdirs();
                    if(!flag)
                        logger.warn("创建文件夹失败");
                }
                file = new File(Constant.IMG_HOME+dir+File.separator+dataTime);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.mJpgData);
                BufferedImage image = ImageIO.read(byteArrayInputStream);
                ImageIO.write(image,"jpeg",file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
