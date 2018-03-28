package cn.anytec.quadrant.cameraControl;

import cn.anytec.quadrant.cameraData.FDCameraData;
import cn.anytec.quadrant.cameraData.FaceDefine;
import cn.anytec.quadrant.cameraData.IdentifiedPerson;
import cn.anytec.quadrant.findface.FindFaceHandler;
import cn.anytec.quadrant.findface.IdentifyFace;
import cn.anytec.quadrant.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class CameraDataRunable implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(CameraDataRunable.class);

    private FDCameraData data;

    public CameraDataRunable(FDCameraData fdCameraData){
        this.data = fdCameraData;
    }

    @Override
    public void run() {
        try {
            FaceDefine[] faceDefines = FindFaceHandler.getInstance().imageDetect(data.mJpgData);
            if(faceDefines == null)
                return;
            if(faceDefines.length == 0)
                return;
            List<IdentifyFace> identifyFaceList = FindFaceHandler.getInstance().imageIdentify(null, data.mJpgData);

            List<IdentifiedPerson> identifiedPersons = CameraDataHandler.getIdentifiedList(data.mStrMac);
            Iterator<IdentifiedPerson> iterator = identifiedPersons.iterator();
            stop:while (iterator.hasNext()){
                IdentifiedPerson identifiedPerson = iterator.next();
                if(identifyFaceList != null){
                    for(int i=0;i<identifyFaceList.size();i++){
                        if(identifyFaceList.get(i)==null)
                            continue ;
                        if(identifyFaceList.get(i).getMeta().equals(identifiedPerson.getMeta())){
                            logger.info("更新已记录人脸");
                            identifyFaceList.set(i,null);
                            identifiedPerson.setAppearTime(System.currentTimeMillis());
                            continue stop;
                        }
                    }
                }

                if(System.currentTimeMillis()-identifiedPerson.getAppearTime()> Constant.STRATEGY_VIDEO_MIN){
                    logger.info("停止记录已识别人脸:"+identifiedPerson.getMeta()+"的视频帧");
                    CameraDataHandler.logoutFace(identifiedPerson.getMeta());
                    iterator.remove();
                }

            }
            if(identifyFaceList == null){
                logger.info("未识别到已注册人脸");
                return;
            }

            for(IdentifyFace identifyFace:identifyFaceList){
                if(identifyFace == null)
                    continue ;
                String meta = identifyFace.getMeta();
                if(meta == null||meta.equals(""))
                    continue;
                identifiedPersons.add(new IdentifiedPerson(meta));
                CameraDataHandler.enrollFace(meta);
                logger.info("开始记录已识别人脸:"+meta+"的视频帧");
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CameraDataHandler.setThreadStatus(data.mStrMac,false);
        }


    }

}
