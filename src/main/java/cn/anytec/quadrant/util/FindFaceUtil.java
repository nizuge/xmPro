package cn.anytec.quadrant.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FindFaceUtil {

    private static final Logger logger = LoggerFactory.getLogger(FindFaceUtil.class);
    private static HttpUtil httpUtil = new HttpUtil();

    public static byte[] getPicByURL(String url){
       /* HttpResponse response = null;
        byte[] pic = null;
        try {
            response = Request.Post(url)
                    .connectTimeout(6000)
                    .socketTimeout(30000)
                    .execute().returnResponse();
            pic = EntityUtils.toByteArray(response.getEntity());
            logger.debug("identify_pic:"+pic.length);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return pic;*/
        byte[] reply = null;
        try {
            reply = httpUtil.getBinaryData(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply;
    }
}

class HttpUtil {
    //response code

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HttpUtil.class);

	/*public static void main(String[] args) {
		String s = "http://192.168.0.132:3333/uploads//20171227/15143371668494108_norm.png";
		try {
			byte[] c = new HttpUtil().getBinaryData(s);
			System.out.println(c.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

    public byte[] getBinaryData(String httpURL) throws IOException {

        try {
            URL url = new URL(httpURL);
            logger.debug("URL :" + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(50000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            if (status != 200) {
                logger.warn("请求二进制数据响应失败");
                return null;
            }
            byte[] binany = streamToByte(connection.getInputStream());
            logger.debug("binary_length:" + binany.length);
            connection.disconnect();
            return binany;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static byte[] streamToByte(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c = 0;
        byte[] buffer = new byte[8 * 1024];
        try {
            while ((c = is.read(buffer)) != -1) {
                baos.write(buffer, 0, c);
                baos.flush();
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
