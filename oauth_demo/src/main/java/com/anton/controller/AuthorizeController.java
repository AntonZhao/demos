package com.anton.controller;

import com.anton.dto.AccessTokenDTO;
import com.anton.dto.GithubUser;
import com.anton.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @GetMapping("callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO()
                .setClient_id("885000f93a718e3d6ec0")
                .setClient_secret("cb3e65542597c21799958b2531dfe3ee79a25f1a")
                .setCode(code)
                .setRedirect_uri("http://localhost:8081/callback")
                .setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(accessToken);

        System.out.println(user);
        request.getSession().setAttribute("user", user.getName());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        request.getSession().removeAttribute("user");

        return "redirect:/";
    }
}
