package com.tiger.rfchelp.web;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider; 
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

public   class SapConnect {

    private static final String SAP_LOGIN_CONFIG = "SAP_LOGIN_CONFIG";
    private static JCoDestination destination = null;

    private static void createDataFile(String name, String suffix, Properties properties) {
        File cfg = new File(name + "." + suffix);
        if (cfg.exists()) {
            cfg.deleteOnExit();
        }
        try {

            FileOutputStream fos = new FileOutputStream(cfg, false);
            properties.store(fos, "for tests only !");
            fos.close();
        } catch (Exception e) {
            System.out.println("Create Data file fault, error msg: " + e.toString());
            throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
        }
    }

    /*
     * * 获取SAP连接
     *
     * @return SAP连接对象
     */
    public static JCoDestination connect(SAPLoginPara sapPara) {
        try {
            Properties connectProperties = new Properties();
            connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapPara.JCO_ASHOST);// 服务器
            connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sapPara.JCO_SYSNR); // 系统编号
            connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sapPara.JCO_CLIENT); // SAP集团
            connectProperties.setProperty(DestinationDataProvider.JCO_USER, sapPara.JCO_USER); // SAP用户名
            connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapPara.JCO_PASSWD); // 密码
            connectProperties.setProperty(DestinationDataProvider.JCO_LANG, sapPara.JCO_LANG); // 登录语言:ZH EN
            connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, sapPara.JCO_POOL_CAPACITY); // 最大连接数
            connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, sapPara.JCO_PEAK_LIMIT); // 最大连接线程
            connectProperties.setProperty(DestinationDataProvider.JCO_SAPROUTER, sapPara.JCO_SAPROUTER); // 远程服务
            createDataFile(SAP_LOGIN_CONFIG, "jcoDestination", connectProperties);

            if(null == destination)
                destination = JCoDestinationManager.getDestination(SAP_LOGIN_CONFIG);
        } catch (JCoException e) {
            System.out.println("Connect SAP fault, error msg: " + e.toString());
        }
        return destination;
    }

    public static String initialConfig(SAPLoginPara sapPara) {
        String sRrturn = "";
        try {
            Properties connectProperties = new Properties();
            connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapPara.JCO_ASHOST);// 服务器
            connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sapPara.JCO_SYSNR); // 系统编号
            connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sapPara.JCO_CLIENT); // SAP集团
            connectProperties.setProperty(DestinationDataProvider.JCO_USER, sapPara.JCO_USER); // SAP用户名
            connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapPara.JCO_PASSWD); // 密码
            connectProperties.setProperty(DestinationDataProvider.JCO_LANG, sapPara.JCO_LANG); // 登录语言:ZH EN
            connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, sapPara.JCO_POOL_CAPACITY); // 最大连接数
            connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, sapPara.JCO_PEAK_LIMIT); // 最大连接线程
            connectProperties.setProperty(DestinationDataProvider.JCO_SAPROUTER, sapPara.JCO_SAPROUTER); // 远程服务
            createDataFile(SAP_LOGIN_CONFIG, "jcoDestination", connectProperties);

            if(null == destination)
                destination = JCoDestinationManager.getDestination(SAP_LOGIN_CONFIG);
                if (destination.isValid())
                {
                    sRrturn = "连接成功";
                }else
                {
                    sRrturn = "连接失败";
                }
        } catch (JCoException e) {
            sRrturn = "Connect SAP fault, error msg: " + e.toString();
        }
        return sRrturn;
    }

}
