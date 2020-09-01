package com.anton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index(HttpServletRequest request) {
//        request.getSession().setAttribute("user","null");
        return "index";
    }

}
