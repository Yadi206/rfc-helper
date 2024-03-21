# rfc-helper

#### 介绍
该服务通过JCO访问SAP的RFC函数。
预制了登录、读取结构、通用调用等方法
也包含了官方DEMO代码
同时以BAPI_PRODORD_CREATEC为例单独写了转换方法

#### 软件架构
软件架构说明
springboot
apache-maven-3.6.2
jdk 1.8


#### 安装教程
package后，需要将JAR中的sapjco3-3.1.2.jar 改为 sapjco3.jar


#### 使用说明
initialConfig：配置登录文件
printFunInfo：打印函数参数信息
getFunInfo：获取函数信息
getInputJson：读取输入参数
getTableJson：读取表参数
getOutputJson：读取输出参数
getChangeJson：读取修改参数
runrfc：通用的RFC调用

调用JSON举例：
```json
{
  "JCO_ASHOST": "172.1.1.1",
  "JCO_SYSNR": "00",
  "JCO_CLIENT": "125",
  "JCO_USER": "user",
  "JCO_PASSWD": "111111",
  "JCO_LANG": "ZH",
  "JCO_POOL_CAPACITY": "100",
  "JCO_PEAK_LIMIT": "100",
  "JCO_SAPROUTER": "",
  "FUNNAME": "STFC_CONNECTION",
  "InputJson": "{\"REQUTEXT\":\"参数值\"}",
  "OutputJson": "",
  "TableJson": "",
  "ChangeJson": ""
}
```


第一次使用时，可通过initialConfig创建配置登录文件

```json
{
  "JCO_ASHOST": "172.1.1.1",
  "JCO_SYSNR": "00",
  "JCO_CLIENT": "125",
  "JCO_USER": "user",
  "JCO_PASSWD": "111111",
  "JCO_LANG": "ZH",
  "JCO_POOL_CAPACITY": "100",
  "JCO_PEAK_LIMIT": "100",
  "JCO_SAPROUTER": ""
}
```


#### 作者介绍 

让分享成为习惯！

微信号：hyd85716

加群方法：加群主好友，拉入群

直播间：http://live.bilibili.com/4623066


#### 带JDK的JAR包

链接：https://pan.baidu.com/s/1XpojCkssCvXrV4m8I4RyEA?pwd=zfvo
提取码：zfvo

直接运行run.cmd启动服务