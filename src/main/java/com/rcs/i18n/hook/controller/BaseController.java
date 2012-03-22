package com.rcs.i18n.hook.controller;

import org.apache.log4j.Logger;

import javax.portlet.MimeResponse;

public abstract class BaseController {
    
    protected static final String DEFAULT_SUCCESS_RESULT = "{\"success\":true}";

    protected static final String DEFAULT_ERROR_RESULT = "{\"success\":false}";

    protected Logger _logger = Logger.getLogger(getClass());

    protected void writeResponse(MimeResponse mimeResponse, String response){
        try{
            mimeResponse.setContentType("text/html");
            mimeResponse.getWriter().write(response);
        } catch (Exception e){
            _logger.error("Error while response, cause - " + e.getMessage(), e);
        }
    }

}
