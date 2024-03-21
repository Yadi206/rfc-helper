package com.tiger.rfchelp.demos.sap.client.advanced.multithreading.job;

public interface MultiStepJob
{

    boolean isFinished();

    void runNextStep();

    String getName();

    void cleanUp();
}
