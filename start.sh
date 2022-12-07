#!/bin/sh
set -e

export NUM_NODES=$1

# Build
javac -d bin -cp bin src/**/*.java src/*.java

# Start manager in background
java -cp bin Manager $NUM_NODES &

# Recover manager address
sleep 0.5
MANAGER_ADDR=$(cat data/manager_address.txt)

# Start n nodes
for i in $(seq 1 $NUM_NODES)
do
	java -cp bin Node $MANAGER_ADDR &
done
