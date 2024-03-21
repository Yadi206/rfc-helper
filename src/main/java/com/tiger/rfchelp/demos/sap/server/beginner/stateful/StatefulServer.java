package com.tiger.rfchelp.demos.sap.server.beginner.stateful;

import java.io.IOException;

import com.tiger.rfchelp.demos.sap.server.beginner.SimpleFunctionHandling;
import com.sap.conn.jco.server.JCoServer;

/**
 * This example demonstrate the stateful RFC function modules. To run the example, please create in your SAP System:<br>
 * <ul>
 * <li>remote enabled function Z_INCREMENT_COUNTER wrapping the INCREMENT_COUNTER<br>
 * 
 * <pre>
 *          FUNCTION Z_INCREMENT_COUNTER.
 *          CALL FUNCTION 'INCREMENT_COUNTER'.
 *          ENDFUNCTION.
 * </pre>
 * 
 * <li>remote enabled function Z_GET_COUNTER wrapping the GET_COUNTER<br>
 * 
 * <pre>
 *          FUNCTION Z_GET_COUNTER.
 *          CALL FUNCTION 'GET_COUNTER'
 *              IMPORTING
 *                 GET_VALUE = GET_VALUE
 *          .
 *          ENDFUNCTION.
 * </pre>
 * 
 * with GET_VALUE TYPE I as export parameter.
 * <li>report ZJCO_STATEFUL_COUNTER<br>
 * 
 * <pre>
 *          REPORT  ZJCO_STATEFUL_COUNTER.
 *          PARAMETERS dest TYPE RFCDEST.
 *          
 *          DATA value TYPE i.
 *          DATA loops TYPE i VALUE 5.
 *          
 *          DO loops TIMES.
 *              CALL FUNCTION 'Z_INCREMENT_COUNTER' DESTINATION dest.
 *          ENDDO.
 *          
 *          CALL FUNCTION 'Z_GET_COUNTER' DESTINATION dest
 *              IMPORTING
 *                 GET_VALUE       = value
 *          .
 *          
 *          IF value <> loops.
 *            write: / 'Error expecting ', loops, ', but get ', value, ' as counter value'.
 *          ELSE.
 *            write: / 'success'.
 *          ENDIF.
 * </pre>
 * </ul>
 *
 * The function modules are required in this example for repository queries only. The client-side stateful communication is
 * illustrated by the client examples. The report, however, is the one you will use to verify the stateful example.
 */
public class StatefulServer
{

    public static void main(String[] args) throws IOException, InterruptedException
    {
        JCoServer server=SimpleFunctionHandling.createServer();
        server.setCallHandlerFactory(new MyFunctionHandlerFactory());

        server.start();

        // don't close application
        System.in.read();

        // stop the server asynchronously and wait until the server state is stopped, i.e. the server has been closed gracefully
        server.stop();
        SimpleFunctionHandling.waitForServerStop(server);
    }
}
