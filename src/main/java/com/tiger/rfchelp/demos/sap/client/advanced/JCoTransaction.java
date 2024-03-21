package com.tiger.rfchelp.demos.sap.client.advanced;

/**
 * SImple helper class for making a tRfc transaction looking similar to a bgRFC unit.
 */
public class JCoTransaction
{
    private String tid;

    public JCoTransaction(String transactionID)
    {
        tid=transactionID;
    }

    public String getTid()
    {
        return tid;
    }

}
