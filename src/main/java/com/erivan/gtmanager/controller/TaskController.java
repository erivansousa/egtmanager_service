package com.erivan.gtmanager.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final Logger logger = LogManager.getLogger();

    @PostMapping("/create")
    public void createTask(){
        logger.info("create task endpoint call");
    }
}
