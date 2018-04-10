/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.bootstrap;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bootstrap")
public class IdracController
{
    // INCOMPLETE JSP  it is complex with jquery/jstl/ajax etc - but allows 
    // separation of the controller
    @RequestMapping(value={"/scan", "/scan/{jobid}"})
    public String getJobs(@PathVariable Map<String, String> pathVariables, Map<String, Object> model, HttpServletRequest request)
    {
        if (pathVariables.containsKey("jobid"))
        {
            model.put("jobid", pathVariables.get("jobid"));
        }
        return "IdracScanner";
    }

}
