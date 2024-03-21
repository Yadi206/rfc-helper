API简介：
initialConfig：配置登录文件
printFunInfo：打印函数参数信息
getFunInfo：获取函数信息
getInputJson：读取输入参数
getTableJson：读取表参数
getOutputJson：读取输出参数
getChangeJson：读取修改参数
runrfc：通用的RFC调用

调用JSON举例：
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

第一次使用时，可通过initialConfig创建配置登录文件
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

package后，需要将JAR中的sapjco3-3.1.2.jar 改为 sapjco3.jar