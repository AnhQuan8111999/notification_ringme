package com.ringme.camid.Notification.controller;

import com.ringme.camid.Notification.service.NofiticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/camid/")
public class NotificationController {
    @Autowired
    private NofiticationService nofiticationService;

    @GetMapping("/test")
    public ResponseEntity Test() {
        return ResponseEntity.ok().body("OK");
    }
}
