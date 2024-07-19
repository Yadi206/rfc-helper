package com.tiger.rfchelp.web;

import com.sap.conn.jco.*;
import com.sap.conn.jco.rt.DefaultListMetaData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class RFCHelpController {

    @RequestMapping("/initialConfig")
    @ResponseBody
    public String initialConfig(@RequestBody SAPLoginPara sapPara) {
        return SapConnect.initialConfig(sapPara);
    }

    @RequestMapping("/printFunInfo")
    @ResponseBody
    public String printFunInfo(@RequestBody SAPLoginPara sapPara) throws JCoException {
        String rJson = "";
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara); 
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);
        rJson = "ImportParameter" + "\r\n";
        rJson = rJson + function.getImportParameterList().getListMetaData().toString()+ "\r\n";
        rJson = rJson + "ExportParameter" + "\r\n";
        rJson = rJson + function.getExportParameterList().getListMetaData().toString()+ "\r\n";
        for (int i = 0; i < function.getTableParameterList().getFieldCount(); i++) {
            JCoTable table = function.getTableParameterList().getTable(((DefaultListMetaData) function.getTableParameterList().getListMetaData()).getName(i));
            table.appendRow();
        }
        rJson = rJson + "ChangingParameter" + "\r\n";
        rJson = rJson + function.getChangingParameterList().getListMetaData().toString()+ "\r\n";
        rJson = rJson + "TableParameter" + "\r\n";
        rJson = rJson + function.getTableParameterList().getListMetaData().toString()+ "\r\n";
        return rJson;
    }

    @RequestMapping("/getFunInfo")
    @ResponseBody
    public SAPReturn getFunInfo(@RequestBody SAPLoginPara sapPara) throws JCoException {
        SAPReturn rSap = new SAPReturn(sapPara);
        String rJson = "";
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara);
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);

        rSap.InputJson = function.getImportParameterList().toJSON();
        rSap.OutputJson = function.getExportParameterList().toJSON();
        for (int i = 0; i < function.getTableParameterList().getFieldCount(); i++) {
            JCoTable table = function.getTableParameterList().getTable(((DefaultListMetaData) function.getTableParameterList().getListMetaData()).getName(i));
            table.appendRow();
        }
        rSap.TableJson = function.getTableParameterList().toJSON();
        rSap.ChangeJson = function.getChangingParameterList().toJSON();
        return rSap;
    }

    @RequestMapping("/getInputJson")
    @ResponseBody
    public String getInputJson(@RequestBody SAPLoginPara sapPara) throws JCoException {
        String rJson = "";
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara);
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);
        rJson = function.getImportParameterList().toJSON();
        return rJson;
    }

    @RequestMapping("/getTableJson")
    @ResponseBody
    public String getTableJson(@RequestBody SAPLoginPara sapPara) throws JCoException {
        String rJson = "";
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara);
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);
        for (int i = 0; i < function.getTableParameterList().getFieldCount(); i++) {
            JCoTable table = function.getTableParameterList().getTable(((DefaultListMetaData) function.getTableParameterList().getListMetaData()).getName(i));
            table.appendRow();
        }
        rJson = function.getTableParameterList().toJSON();
        return rJson;
    }

    @RequestMapping("/getOutputJson")
    @ResponseBody
    public String getOutputJson(@RequestBody SAPLoginPara sapPara) throws JCoException {
        String rJson = "";
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara);
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);
        rJson = function.getExportParameterList().toJSON();
        return rJson;
    }

    @RequestMapping("/getChangeJson")
    @ResponseBody
    public String getChangeJson(@RequestBody SAPLoginPara sapPara) throws JCoException {
        String rJson = "";
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara);
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);
        rJson = function.getChangingParameterList().toJSON();
        return rJson;
    }

    @RequestMapping("/runrfc")
    @ResponseBody
    public SAPReturn postRunRfc(@RequestBody SAPLoginPara sapPara) throws JCoException {
        SAPReturn rSap = new SAPReturn(sapPara);
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara);
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);
        if (sapPara.OutputJson != "" && sapPara.OutputJson != null) {
            function.getExportParameterList().fromJSON(sapPara.OutputJson);
        }
        if (sapPara.InputJson != "" && sapPara.InputJson != null)  {
            function.getImportParameterList().fromJSON(sapPara.InputJson);
        }
        if (sapPara.ChangeJson != "" && sapPara.ChangeJson != null) {
            function.getChangingParameterList().fromJSON(sapPara.ChangeJson);
        }
        if (sapPara.TableJson != "" && sapPara.TableJson != null) {
            function.getTableParameterList().fromJSON(sapPara.TableJson);
        }
        try {
            function.execute(destination);
            if (function.getImportParameterList() != null)
            {
                rSap.InputJson = function.getImportParameterList().toJSON();
            }
            if (function.getExportParameterList() != null)
            {
                rSap.OutputJson = function.getExportParameterList().toJSON();
            }
            if (function.getChangingParameterList() != null)
            {
                rSap.ChangeJson = function.getChangingParameterList().toJSON();
            }
            if (function.getTableParameterList() != null)
            {
                rSap.TableJson = function.getTableParameterList().toJSON();
            }
            rSap.rType = "S";
            rSap.rMessage = "成功";
        } catch (AbapException e) {
            rSap.rType = "E";
            rSap.rMessage = e.getMessage();
            return rSap;
        }
        return rSap;
    }
}
