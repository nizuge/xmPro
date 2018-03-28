package cn.anytec.controller;

import cn.anytec.quadrant.findface.FindFaceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(value = "/anytec/enrollFace",method = RequestMethod.POST)
    @ResponseBody
    public boolean enrollFace(HttpServletRequest request, @RequestParam("photo") MultipartFile file){

        byte[] pic = null;
        try{
            if(!file.isEmpty()){
                pic = file.getBytes();
            }
            if(pic == null)
                return false;
            if(FindFaceHandler.getInstance().enrollFace(request.getParameterMap(),pic))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
