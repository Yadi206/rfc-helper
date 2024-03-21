package com.tiger.rfchelp.web;

public class SAPReturn {
    public SAPReturn() {
    }
    public SAPReturn(SAPLoginPara para) {
        this.JCO_ASHOST = para.JCO_ASHOST;
        this.JCO_SYSNR = para.JCO_SYSNR;
        this.JCO_CLIENT = para.JCO_CLIENT;
        this.JCO_USER = para.JCO_USER;
        this.JCO_PASSWD = "";
        this.JCO_LANG = para.JCO_LANG;
        this.JCO_POOL_CAPACITY = para.JCO_POOL_CAPACITY;
        this.JCO_PEAK_LIMIT = para.JCO_PEAK_LIMIT;
        this.JCO_SAPROUTER = para.JCO_SAPROUTER;
        this.FUNNAME = para.FUNNAME;
        this.InputJson = para.InputJson;
        this.OutputJson = para.OutputJson;
        this.TableJson = para.TableJson;
        this.ChangeJson = para.ChangeJson;
    }
    public String JCO_ASHOST;//服务器
    public String JCO_SYSNR;// 系统编号
    public String JCO_CLIENT;// SAP集团
    public String JCO_USER;// SAP用户名
    public String JCO_PASSWD;// 密码
    public String JCO_LANG;// 登录语言:ZH EN
    public String JCO_POOL_CAPACITY;// 最大连接数
    public String JCO_PEAK_LIMIT;// 最大连接线程
    public String JCO_SAPROUTER;//远程链接服务
    public String FUNNAME;

    public String InputJson;
    public String OutputJson;
    public String TableJson;
    public String ChangeJson;

    public String rType;

    public  String rMessage;
}
