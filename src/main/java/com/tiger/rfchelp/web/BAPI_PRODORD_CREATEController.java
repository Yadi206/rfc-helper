package com.tiger.rfchelp.web;

import com.sap.conn.jco.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BAPI_PRODORD_CREATEController {

    @RequestMapping("/BAPI_PRODORD_CREATE")
    @ResponseBody
    public String BAPI_PRODORD_CREATE(@RequestBody String sPara) throws JCoException {
        String rJson = "";
        JCoDestination destination = JCoDestinationManager.getDestination("SAP_LOGIN_CONFIG");
        JCoFunction function = destination.getRepository().getFunction("BAPI_PRODORD_CREATE");
        if (sPara != "" && sPara != null)
        {
            function.getImportParameterList().fromJSON(sPara);
        }
        try {
            function.execute(destination);
            rJson = function.getExportParameterList().toJSON();
        } catch (AbapException e) {
            return e.getMessage();
        }
        return rJson;
    }
}
