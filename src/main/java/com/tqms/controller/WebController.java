package com.tqms.controller;

import com.tqms.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final QueueService queueService;

    @Autowired
    public WebController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("summary", queueService.getSummary());
        model.addAttribute("records", queueService.getAllRecords());
        return "index";
    }
}
