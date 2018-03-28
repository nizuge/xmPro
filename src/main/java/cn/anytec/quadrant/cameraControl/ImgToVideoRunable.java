package cn.anytec.quadrant.cameraControl;

import cn.anytec.quadrant.util.Constant;
import cn.anytec.quadrant.util.FileComparer;
import cn.anytec.quadrant.util.RuntimeLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

public class ImgToVideoRunable implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(ImgToVideoRunable.class);
    public static final String videoDir = "generateVideo";

    @Override
    public void run() {
        File file = new File(Constant.IMG_HOME);
        if(!file.exists()){
            if(!file.mkdirs())
                logger.error("存储图片的文件夹不存在，创建文件夹失败");
        }
        while (true){
            try {
                File[] visitors = file.listFiles();
                if(visitors == null || visitors.length == 0){
                    Thread.sleep(2000);
                    continue;
                }

                for (File visitor : visitors){
                    if(!visitor.isDirectory())
                        continue;

                    File[] camViews = visitor.listFiles();
                    if(camViews == null||camViews.length == 0)
                        continue;
                    for(File view:camViews){
                        boolean hasVideoDir = false;
                        for(String dirName:view.list()){
                            if(dirName.equals(videoDir)){
                                hasVideoDir = true;
                                break;
                            }
                        }
                        if(!hasVideoDir){
                            if(!new File(view,videoDir).mkdir()){
                                logger.warn("创建视频文件夹失败！");
                                continue;
                            }
                        }
                        File[] videoList = view.listFiles();
                        if(videoList.length < 2)
                            continue;
                        boolean canRecord = CameraDataHandler.canMakeVideo(visitor.getName());
                        if(videoList == null||(videoList.length<3&&!canRecord)){
                            continue;
                        }

                        Arrays.sort(videoList,new FileComparer());

                        if(videoList[0].list().length < 10){
                            clearDir(videoList[0]);
                            continue;
                        }
                        String picsPath = videoList[0].getAbsolutePath();

                        RuntimeLocal runtimeLocal = new RuntimeLocal();
                        StringBuilder cmd = new StringBuilder("ffmpeg");
                        String outName = System.currentTimeMillis()+".mp4";
                        cmd.append(" -framerate ").append(Constant.STRATEGY_VIDEO_FPS)
                                .append(" -pattern_type glob")
                                .append(" -i ").append(picsPath).append("/*.jpeg ")
                                .append(visitor.getAbsolutePath())
                                .append(File.separator).append(view.getName()).append(File.separator)
                                .append(videoDir).append(File.separator)
                                .append(outName);
                        logger.info("开始生成"+visitor.getName()+outName);
                        runtimeLocal.execute(cmd.toString());
                        while (runtimeLocal.isAlive()){
                            Thread.sleep(500);
                        }
                        boolean generateVideo = false;
                        String[] videos = new File(view,videoDir).list();
                        for(String video:videos){
                            if(video.equals(outName))
                                generateVideo = true;
                        }
                        if(generateVideo){
                            logger.info("已生成"+visitor.getName()+"视频:"+outName);
                            logger.info("删除文件夹"+videoList[0].getName()+"结果:"+clearDir(videoList[0]));
                        }
                    }

                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (Exception e){
                logger.error("图片转视频时出现异常");
                e.printStackTrace();
            }

        }

    }

    //删除文件夹下所有文件及当前文件夹
    private boolean clearDir(File dir){
        if(!dir.exists()){
            logger.warn("要删除的图片文件夹不存在");
            return true;
        }
        if (dir.isDirectory()) {
            String[] childrens = dir.list();
            if(childrens.length == 0){
                logger.warn("要删除的文件夹为空");
                return true;
            }
            for (String children:childrens) {
                File file = new File(dir,children);
                if(!clearDir(file)){
                    return false;
                }
            }
        }
        if(dir.delete())
            return true;
        return false;
    }
}
