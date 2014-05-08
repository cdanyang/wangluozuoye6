#!/bin/bash
javac Client.java
max=$3
max=$(($max - 1))
echo $max
for i in `seq 0 $max`
do
	java Client $1 $2 $i &
done
