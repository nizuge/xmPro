package cn.anytec.quadrant.imgToVideoUtil;

import cn.anytec.quadrant.imgToVideoUtil.gifUtil.AnimatedGifEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class JpgToGif {

    public static void main(String[] args) {
       // System.out.println(clearDir(new File("/home/anytec-z/zzz/test")));
        JpgToGif jpgToGif = new JpgToGif();
        jpgToGif.makeGif("/home/anytec-z/Pictures/test");
    }

    private synchronized void makeGif(String path) {

        File[] files = new File(path).listFiles();
        //图片文件夹下图片均以时间戳命名，根据时间先后排序
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                int i1 = Integer.valueOf(file1.getName().substring(5));
                int i2 = Integer.valueOf(file2.getName().substring(5));
                return i1-i2;
            }
        });
        double scale = 3;
        int length = (int)Math.floor(files.length / scale);
        BufferedImage[] bufferedImages = new BufferedImage[length];
        int frame = 0;
        try{
            for(int i = 0;i < files.length;i++){
                System.out.println(files[i].getName());
                if((i+1)%scale == 0){
                    bufferedImages[frame] = ImageIO.read(files[i]);
                    frame++;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnimatedGifEncoder gif = new AnimatedGifEncoder();
        gif.start("/home/anytec-z/Pictures/test.gif");//gif生成位置
        gif.setRepeat(0);
        gif.setDelay((int)(50/scale));
        gif.setSize(-1,-1);
        for (BufferedImage image : bufferedImages){
            System.out.println("Time:"+System.currentTimeMillis());
            gif.addFrame(image);
        }
    }


}