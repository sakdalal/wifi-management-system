package com.sak.wifi.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    // After you have applied JWT filters to check whether the api are working accordingly;

    @GetMapping
    public String test(){
        return "Protected ApI working";
    }

    @PostMapping
    public String test(@RequestBody String body) {
        System.out.println(body);
        return "OK";
    }

    @GetMapping("/me")
    public String me() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getName();
    }

    @GetMapping("/roles")
    public Object roles() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getAuthorities();
    }


}
