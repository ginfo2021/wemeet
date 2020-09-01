package com.wemeet.dating.api;

import com.wemeet.dating.exception.ResourceFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Profile("production")
@RestController
public class BaseController {

    @RequestMapping(value = "swagger-ui.html", method = RequestMethod.GET)
    public void getSwagger(HttpServletResponse httpResponse) throws ResourceFoundException {
        throw new ResourceFoundException("Please enter a valid path");
    }
}