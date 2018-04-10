/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.bootstrap;

import com.dell.cpsd.bootstrap.BootStrapWeb.JobDetail;

public abstract class BootStrapJob implements Runnable
{
    private JobDetail myJob;
    public abstract void executeJob() throws Exception;
    
    public JobDetail getMyJob()
    {
        return myJob;
    }

    public void setMyJob(JobDetail myJob)
    {
        this.myJob = myJob;
    }

    @Override
    public void run()
    {
        try
        {
            myJob.setProgress(0);
            myJob.addMessage("Starting Java Job");
            executeJob();
            myJob.addMessage("Finished Java Job");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("executeJob finished");
    }
    
}
