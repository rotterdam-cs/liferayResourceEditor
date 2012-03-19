package com.aimprosoft.i18n.hook.controller;

import com.aimprosoft.i18n.common.message.CustomMessage;
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
import java.io.IOException;
import java.util.List;

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

    /**
     * Sometimes Liferay for enter to ACTION phase of portlet in Control Panel
     * That's why it is needed some mock handler for ACTION phase of portlet
     */
    @ActionMapping
    public void action(){
    }

    @RequestMapping
    public ModelAndView view(PortletRequest portletRequest) {
        return new ModelAndView("/view/view");
    }

    @ResourceMapping("resourceContent")
    public void resourceContent(@RequestParam("startIndex") Integer startIndex,@RequestParam("pageSize") Integer pageSize,
                                ResourceRequest request, ResourceResponse response) {
        writeResponse(response, messageSourceService.getMSWJson(startIndex, startIndex + pageSize));
    }

    @ResourceMapping("uploadResources")
    public void uploadResources(@RequestParam("data") String data, ResourceResponse response) {
        List<CustomMessage> customMessages = messageSourceService.saveMessageSources(data, false);
        writeResponse(response, messageSourceService.getCMJson(customMessages));
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

    @ResourceMapping("save")
    public void save(@RequestParam("data") String data){
        messageSourceService.saveMessageSources(data, true);
    }
}
