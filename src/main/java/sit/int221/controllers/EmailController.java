package sit.int221.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.int221.services.EmailService;

@RestController
@RequestMapping("/sendEmail")
public class EmailController {
    @Autowired
    private EmailService emailservice;

    @GetMapping("")
    public String sendEmail(){
        emailservice.sendEmail("itbkkttone@gmail.com", "Poom", "READ", "TEST", "1235");
        return "success";
    }

}
