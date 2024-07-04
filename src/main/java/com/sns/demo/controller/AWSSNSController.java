package com.sns.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sns.demo.api.model.SMSRequestVO;
import com.sns.demo.service.AWSSNSService;

@RestController
public class AWSSNSController {

    @Autowired
    AWSSNSService awssnsService;

    @PostMapping("subscribe/{mobile}")
    public String sendSMS(@PathVariable(value = "mobile") String mobile) {
        String response = awssnsService.subscribeMobile(mobile);
        return String.format("SNS Response: %s", response);
    }

    @PostMapping("customer-sms")
    public String sendCustomerSMS(@RequestBody SMSRequestVO request) {
        String response = awssnsService.sendMessageToCustomer(request.getMessage(), request.getMobile());
        return String.format("SNS Response: %s", response);
    }

}
