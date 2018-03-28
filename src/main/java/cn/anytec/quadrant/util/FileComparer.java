package cn.anytec.quadrant.util;

import cn.anytec.quadrant.cameraControl.ImgToVideoRunable;

import java.io.File;
import java.util.Comparator;

public class FileComparer implements Comparator{
    @Override
    public int compare(Object o1, Object o2) {
        String numberName1 = ((File)o1).getName();
        if(numberName1.equals(ImgToVideoRunable.videoDir))
            return 1;
        numberName1 = numberName1.substring(5);
        String numberName2 =  ((File)o2).getName();
        if(numberName2.equals(ImgToVideoRunable.videoDir))
            return -1;
        numberName2 = numberName2.substring(5);
        return new Integer(numberName1) - new Integer(numberName2);
    }
}
