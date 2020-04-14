package com.example.ffaid.controller;

import com.example.ffaid.domain.*;
import com.example.ffaid.faceapi.FaceAdd;
import com.example.ffaid.faceapi.FaceIdentify;
import com.example.ffaid.service.AidDataService;
import com.example.ffaid.service.UrgentTelService;
import com.example.ffaid.speechapi.AsrMain;
import com.example.ffaid.speechapi.common.DemoException;
import com.example.ffaid.util.FileUpLoadUtil;
import com.example.ffaid.util.GsonUtils;
import com.example.ffaid.util.IdUtil;
import com.example.ffaid.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.EAN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.ffaid.util.BaiduAuth;
import sun.security.krb5.internal.crypto.Des;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


/**
 * @author DIX
 * @date 2019/11/29 20:01
 */
@Api(value="用户controller",tags={"用户操作接口"})
@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UrgentTelService urgentTelService;

    public static String UPLOAD_PATH="/root/imag/picStore";

    public static String CHECK_PATH="/root/imag/picCheck";




    private String handleUploadPicture(MultipartFile file) {
        String ranName=IdUtil.getValue(8)
                + file.getOriginalFilename();
        String path = "C:\\Users\\Administrator\\Desktop\\下\\ImageData\\"
//        String path = "/root/imag/picStore"
                + ranName;


        String ok = "Success";
        if (ok.equals(FileUpLoadUtil.upload(file, path))) {
            return ranName;
        }
        return null;
    }

    private String handleUploadPictureCheck(MultipartFile file) {
        String ranName=IdUtil.getValue(8)
                + file.getOriginalFilename();
        String path = "C:\\Users\\Administrator\\Desktop\\下\\ImageData\\check\\"
//        String path = "/root/imag/picCheck"
                + ranName;


        String ok = "Success";
        if (ok.equals(FileUpLoadUtil.upload(file, path))) {
            return path;
        }
        return null;
    }



    /**
     * 登录
     */
    @GetMapping("/login")
    @ApiOperation(value="用户登录",tags={""},notes="用户名密码不为空")
    public User userLogin(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.userLogin(username, password);
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value="获得单个用户信息",tags={""},notes="参数id在路径中带上即可")
    public User getUser(@PathVariable("id") Integer id) {
//        String auth=BaiduAuth.getAuth();
//        System.out.print(auth);
        return userService.getUser(id);
    }


    @PostMapping("")
    @ApiOperation(value="用户注册",tags={""},notes="user 为整个对象，还要传入一个图像file")
    public Object userRegister(MultipartFile file, User user) {

        System.out.println(file.isEmpty());
        String ranName=handleUploadPicture(file);
        user.setPic(ranName);
        userService.userRegister(user);
        User user1 =userService.getUserByTel(user.getTel());
        FaceAdd.add(user1.getId(),ranName);
        return "success";
    }

    @PostMapping("/image")
    @ApiOperation(value="上传文件",tags={""},notes="单独的测试接口，返回文件在服务器中的路径")
    public Object upload(MultipartFile file) {

        System.out.println(file.isEmpty());
        String url=handleUploadPicture(file);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/identify")
    @ApiOperation(value="用户人脸认证",tags={""},notes="如果用户识别成功，返回这个人的紧急联系人电话列表，不存在这个人返回失败")
        public Object searchFace(MultipartFile file) {

        TelResult telResult=new TelResult();
            String picPath=handleUploadPictureCheck(file);
//        System.out.println(picPath);
            Result result=FaceIdentify.identify(picPath);

            int userId=result.getUserId();
            if(result.getScore()>80.0)
            {
                List<UrgentTel> tels=urgentTelService.findUserTel(userId);
                if(tels.size()>=1) {
                    telResult.setTel1(tels.get(0).getUrgentTel());
                }
                if(tels.size()>=2){
                    telResult.setTel2(tels.get(1).getUrgentTel());
                }
                if(tels.size()>=3){
                    telResult.setTel3(tels.get(2).getUrgentTel());
                }
                return telResult;
            }
            else {
                return "not found";
            }
    }

    @PutMapping("/{id}")
    @ApiOperation(value="修改用户信息",tags={""},notes="")
    public Object update(@PathVariable Integer id, User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.update(user));
    }


//    @PutMapping("/{id}")
//    public Object update(@PathVariable Integer id,@RequestBody User user)
//    {
//        return ResponseEntity.ok(userService.update(user));
//    }
//    }
//


    @ApiOperation(value="删除用户",tags={""},notes="用户名密码不为空")
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(userService.logicalDelete(id));
    }

    /**
     * 语音转文字接口
     */
    @PostMapping("/voice")
    @ApiOperation(value="语音转文字接口",tags={""},notes="")
    public Object speechIdentify(MultipartFile file) throws IOException, DemoException {
        String speechPath=handleUploadPictureCheck(file);
        AsrMain asrMain=new AsrMain();
        String result=asrMain.runFile(speechPath);
        int loc1=result.indexOf("result");
        int loc2=result.indexOf("\"",loc1+10);
        String result1=result.substring(loc1+10,loc2);
        return result1;
    }

    /**
     * 文字获得结果接口
     */
    @GetMapping("/vWord")
    @ApiOperation(value="文字获得结果接口",tags={""},notes="")
    public Object WordIdentify(@RequestParam("word") String word) throws IOException, DemoException {
        String result="";
//        String[] arguments = new String[] {"python", "C:\\Users\\Administrator\\Desktop\\下\\OPPO\\v2\\confidenceBasedModel2.py",word};
        String[] arguments = new String[] {"python3", "/root/py/confidenceBasedModel2.py",word};
        try {
            Process process = Runtime.getRuntime().exec(arguments);
//            BufferedReader in  = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            BufferedReader in  = new BufferedReader(new InputStreamReader(process.getInputStream(),"utf-8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                result=result+line;
            }
            in.close();
            int re = process.waitFor();
            System.out.println(re);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Description resultObj= GsonUtils.fromJson(result,Description.class);
        String[] s=new String[9];
        Output output=new Output();
        for(int i=0;i<3;i++)
        {
            if(resultObj.getDisease().size()>i)
            {
                s[i]=resultObj.getDisease().get(i).getName();
            }

        }
        for(int i=3;i<9;i++)
        {
            if(resultObj.getSymptoms().get(i-3)!=null)
            {
                s[i]=resultObj.getSymptoms().get(i-3);
            }

        }
        output.setAid1(s[0]);
        output.setAid2(s[1]);
        output.setAid3(s[2]);
        output.setS1(s[3]);
        output.setS2(s[4]);
        output.setS3(s[5]);
        output.setS4(s[6]);
        output.setS5(s[7]);
        output.setS6(s[8]);
        return output;
    }

    /**
     * 语音直接获得结果接口
     */
    @PostMapping("/voice/doc")
    @ApiOperation(value="语音直接获得结果接口",tags={""},notes="")
    public Object speechIdentifyDoc(MultipartFile file) throws IOException, DemoException {
        String speechPath=handleUploadPictureCheck(file);
        AsrMain asrMain=new AsrMain();
        String result=asrMain.runFile(speechPath);
        String result2="";
        int loc1=result.indexOf("result");
        int loc2=result.indexOf("\"",loc1+10);
        String result1=result.substring(loc1+10,loc2);
//        String[] arguments = new String[] {"python", "C:\\Users\\Administrator\\Desktop\\下\\OPPO\\confidenceBasedModel.py",result1};
        String[] arguments = new String[] {"python3", "/root/py/confidenceBasedModel2.py",result1};
        try {
            Process process = Runtime.getRuntime().exec(arguments);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),"utf-8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                result2=result2+line;
            }
            in.close();
            //java代码中的process.waitFor()返回值为0表示我们调用python脚本成功，
            //返回值为1表示调用python脚本失败，这和我们通常意义上见到的0与1定义正好相反
            int re1 = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result2;
    }


    public static void main(String[] args) {
        String result="呃，这个人，他口吐白沫";
        String[] arguments = new String[] {"python","C:\\Users\\Administrator\\Desktop\\下\\OPPO\\v2\\confidenceBasedModel2.py",result};
        String result1="";
        try {

            Process process = Runtime.getRuntime().exec(arguments);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                result1=result1+line;
            }
            in.close();

            //java代码中的process.waitFor()返回值为0表示我们调用python脚本成功，
            //返回值为1表示调用python脚本失败，这和我们通常意义上见到的0与1定义正好相反
            int re = process.waitFor();
            System.out.println(re);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String result1="{\"disease\": [], \"symtoms\": \"[\"1\",\"2\"]\"}";
//        result1=result1.substring(1,result1.length()-1);
        Description result3= GsonUtils.fromJson(result1,Description.class);
        System.out.println(result3.getSymptoms().get(1));
        System.out.println(result3.getDisease().get(1).getName());
        System.out.println(result3.getDisease().get(1).getRelatedDisease());

    }
}
