package com.tiger.rfchelp.demos.sap.client.advanced;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.tiger.rfchelp.demos.sap.client.beginner.DestinationConcept;
import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.ext.TenantProvider;

/**
 * This example demonstrates, how a tenant provider can be registered, and that relevant internal runtime objects such as
 * repositories and destinations are separated from each other.
 */
public class MultiTenantExample
{

    private static String TENANT_A="Tenant_A";
    private static String TENANT_B="Tenant_B";

    /** This tenant provider just changes its tenant if it is set from outside. */
    private static class TestTenantProvider implements TenantProvider
    {
        private String currentTenant;

        @Override
        public String getCurrentTenant()
        {
            return currentTenant;
        }

        void setCurrentTenant(String newTenant)
        {
            currentTenant=newTenant;
        }
    }

    public static void main(String[] args) throws JCoException
    {
        // create a simple tenant provider and register it
        final TestTenantProvider tenantProvider=new TestTenantProvider();
        Environment.registerTenantProvider(tenantProvider);

        // activate first tenant
        tenantProvider.setCurrentTenant(TENANT_A);

        // lookup of function module STFC_CONNECTION and execute it 
        final JCoDestination destinationA=JCoDestinationManager.getDestination(DestinationConcept.SomeSampleDestinations.ABAP_AS1);
        destinationA.getRepository().getFunction("STFC_CONNECTION").execute(destinationA);

        // now the repository and destination are printed out for tenant A
        printRepositoriesAndDestinations();

        // activate second tenant
        tenantProvider.setCurrentTenant(TENANT_B);

        // this should be empty now, since tenant B does not have executed anything
        printRepositoriesAndDestinations();
    }

    private static void printRepositoriesAndDestinations()
    {
        System.out.println("Repositories: ");
        for (String repository : JCo.getRepositoryIDs())
            System.out.println("\t"+repository);

        System.out.println("Destinations: ");
        for (String destination : JCo.getDestinationIDs())
            System.out.println("\t"+destination);
    }
}
