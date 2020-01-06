package com.bailiban.controller;

import com.bailiban.controller.model.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/helloword")
public class HelloController {

    @RequestMapping("/hello")
    public User HelloWorld(@RequestBody User user)
    {
        System.out.println(user);
        return user;
    }

    @RequestMapping("/hi")
    public String Hi(String msg)
    {
        System.out.println(msg);
        return msg;
    }
}
