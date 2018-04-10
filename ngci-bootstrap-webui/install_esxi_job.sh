#!/bin/sh
echo "Job (install esxi) Starting"

export JOBID=$1

echo "Job id is $JOBID"

python deploy_esxi_x.py --jobid $JOBID

echo "Job Completed"
echo "Last Message"