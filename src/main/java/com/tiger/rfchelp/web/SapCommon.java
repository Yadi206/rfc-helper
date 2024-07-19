package com.tiger.rfchelp.web;

import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.rt.DefaultListMetaData;

public class SapCommon {

    public static void setDefaultValue(JCoStructure structure) {
        JCoRecordMetaData sm = structure.getRecordMetaData();
        for (int j = 0; j < sm.getFieldCount(); j++) {
            if (sm.getType(j) == 17) {
                JCoStructure st = structure.getStructure(j);
                setDefaultValue(st);
            }
            if (sm.getType(j) == 99) {
                JCoTable tb = structure.getTable(j);
                setDefaultValue(tb);
            }
        }
    }

    public static void setDefaultValue(JCoTable table) {
        table.appendRow();
        JCoRecordMetaData sm = table.getRecordMetaData();
        for (int j = 0; j < sm.getFieldCount(); j++) {
            if (sm.getType(j) == 17) {
                JCoStructure st = table.getStructure(j);
                setDefaultValue(st);
            }
            if (sm.getType(j) == 99) {
                JCoTable tb = table.getTable(j);
                setDefaultValue(tb);
            }
        }
    }
}
