/**
 * Copyright &copy; 2018 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Will have a hash map of jobs, ID {[messages,...],progress}
// To limit memory useage, we will keep depth at 10

@SpringBootApplication
@Controller
public class BootStrapWeb extends SpringBootServletInitializer  
{
    private static String OS = System.getProperty("os.name").toLowerCase();

    static ArrayList<String> execResults = new ArrayList<String>();
    
    static int jobCounter = 0;
    
    HashMap<Long,JobDetail> currentJobs = new HashMap<Long,JobDetail>();
    
    @RequestMapping("/hello")
    public String hiThere(Map<String, Object> model)
    {
        System.out.println("You had me at hello");
        model.put("data1", "data1 object");
        model.put("data2", "data2 object");
        return "welcome";
    }
    
    @RequestMapping("/resultstxt")
    @ResponseBody
    public ArrayList<String> results()
    {
        ArrayList<String> response = new ArrayList<String>();
        response.add("Actual Results:");
        response.addAll(execResults);
        return response;
    }

    @RequestMapping(value = "/exectest", method = RequestMethod.POST)
    public String doWork()
    {
        
        System.out.println("POST CAlled on " + OS);
        execResults.clear();
        ExecProcess execProcess = new ExecProcess();
        //execProcess.setTask("do_test.sh");

        JobDetail newJob = new JobDetail();
        // jobid to var, wipe old one from mem first/?
        newJob.setJobId((jobCounter++ % 10) + 1);
        newJob.setProgress(0);

        
        if(OS.indexOf("win") >= 0)
        {
            execProcess.setTask("testjob.cmd " + newJob.getJobId());
            execProcess.setShellUse(false);
        }
        else
        {
            execProcess.setTask("testjob.sh " + newJob.getJobId());
        }

        newJob.addMessages("Starting " + execProcess.getTask());
         
        
        Thread thread = new Thread(execProcess);
        
        if(currentJobs.size() > 9)
        {
            System.out.print("Need to prune JOBS");
        }
        
        System.out.println("ADDING JOB " + newJob.getJobId());
        currentJobs.put(newJob.getJobId(), newJob);
        
        thread.start();
        return "redirect:./results/" + newJob.getJobId();
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public ResponseEntity<String> updateJobStatus(@RequestParam("jobid") long jobId, 
            @RequestParam("progress") int progress, @RequestParam("message") String message)
    {
        JobDetail job = currentJobs.get(jobId);
        
        if(job == null)
        {
            return new ResponseEntity("Job Not Found", HttpStatus.NOT_FOUND);
        }
        
        job.setProgress(Math.min(progress, 100));
        job.addMessages(message);

        return new ResponseEntity("Status updated", HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/results/{jobid}", method = RequestMethod.GET)
    public String getFoosBySimplePathWithPathVariable(@PathVariable("jobid") long jobId, Map<String, Object> results)
    {
        JobDetail job = currentJobs.get(jobId);
        
        if(job != null)
        {
            results.put("jobid", job.getJobId());
            results.put("progress", job.getProgress());
            results.put("messages", job.getMessages());
        }

        return "displayprogress";
    }

    private class JobDetail
    {
        long              jobId;
        int               progress;
        ArrayList<String> messages = new ArrayList<String>();

        public long getJobId()
        {
            return jobId;
        }

        public void setJobId(long jobId)
        {
            this.jobId = jobId;
        }

        public int getProgress()
        {
            return progress;
        }

        public void setProgress(int progress)
        {
            this.progress = progress;
        }

        public ArrayList<String> getMessages()
        {
            return messages;
        }

        public void addMessages(String message)
        {
            this.messages.add(message);
        }
    }

    public class ExecProcess implements Runnable
    {
        private String commandLine = "ls";
        private boolean useShell = true;
        
        public void run()
        {
            Runtime r = Runtime.getRuntime();
            Process app = null;
            BufferedReader output = null;
            String line;
            
            try
            {
                String[] cmd = {
                        "/bin/sh",
                        "-c",
                        commandLine
                        };
                
                
                if (!useShell)
                {
                    app = r.exec(commandLine);
                }
                else
                {
                    app = r.exec(cmd);
                }

                System.out.println("In ExecProcess after exec [" + commandLine + "] " + app.toString());
                output = new BufferedReader(new InputStreamReader(app.getInputStream()));
                
                while(true)
                {
                    line = output.readLine();
                    System.out.println(line);
                    if(line == null)
                    {
                        System.out.println("In ExecProcess after EOF");
                        System.out.flush();
                        break;
                    }
                    execResults.add(line);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if( output != null)
                {
                    try
                    {
                        output.close();
                    }
                    catch (IOException ignored)
                    {
                        System.currentTimeMillis();
                    }
                }
            }
            
            if( app != null)
            {
                try
                {
                    app.waitFor(); // wait for process to complete
                }
                catch (InterruptedException e)
                {
                    System.err.println(e); // "Can'tHappen"
                    return;
                }
                System.err.println("Process done, exit status was " + app.exitValue());
            }
            execResults.add("ThatsAll");
            System.out.println("ExecProcess Completed");
        }
        
        public void setTask(String commandLine)
        {
            this.commandLine = new String(commandLine);
        }
        
        public String getTask()
        {
            return this.commandLine;
        }
        
        public void setShellUse(boolean value)
        {
            this.useShell = value;
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(BootStrapWeb.class);
    }

    public static void main(String[] args)
    {
        SpringApplication.run(BootStrapWeb.class, args);
    }
}