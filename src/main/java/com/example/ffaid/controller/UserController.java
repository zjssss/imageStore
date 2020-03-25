package com.example.ffaid.controller;

import com.example.ffaid.domain.Result;
import com.example.ffaid.domain.UrgentTel;
import com.example.ffaid.faceapi.FaceAdd;
import com.example.ffaid.faceapi.FaceIdentify;
import com.example.ffaid.service.AidDataService;
import com.example.ffaid.service.UrgentTelService;
import com.example.ffaid.util.FileUpLoadUtil;
import com.example.ffaid.util.IdUtil;
import com.example.ffaid.domain.User;
import com.example.ffaid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.ffaid.util.BaiduAuth;


/**
 * @author DIX
 * @date 2019/11/29 20:01
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UrgentTelService urgentTelService;


    private String handleUploadPicture(MultipartFile file) {
        String ranName=IdUtil.getValue(8)
                + file.getOriginalFilename();
        String path = "C:\\Users\\Administrator\\Desktop\\下\\ImageData\\"
//        String path = "/root/imag/"
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
//        String path = "/root/imag/"
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
    public User userLogin(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.userLogin(username, password);
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Integer id) {
//        String auth=BaiduAuth.getAuth();
//        System.out.print(auth);
        return userService.getUser(id);
    }


    @PostMapping("")
    public Object userRegister(MultipartFile file, User user) {

        System.out.println(file.isEmpty());
        String ranName=handleUploadPicture(file);
        user.setPic(ranName);
        userService.userRegister(user);
        User user1 =userService.getUserByTel(user.getTel());
        FaceAdd.add(user1.getId(),ranName);
        return ResponseEntity.ok();
    }

    @PostMapping("/image")
    public Object upload(MultipartFile file) {

        System.out.println(file.isEmpty());
        String url=handleUploadPicture(file);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/identify")
    public Object searchFace(MultipartFile file) {

        String picPath=handleUploadPictureCheck(file);
//        System.out.println(picPath);
        Result result=FaceIdentify.identify(picPath);

        int userId=result.getUserId();
        if(result.getScore()>90.0)
        {
            return urgentTelService.findUserTel(userId);
        }
        else {
            return "not found";
        }
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable Integer id, User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.update(user));
    }


//    @PutMapping("/{id}")
//    public Object update(@PathVariable Integer id,@RequestBody User user)
//    {
//        return ResponseEntity.ok(userService.update(user));
//    }
//


    @DeleteMapping("/{id}")
    public Object delete(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(userService.logicalDelete(id));
    }


}
