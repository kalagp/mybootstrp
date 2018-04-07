@echo off
@echo Job Starting

set jobid=%1

IF "%1"=="" SET jobid=0

echo Job id is %jobid%

wget http://localhost:8300/bootstrap/status --post-data "jobid=%jobid%&progress=0&message=Started"
sleep 3
wget http://localhost:8300/bootstrap/status --post-data "jobid=%jobid%&progress=50&message=Working"
sleep 3
wget http://localhost:8300/bootstrap/status --post-data "jobid=%jobid%&progress=100&message=Done"

@echo Job Completed


:end

