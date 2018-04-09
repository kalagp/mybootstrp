@echo off
@echo Job Starting

set jobid=%1

IF "%1"=="" SET jobid=0

echo Job id is %jobid%

wget -q -O jobdetails-%jobid%.log http://localhost:8400/bootstrap/jobdetails/%jobid%

wget -q -O NUL http://localhost:8400/bootstrap/status --post-data "jobid=%jobid%&progress=0&message=Started"
sleep 3
wget -q -O NUL http://localhost:8400/bootstrap/status --post-data "jobid=%jobid%&progress=20&message=Thinking"
sleep 3
wget -q -O NUL http://localhost:8400/bootstrap/status --post-data "jobid=%jobid%&progress=50&message=Planning"
sleep 3
wget -q -O NUL http://localhost:8400/bootstrap/status --post-data "jobid=%jobid%&progress=85&message=Working"
sleep 3
wget -q -O NUL http://localhost:8400/bootstrap/status --post-data "jobid=%jobid%&progress=100&message=Done"

@echo Job Completed
@echo Last Message


:end

