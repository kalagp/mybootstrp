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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
// To limit memory usage, we will keep depth at 10 (MAX_NUMBER_JOBS)

@SpringBootApplication
@Controller
public class BootStrapWeb extends SpringBootServletInitializer  
{
    private static int MAX_NUMBER_JOBS = 10;
    private static String OS = System.getProperty("os.name").toLowerCase();

    static int jobCounter = 0;
    
    HashMap<Long,JobDetail> currentJobs = new HashMap<Long,JobDetail>();
    
    @RequestMapping("/reset")
    @ResponseBody
    public String reset()
    {
        currentJobs.clear();
        return "Results cleared";
    }

    @RequestMapping("/jobs")
    //public String getJobs(Map<String,Object> jobs, HttpServletRequest request)
    public String getJobs( HttpServletRequest request)
    {
        doWork();
        
        request.setAttribute("jobList", currentJobs);
        return "JobsList";
    }
    
    // See testjob.html
    @RequestMapping(value = "/exectest", method = RequestMethod.POST)
    public String doWork()
    {
        Long newJobId = (long) ((jobCounter++ % MAX_NUMBER_JOBS) + 1);
        
        if(currentJobs.get(newJobId) != null)
        {
            if(currentJobs.get(newJobId).getProgress() != 100 )
            {
                System.out.println("Job queue may be full");
                return "error";
            }
            else
            {
                // Destroy old job
                currentJobs.remove(newJobId);
            }
        }
        
        ExecProcess execProcess = new ExecProcess();

        JobDetail newJob = new JobDetail();
        // jobid to var, wipe old one from mem first/?
        newJob.setJobId(newJobId);
        newJob.setJobName("testjob");
        newJob.setProgress(0);
        
        execProcess.setJobId(newJobId);
        
        if(OS.indexOf("win") >= 0)
        {
            execProcess.setTask("testjob.cmd " + newJob.getJobId());
            execProcess.setShellUse(false);
        }
        else
        {
            execProcess.setTask("testjob.sh " + newJob.getJobId());
        }

        newJob.addMessage("Starting " + execProcess.getTask());

        System.out.println("ADDING JOB " + newJob.getJobId());
        currentJobs.put(newJob.getJobId(), newJob);
        
        Thread thread = new Thread(execProcess);
        thread.start();
        return "redirect:./results/" + newJob.getJobId();
    }

    @RequestMapping(value = "/startjob", method = RequestMethod.POST)
    public String startNewJob(@RequestParam("job") String job)
    {
        Long newJobId = -1L;
        newJobId = forkJob(job, (OS.indexOf("win") == -1));
        return "redirect:./results/" + newJobId;
    }

    
    // TODO
    public Long forkJob(String executable, boolean useCommandShell)
    {
        Long newJobId = (long) ((jobCounter++ % MAX_NUMBER_JOBS) + 1);
        
        if(currentJobs.get(newJobId) != null)
        {
            if(currentJobs.get(newJobId).getProgress() != 100 )
            {
                System.out.println("Job queue may be full");
                return -1L;
            }
            else
            {
                // Destroy old job
                currentJobs.remove(newJobId);
            }
        }
        
        ExecProcess execProcess = new ExecProcess();

        
        execProcess.setJobId(newJobId);
        execProcess.setTask(executable + " " + newJobId);
        execProcess.setShellUse(useCommandShell);

        JobDetail newJob = new JobDetail();
        newJob.setJobId(newJobId);
        newJob.setJobName(executable);
        newJob.setProgress(0);
        newJob.addMessage("Starting " + execProcess.getTask());
        
        Thread thread = new Thread(execProcess);
        
        System.out.println("ADDING JOB " + newJob.getJobId());
        currentJobs.put(newJob.getJobId(), newJob);
        
        thread.start();
        
        return newJobId;
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
        job.addMessage(message);

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

    public class JobDetail
    {
        long              jobId;
        String            jobName;
        int               progress;
        ArrayList<String> messages = new ArrayList<String>();
        ArrayList<String> consoleMessages = new ArrayList<String>();

        public long getJobId()
        {
            return jobId;
        }

        public void setJobId(long jobId)
        {
            this.jobId = jobId;
        }

        public String getJobName()
        {
            return jobName;
        }

        public void setJobName(String jobName)
        {
            this.jobName = jobName;
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

        public void setMessages(ArrayList<String> messages)
        {
            this.messages = messages;
        }

        public void addMessage(String message)
        {
            this.messages.add(message);
        }

        public void setConsoleMessages(ArrayList<String> consoleMessages)
        {
            this.consoleMessages = consoleMessages;
        }

        public ArrayList<String> getConsoleMessages()
        {
            return consoleMessages;
        }

        public void addConsoleMessage(String message)
        {
            this.consoleMessages.add(message);
        }
    }

    public class ExecProcess implements Runnable
    {
        private String commandLine = "ls";
        private boolean useShell = true;
        private Long jobId = 0L;
        
        public void run()
        {
            System.out.println("Starting Job ID " + jobId);
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
                    System.out.println(currentJobs);
                    System.out.println(line);
                    System.out.println(currentJobs.get(this.jobId));
                    currentJobs.get(this.jobId).addConsoleMessage(line);
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
            currentJobs.get(this.jobId).addConsoleMessage("ThatsAll");
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

        public Long getJobId()
        {
            return jobId;
        }

        public void setJobId(Long newJobId)
        {
            this.jobId = newJobId;
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