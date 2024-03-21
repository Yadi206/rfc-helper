package com.tiger.rfchelp.demos.sap.server.advanced;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.tiger.rfchelp.demos.sap.client.beginner.DestinationConcept;
import com.tiger.rfchelp.demos.sap.server.beginner.SimpleFunctionHandling;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerFunctionHandler;

/**
 * The following server example demonstrates how to develop a function module which should be defined only on Java side. At
 * first, we define the remote-enabled function module interface in a development ABAP backend, so that JCo is able to fetch its
 * meta data once in the beginning. The interface needs to look as defined below:
 * 
 * <pre>
 * FUNCTION JCO_SET_TRACE
 *   IMPORTING
 *     VALUE(TRACE_LEVEL) TYPE INT1
 *     VALUE(TRACE_PATH) TYPE STRING.
 * ENDFUNCTION.
 * </pre>
 * 
 * After activating the function module without implementation, we fetch the meta data from the backend with JCo and save it to
 * a JSON file using the {@link JCoRepository.save()} method. This file can then be reused to create a local repository.
 * 
 * Certainly, we also need to provide the implementation of the function - see class {@link SetTraceHandler}.
 * 
 * Last but not least, the following ABAP report invokes the function module JCO_SET_TRACE under which the SetTraceHandler
 * implementation is registered.
 * 
 * <pre>
 *      REPORT  ZTEST_JCO_SET_TRACE.
 *      
 *      DATA trace_level TYPE INT1.
 *      DATA trace_path TYPE STRING.
 *      DATA msg(255) TYPE C.
 *      
 *      trace_level = 5.
 *      trace_path = '.'.
 *      
 *      CALL FUNCTION 'JCO_SET_TRACE' destination 'JCO_SERVER'
 *        EXPORTING
 *          TRACE_LEVEL = trace_level
 *          TRACE_PATH = trace_path
 *       EXCEPTIONS
 *         COMMUNICATION_FAILURE       = 1
 *         SYSTEM_FAILURE              = 2 MESSAGE msg
 *         RESOURCE_FAILURE            = 3
 *         OTHERS                      = 4
 *                .
 *      IF SY-SUBRC <> 0.
 *         write: 'ERROR: ',  SY-SUBRC, msg.
 *      ENDIF.
 * </pre>
 */
public class StaticRepository
{

    public static void main(String[] args) throws IOException, JCoException, InterruptedException
    {
        final String functionName="JCO_SET_TRACE";

        // first store locally.. 
        // comment out when not calling the class for the first time to fetch the metadata from the backend
        // typically this would be done in some independent class
        createLocalRepositoryFile(functionName);

        // .. and read from file
        final JCoCustomRepository customRepository=getRepositoryFromFile(functionName);

        JCoServer server=SimpleFunctionHandling.createServer();

        // for function modules whose metadata is not provided in the file, there is option to specific a destination where it
        // might be then fetched dynamically
        String repDest=server.getRepositoryDestination();
        if (repDest!=null)
            try
            {
                customRepository.setDestination(JCoDestinationManager.getDestination(repDest));
            }
            catch (JCoException e)
            {
                e.printStackTrace();
                System.out.println(">>> repository contains static function definition only");
            }

        // set the repository in the server
        server.setRepository(customRepository);

        DefaultServerHandlerFactory.FunctionHandlerFactory factory=new DefaultServerHandlerFactory.FunctionHandlerFactory();
        factory.registerHandler(functionName, new SetTraceHandler());

        server.setCallHandlerFactory(factory);

        server.start();

        // don't close application
        System.in.read();

        // stop the server asynchronously and wait until the server state is stopped, i.e. the server has been closed gracefully
        server.stop();
        SimpleFunctionHandling.waitForServerStop(server);
    }

    private static void createLocalRepositoryFile(String functionName) throws JCoException, IOException
    {
        // get the meta data from the dev backend
        final JCoFunctionTemplate func_template=JCoDestinationManager
                .getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1).getRepository()
                .getFunctionTemplate(functionName);

        // create a custom repository just containing this meta data
        final JCoCustomRepository customRepository=JCo.createCustomRepository("MyCustomRepository");
        customRepository.addFunctionTemplateToCache(func_template);

        // save it
        final FileWriter writer=new FileWriter(new File(functionName+".json").getAbsolutePath());
        customRepository.save(writer);
        writer.close();
    }

    private static JCoCustomRepository getRepositoryFromFile(String functionName) throws IOException
    {
        // create a custom repository just containing this meta data from file
        final JCoCustomRepository customRepository=JCo.createCustomRepository("MyCustomRepository");

        final FileReader reader=new FileReader(functionName+".json");
        customRepository.load(reader);
        reader.close();
        return customRepository;
    }

    private static class SetTraceHandler implements JCoServerFunctionHandler
    {

        @Override
        public void handleRequest(JCoServerContext serverCtx, JCoFunction function)
        {
            final int level=function.getImportParameterList().getInt("TRACE_LEVEL");
            final String path=function.getImportParameterList().getString("TRACE_PATH");
            System.out.println(">>> SetTrace invoked with "+level+(path!=null?(", "+path):""));
            JCo.setTrace(level, path);
        }
    }
}
