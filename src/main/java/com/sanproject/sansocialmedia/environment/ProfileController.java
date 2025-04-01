package com.sanproject.sansocialmedia.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @Value("${cloud.aws.s3.prefix:}")
    private String profile;

    @GetMapping("/api/profile")
    public String getProfile() {
        return profile;
    }

}
