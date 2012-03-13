package com.aimprosoft.i18n.hook.controller;

import com.aimprosoft.i18n.common.persistence.MessageSourcePersistence;
import com.aimprosoft.i18n.common.service.LocaleService;
import com.aimprosoft.i18n.common.service.MessageSourceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

@Controller
@RequestMapping("VIEW")
public class MainController extends BaseController {

    private final Logger _log = Logger.getLogger(getClass());

    @Autowired
    private MessageSourcePersistence messageSourcePersistence;

    @Autowired
    private MessageSourceService messageSourceService;

    @Autowired
    private LocaleService localeService;

    @RequestMapping
    public ModelAndView view(PortletRequest portletRequest) {
        return new ModelAndView("/view/view");
    }

    @ResourceMapping("resourceContent")
    public void resourceContent(ResourceRequest request, ResourceResponse response) {
        writeResponse(response, messageSourceService.getMessageSourceWrappers(0, 10));
    }

    @ResourceMapping("uploadResources")
    public void uploadResources(@RequestParam("data") String data, ResourceRequest request, ResourceResponse response) {
        messageSourceService.saveMessageSources(data, false);
    }

    @ResourceMapping("delete")
    public void delete(@RequestParam("data") String data) {
        messageSourceService.delete(data);
    }

    @RequestMapping(params = "render=edit")
    public ModelAndView edit(){
        ModelAndView mov = new ModelAndView("/view/edit");
        mov.addObject("locales", localeService.getSortedLocales());
        return mov;
    }
    
    @RequestMapping(params = "render=save")
    public String save(@RequestParam("data") String data){
        messageSourceService.saveMessageSources(data, true);
        return "/view/view";
    }
}
