#!/bin/sh

cd /home/ec2-user/src/QAAutomation_FILA 
java -jar ./QAAutomation_FILA.jar & 
wait

echo "Completed execution of test Automation"

/home/ec2-user/src/copy-to-s3.sh &
wait 
echo "Completed copying to S3"
