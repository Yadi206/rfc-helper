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
        int i = 0;
        rJson += "ImportParameter" + "\r\n";
        if (function.getImportParameterList() != null) {
            for (i = 0; i < function.getImportParameterList().getFieldCount(); i++) {
                JCoParameterList jpl = function.getImportParameterList();
                DefaultListMetaData dlmd = (DefaultListMetaData) jpl.getListMetaData();
                String name = dlmd.getName(i);
                String Type = dlmd.getTypeAsString(i);
                int Typei = dlmd.getType(i);
                if (Typei == 17) {
                    JCoStructure structure = function.getImportParameterList().getStructure(((DefaultListMetaData) function.getImportParameterList().getListMetaData()).getName(i));
                    SapCommon.setDefaultValue(structure);
                }
                if (Typei == 99) {
                    JCoTable table = function.getImportParameterList().getTable(((DefaultListMetaData) function.getImportParameterList().getListMetaData()).getName(i));
                    SapCommon.setDefaultValue(table);
                }
                String aa = "";
            }
            rJson += function.getImportParameterList().toJSON() + "\r\n";
        }
        rJson += "ExportParameter" + "\r\n";
        if (function.getExportParameterList() != null) {
            for (i = 0; i < function.getExportParameterList().getFieldCount(); i++) {
                JCoParameterList jpl = function.getExportParameterList();
                DefaultListMetaData dlmd = (DefaultListMetaData) jpl.getListMetaData();
                String name = dlmd.getName(i);
                String Type = dlmd.getTypeAsString(i);
                int Typei = dlmd.getType(i);
                if (Typei == 17) {
                    JCoStructure structure = function.getExportParameterList().getStructure(((DefaultListMetaData) function.getExportParameterList().getListMetaData()).getName(i));
                    SapCommon.setDefaultValue(structure);
                }
                if (Typei == 99) {
                    JCoTable table = function.getExportParameterList().getTable(((DefaultListMetaData) function.getExportParameterList().getListMetaData()).getName(i));
                    SapCommon.setDefaultValue(table);
                }
                String aa = "";
            }
            rJson += function.getExportParameterList().toJSON() + "\r\n";
        }
        rJson += "TableParameter" + "\r\n";
        if (function.getTableParameterList() != null) {
            for (i = 0; i < function.getTableParameterList().getFieldCount(); i++) {
                JCoTable table = function.getTableParameterList().getTable(((DefaultListMetaData) function.getTableParameterList().getListMetaData()).getName(i));
                SapCommon.setDefaultValue(table);
                rJson += function.getTableParameterList().toJSON() + "\r\n";
            }
        }
        rJson += "ChangingParameter" + "\r\n";
        if (function.getChangingParameterList() != null) {
            for (i = 0; i < function.getChangingParameterList().getFieldCount(); i++) {
                JCoParameterList jpl = function.getChangingParameterList();
                DefaultListMetaData dlmd = (DefaultListMetaData) jpl.getListMetaData();
                String name = dlmd.getName(i);
                String Type = dlmd.getTypeAsString(i);
                int Typei = dlmd.getType(i);
                if (Typei == 17) {
                    JCoStructure structure = function.getChangingParameterList().getStructure(((DefaultListMetaData) function.getChangingParameterList().getListMetaData()).getName(i));
                    SapCommon.setDefaultValue(structure);
                }
                if (Typei == 99) {
                    JCoTable table = function.getChangingParameterList().getTable(((DefaultListMetaData) function.getChangingParameterList().getListMetaData()).getName(i));
                    SapCommon.setDefaultValue(table);
                }
                String aa = "";
            }
            rJson += function.getChangingParameterList().toJSON() + "\r\n";
        }
        return rJson;
    }

    @RequestMapping("/getFunInfo")
    @ResponseBody
    public SAPReturn getFunInfo(@RequestBody SAPLoginPara sapPara) throws JCoException {
        SAPReturn rSap = new SAPReturn(sapPara);
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara);
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);
        if (function.getImportParameterList() != null) {
            for (int i = 0; i < function.getImportParameterList().getFieldCount(); i++) {
                JCoParameterList jpl = function.getImportParameterList();
                DefaultListMetaData dlmd = (DefaultListMetaData) jpl.getListMetaData();
                String name = dlmd.getName(i);
                String Type = dlmd.getTypeAsString(i);
                int Typei = dlmd.getType(i);
                if (Typei == 17) {
                    JCoStructure structure = function.getImportParameterList().getStructure(((DefaultListMetaData) function.getImportParameterList().getListMetaData()).getName(i));
                    SapCommon.setDefaultValue(structure);
                }
                if (Typei == 99) {
                    JCoTable table = function.getImportParameterList().getTable(((DefaultListMetaData) function.getImportParameterList().getListMetaData()).getName(i));
                    SapCommon.setDefaultValue(table);
                }
                String aa = "";
            }
            rSap.InputJson = function.getImportParameterList().toJSON();
        }

        if (function.getExportParameterList() != null) {
            rSap.OutputJson = function.getExportParameterList().toJSON();
        }
        if (function.getTableParameterList() != null) {
            for (int i = 0; i < function.getTableParameterList().getFieldCount(); i++) {
                JCoTable table = function.getTableParameterList().getTable(((DefaultListMetaData) function.getTableParameterList().getListMetaData()).getName(i));
                table.appendRow();
            }
        }
        if (function.getTableParameterList() != null) {
            rSap.TableJson = function.getTableParameterList().toJSON();
        }
        if (function.getChangingParameterList() != null) {
            rSap.ChangeJson = function.getChangingParameterList().toJSON();
        }
        return rSap;
//       16 ABAPOBJECT
//       17 STRUCTURE
//       99 TABLE
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
        if (sapPara.InputJson != "" && sapPara.InputJson != null) {
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
            if (function.getImportParameterList() != null) {
                rSap.InputJson = function.getImportParameterList().toJSON();
            }
            if (function.getExportParameterList() != null) {
                rSap.OutputJson = function.getExportParameterList().toJSON();
            }
            if (function.getChangingParameterList() != null) {
                rSap.ChangeJson = function.getChangingParameterList().toJSON();
            }
            if (function.getTableParameterList() != null) {
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
