package com.wemeet.dating.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    Logger logger = LoggerFactory.getLogger(WebController.class);

    @RequestMapping(value = "/")
    public String index() {
        logger.info("redirect to => swagger-ui.html");
        return "redirect:swagger-ui.html";
    }
}
