package com.example.api.schedule;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/schedule")
public class ScheduleManageController {


    @GetMapping("/test")
    public String test() {
        return "hi";
    }
}
