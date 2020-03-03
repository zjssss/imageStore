package com.example.ffaid.faceapi;

import com.example.ffaid.util.*;

import java.net.URLEncoder;

/**
 * @author DIX
 * @date 2020/3/3 11:09
 * 人脸识别
 */
public class FaceIdentify {

    public static String identify() {

        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";

        String groupId="u_g_1";

        //检测上限人数，默认为一
        String userTopNum = "1";
        //检测分数，默认为一
        String faceTopNum = "1";
        String faceImage = "C:\\Users\\Administrator\\Desktop\\下\\ImageData\\zjs1.jpg";
        try {
            byte[] imgData = FileUtil.readFileByBytes(faceImage);
            String imgStr1 = Base64Util.encode(imgData);

            //参数参考api文档
            String param="user_top_num="+userTopNum+"&image_type=BASE64"+"&group_id_list="+groupId+"&"+ URLEncoder.encode("image","UTF-8")+"="+URLEncoder.encode(imgStr1,"UTF-8");

            String accessToken = "24.ac0ddcae950627222581b9d9171be64c.2592000.1585790230.282335-18646513";

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        FaceIdentify.identify();
    }
}
