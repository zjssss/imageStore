package com.example.ffaid.controller;

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

    private BaiduAuth baiduAuth;

    private String handleUploadPicture(MultipartFile file) {
        String path = "/ffaid/files/"
                + IdUtil.getValue(8)
                + file.getOriginalFilename();
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

//    @PostMapping("")
//    public Object userRegister(@RequestParam("username")String username, @RequestParam("password")String password, @RequestParam("sex") Integer sex, @RequestParam("tel")String tel, @RequestParam("pic")String pic, @RequestParam("birthday") Date birtyday,@RequestParam("status")Integer status)
//    {
//        User user=new User();
//        user.setUsername(username);
//        user.setPassword(password);
//        user.setSex(sex);
//        user.setPic(pic);
//        user.setTel(tel);
//        user.setStatus(status);
//        user.setBirthday(birtyday);
//
//        return ResponseEntity.ok(userService.userRegister(user));
//    }

    @PostMapping("")
    public Object userRegister(MultipartFile file, User user) {

        System.out.println(file.isEmpty());
        String url=handleUploadPicture(file);
        user.setPic(url);
        return ResponseEntity.ok(userService.userRegister(user));
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
