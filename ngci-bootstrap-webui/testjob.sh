#!/bin/sh
echo "Job Starting"

export JOBID=$1

echo "Job id is $JOBID"

wget -q -O jobdetails-$JOBID.log http://localhost:8400/bootstrap/jobdetails/$JOBID

wget -q -O /dev/null http://localhost:8400/bootstrap/status --post-data "jobid=$JOBID&progress=0&message=Started"
sleep 3
wget -q -O /dev/null http://localhost:8400/bootstrap/status --post-data "jobid=$JOBID&progress=20&message=Thinking"
sleep 3
wget -q -O /dev/null http://localhost:8400/bootstrap/status --post-data "jobid=$JOBID&progress=50&message=Planning"
sleep 3
wget -q -O /dev/null http://localhost:8400/bootstrap/status --post-data "jobid=$JOBID&progress=85&message=Working"
sleep 3
wget -q -O /dev/null http://localhost:8400/bootstrap/status --post-data "jobid=$JOBID&progress=100&message=Done"

echo "Job Completed"
echo "Last Message"


