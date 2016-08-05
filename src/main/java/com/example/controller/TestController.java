package com.example.controller;


import com.example.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/aws/s3")
public class TestController {

    @Autowired
    BusinessService businessService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity list() throws IOException {

        List ss = businessService.getReconciliationFiles();

        businessService.readReconciliationFiles(ss);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
