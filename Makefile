default: run_manager

# Parse the command line arguments by ignoring the first one
ARGS = $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

build:
	javac -d bin -cp bin src/sim/protocol/*.java
	javac -d bin -cp bin src/sim/*.java
	javac -d bin -cp bin src/ui/*.java
	javac -d bin -cp bin src/utils/*.java
	javac -d bin -cp bin src/*.java src/*.java

clean:
	rm -rf bin/*

start_manager:
	java -cp bin Manager $(ARGS)

start_node:
	java -cp bin Node $(ARGS)

start_simulator:
	java -cp bin Simulator $(ARGS)

run_manager: build start_manager
run_node: build start_node
run_simulator: build start_simulator
