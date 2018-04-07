@echo off

set jobid=%1

IF "%1"=="" SET jobid=0

echo Job id is %jobid%

wget http://localhost/bootstrap/status --post-data "jobid=%jobid%&progress=0&message=Started"
timeout 10
wget http://localhost/bootstrap/status --post-data "jobid=%jobid%&progress=50&message=Working"
timeout 10
wget http://localhost/bootstrap/status --post-data "jobid=%jobid%&progress=100&message=Done"

:end