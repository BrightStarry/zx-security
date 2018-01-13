package com.zx.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 09:50
 */
@Controller
@Slf4j
public class TestController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }



    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("msg", "登录失败");
        return "login";
    }

    @ResponseBody
    @RequestMapping("/a")
    public String a(){
        return "a";
    }
    @ResponseBody
    @RequestMapping("/b")
    public String b(){
        return "b";
    }
    @ResponseBody
    @RequestMapping("/c")
    public String c(){
        return "c";
    }
    @ResponseBody
    @RequestMapping("/d")
    public String d(){
        return "d";
    }
    @ResponseBody
    @RequestMapping("/e")
    public String e(){
        return "e";
    }
    @ResponseBody
    @RequestMapping("/f")
    public String f(){
        return "f";
    }
    @ResponseBody
    @RequestMapping("/aaaaa/dddd/ccc")
    public String g(){
        return "g";
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}
