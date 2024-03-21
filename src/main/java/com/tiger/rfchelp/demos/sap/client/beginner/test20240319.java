package com.tiger.rfchelp.demos.sap.client.beginner;

import com.tiger.rfchelp.web.SAPLoginPara;
import com.tiger.rfchelp.web.SapConnect;
import com.sap.conn.jco.*;
import com.sap.conn.jco.rt.DefaultListMetaData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class test20240319 {

    public String postRunRfc(@RequestBody SAPLoginPara sapPara) throws JCoException {
        JCoDestination destination = null;
        destination = SapConnect.connect(sapPara);
        JCoFunction function = destination.getRepository().getFunction(sapPara.FUNNAME);
        JCoParameterList exps  = function.getExportParameterList();
        DefaultListMetaData dm = (DefaultListMetaData)function.getExportParameterList().getListMetaData();
        JCoMetaData m;
        JCoStructure JCoS;
        String userStr = "";
        char abapType;
        for (int i = 0; i < exps.getFieldCount() ; i++) {
            abapType = dm.getAbapType(i);
            switch(abapType){
                case 'C'://CHAR
                    break;
                case 'N'://NUM
                    break;
                case 'F'://FLOAT
                    break;
                case 'I'://INT
                    break;
                case 's'://INT2
                    break;
                case 'b'://INT1
                    break;
                case '*'://ABAPOBJECT
                    break;
                case 'u'://STRUCTURE
                    JCoS = exps.getStructure(dm.getName(i));
                    break;
                case 'a'://DECF16
                    break;
                case 'e'://DECF34
                    break;
                case 'g'://STRING
                    break;
                case 'y'://XSTRING
                    break;
                case 'j'://BOX
                    break;
                case 'k'://GENERIC_BOX
                    break;
                case '8'://INT8
                    break;
                case 'p'://UTCLONG
                    break;
                case 'n'://UTCSECOND
                    break;
                case 'w'://UTCMINUTE
                    break;
                case 'd'://DTDAY
                    break;
                case '7'://DTWEEK
                    break;
                case 'x'://DTMONTH
                    break;
                case 't'://TSECOND
                    break;
                case 'i'://TMINUTE
                    break;
                case 'c'://CDAY
                    break;
                case 'E'://EXCEPTION
                    break;
                case 'h'://TABLE
                    break;
                //你可以有任意数量的case语句
                default : //可选
                    //语句
            }
            //            C,CHAR
//            N,NUM
//            F,FLOAT
//            I,INT
//            s,INT2
//            b,INT1
//            *,ABAPOBJECT
//            u,STRUCTURE
//            a,DECF16
//            e,DECF34
//            g,STRING
//            y,XSTRING
//            j,BOX
//            k,GENERIC_BOX
//            8,INT8
//            p,UTCLONG
//            n,UTCSECOND
//            w,UTCMINUTE
//            d,DTDAY
//            7,DTWEEK
//            x,DTMONTH
//            t,TSECOND
//            i,TMINUTE
//            c,CDAY
//            E,EXCEPTION
//            h,TABLE


            exps.setActive(i, true);
            m =  exps.getMetaData();
            m.getName(i);

        }


        return userStr;

    }


}
