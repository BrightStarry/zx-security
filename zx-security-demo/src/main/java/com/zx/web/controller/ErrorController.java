package com.zx.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * author:ZhengXing
 * datetime:2017-11-26 17:32
 * 异常controller
 */
@Controller
@RequestMapping("/error/custom")
public class ErrorController {



    @RequestMapping
    @ResponseBody
    public String error(HttpServletRequest request) {
        return (String)request.getAttribute("error");
    }

    @RequestMapping(
            produces = {"text/html"}
    )
    public String errorView(HttpServletRequest request) {
        return "error/404";
    }
}
