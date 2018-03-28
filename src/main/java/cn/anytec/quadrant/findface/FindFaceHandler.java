package cn.anytec.quadrant.findface;


import cn.anytec.quadrant.cameraData.FDCameraData;
import cn.anytec.quadrant.cameraData.FaceDefine;
import cn.anytec.quadrant.util.Constant;
import cn.anytec.quadrant.util.FindFaceUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

@Component
public class FindFaceHandler {
    private static Logger logger = LoggerFactory.getLogger(FindFaceHandler.class);
    private static Map<String,Boolean> cameraThreadMap = new HashMap<>(Constant.CAMERA_AMOUNT);
    private static FindFaceHandler singleton = new FindFaceHandler();
    private static JSONParser jsonParser = new JSONParser();


    private FindFaceHandler(){}
    public static FindFaceHandler getInstance(){
        return singleton;
    }

    public void saveFace(FaceDefine faceDefine, FDCameraData fdCameraData, String[] metas){
        int x1= Double.valueOf(faceDefine.left).intValue();
        int x2 = Double.valueOf(faceDefine.right).intValue();
        int y1 = Double.valueOf(faceDefine.top).intValue();
        int y2 = Double.valueOf(faceDefine.bottom).intValue();
        int width = x2-x1;
        int height = y2-y1;
        String path = "/home/anytec-z/Pictures/sdpImages/["+x1+","+x2+","+y1+","+y2+"]";
        for(String s:metas){
            path=path+","+s;
        }

        ByteArrayInputStream in = null;
        try {

            in = new ByteArrayInputStream(fdCameraData.mJpgData);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpeg");
            ImageReader reader =  readers.next();
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(in);
            reader.setInput(imageInputStream, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x1, y1, width, height);
            param.setSourceRegion(rect);
            BufferedImage imageHandle = reader.read(0, param);
            //=====画框用的==============================
           /* BufferedImage image = ImageIO.read(in);
            Graphics g = image.getGraphics();
            g.setColor(Color.RED);//画笔颜色

            g.drawRect(x1, y1, width, height);*/
            //g.dispose();
            //=====画框用的==============================
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            ImageIO.write(imageHandle,"jpeg", out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in!=null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }
    public FaceDefine[] imageDetect(byte[] image){
        logger.info("===========detect============");
        HttpResponse response = null;
        int i = 0;
        FaceDefine[] faceDefines = null;
        try {
            response = Request.Post(Constant.SDK_IP+"/v0/detect")
                    .connectTimeout(10000)
                    .socketTimeout(30000)
                    .addHeader("Authorization", "Token " + Constant.TOKEN)
                    .body(MultipartEntityBuilder
                            .create()
                            //.addTextBody("mf_selector", "all")
                            .addBinaryBody("photo", image, ContentType.create("image/jpeg"), "photo.jpg")
                            .build())
                    .execute().returnResponse();
            String reply = EntityUtils.toString(response.getEntity());
            logger.info(reply);
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode!=200){
                logger.info("请求未正确响应："+responseCode);
                logger.info(reply);
                return null;
            }
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reply);
            if(jsonObject.containsKey("faces")){
                JSONArray faceArray = (JSONArray) jsonObject.get("faces");
                int faceNum = faceArray.size();
                if(faceArray.size()==0){
                    logger.warn("图片中没有人脸！");
                    return null;
                }
                faceDefines = new FaceDefine[faceNum];
                Iterator iterator1 = faceArray.iterator();
                while (iterator1.hasNext()){
                    JSONObject face = (JSONObject)(iterator1.next());
                    FaceDefine faceDefine = new FaceDefine();

                    faceDefine.left = (double)(long)face.get("x1");
                    faceDefine.right = (double)(long)face.get("x2");
                    faceDefine.top = (double)(long)face.get("y1");
                    faceDefine.bottom = (double)(long)face.get("y2");
                    faceDefines[i] = faceDefine;
                    i++;
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return faceDefines;
    }
    public List<IdentifyFace> imageIdentify(FaceDefine faceDefine, byte[] data){
        logger.info("===========identify============");
        List<IdentifyFace> identifyFaceList = null;
        HttpResponse response;
        HttpEntity httpEntity;
        StringBuilder bbox;
        if(faceDefine != null) {
            bbox = new StringBuilder("[[");
            bbox.append(faceDefine.left).append(",").append(faceDefine.top).append(",").append(faceDefine.right).append(",").append(faceDefine.bottom);
            bbox.append("]]");
            httpEntity = MultipartEntityBuilder
                    .create()
                    .addTextBody("bbox",bbox.toString())
                    .addTextBody("threshold",Constant.IDENTIFY_THRESHOLD)
                    .addBinaryBody("photo", data, ContentType.create("image/jpeg"), "photo.jpg")
                    .build();
        }else {
            httpEntity = MultipartEntityBuilder
                    .create()
                    .addTextBody("mf_selector","all")
                    .addTextBody("threshold",Constant.IDENTIFY_THRESHOLD)
                    .addBinaryBody("photo", data, ContentType.create("image/jpeg"), "photo.jpg")
                    .build();
        }
        try {
            response = Request.Post(Constant.SDK_IP + "/v0/identify")
                    .connectTimeout(10000)
                    .socketTimeout(30000)
                    .addHeader("Authorization", "Token " + Constant.TOKEN)
                    .body(httpEntity)
                    .execute().returnResponse();
            String reply = EntityUtils.toString(response.getEntity());
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode!=200){
                logger.warn("请求未正确响应："+responseCode);
                logger.warn(reply);
                return null;
            }
            logger.info("SDK-identify:"+reply);
            JSONObject root = (JSONObject) jsonParser.parse(reply);
            if(root.containsKey("results")){
                JSONObject results = (JSONObject) root.get("results");
                Iterator iterator = results.keySet().iterator();
                while (iterator.hasNext()){
                    String faceCoordinates = (String)iterator.next();
                    JSONArray jsonArray = (JSONArray) results.get(faceCoordinates);
                    if(jsonArray.size() == 0)
                        continue;
                    if(identifyFaceList == null)
                        identifyFaceList = new ArrayList<>();
                    JSONObject match = (JSONObject) jsonArray.get(0);
                    IdentifyFace identifyFace = new IdentifyFace();
                    identifyFace.setFaceCoordinates(faceCoordinates);
                    identifyFace.setConfidence((double)match.get("confidence"));
                    JSONObject face = (JSONObject) match.get("face");
                    identifyFace.setFriendOrFoe((boolean)face.get("friend"));
                    identifyFace.setId((long)face.get("id"));
                    identifyFace.setMeta((String)face.get("meta"));
                    identifyFace.setPersonId((long)face.get("person_id"));
                    identifyFace.setTimestamp((String)face.get("timestamp"));
                    identifyFace.setX1((int)(long)face.get("x1"));
                    identifyFace.setX1((int)(long)face.get("x2"));
                    identifyFace.setX1((int)(long)face.get("y1"));
                    identifyFace.setX1((int)(long)face.get("y2"));
                    JSONArray galleries = (JSONArray) face.get("galleries");
                    identifyFace.setGalleries(galleries.toJSONString());
                    String photoUrl = (String) face.get("normalized");
                    byte[] pic = FindFaceUtil.getPicByURL(photoUrl);
                    identifyFace.setNormalizedPhoto(pic);
                    if(pic==null) {
                        logger.warn("ERROR：identifyURL获取图片为null！！！");
                    }else {
                        logger.info("成功识别"+identifyFace.getMeta()+"："+pic.length);
                    }
                    identifyFaceList.add(identifyFace);
                }
                return identifyFaceList;

            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public boolean enrollFace(Map<String,String[]> params,byte[] pic){
        logger.info("===========enrollFace============");
        HttpResponse response;
        HttpEntity entity;
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addBinaryBody("photo",pic,ContentType.DEFAULT_BINARY, "photo");
        if(params.containsKey("meta"))
            multipartEntityBuilder.addTextBody("meta",params.get("meta")[0]);
        if(params.containsKey("galleries"))
            multipartEntityBuilder.addTextBody("galleries",params.get("galleries")[0]);
        if(params.containsKey("mf_selector"))
            multipartEntityBuilder.addTextBody("mf_selector",params.get("mf_selector")[0]);
        if(params.containsKey("bbox"))
            multipartEntityBuilder.addTextBody("bbox",params.get("bbox")[0]);
        if(params.containsKey("cam_id"))
            multipartEntityBuilder.addTextBody("cam_id",params.get("cam_id")[0]);
        entity = multipartEntityBuilder.build();
        try {
            response = Request.Post(Constant.SDK_IP + "/v0/face")
                    .connectTimeout(10000)
                    .socketTimeout(30000)
                    .addHeader("Authorization", "Token " + Constant.TOKEN)
                    .body(entity)
                    .execute().returnResponse();
            String reply = EntityUtils.toString(response.getEntity());

            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode == 200){
                logger.info(reply);
                return true;
            }
            logger.info("请求未正确响应：" + responseCode);
            logger.info(reply);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
