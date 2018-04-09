/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.bootstrap.classjobs;

import com.dell.cpsd.bootstrap.BootStrapJob;

public class NoOpJob extends BootStrapJob
{
    @Override
    public void executeJob() throws Exception
    {
        try
        {
            int step = 0;
            while (this.getMyJob().getProgress() < 100)
            {
                this.getMyJob().setProgress(this.getMyJob().getProgress() + 9);
                this.getMyJob().addConsoleMessage("Working on step " + step++);
                this.getMyJob().addMessage("data output " + step);
                Thread.sleep(900);
                System.out.println("My job is " + this.getMyJob().getProgress() + "% Completed"); // console message
            }
        }
        catch (Exception uhOh)
        {

        }
        finally
        {

        }
    }
}
