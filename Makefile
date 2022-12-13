default: run_manager

# Parse the command line arguments by ignoring the first one
ARGS = $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

build:
	javac src/utils/*.java \
		src/sim/protocol/*.java \
		src/sim/*.java \
		src/ui/*.java \
		src/*.java

clean:
	find . -name "*.class" -type f -delete

start_manager:
	java -cp bin Manager $(ARGS)

start_node:
	java -cp bin Node $(ARGS)

start_simulator:
	java -cp bin Simulator $(ARGS)

run_manager: build start_manager
run_node: build start_node
run_simulator: build start_simulator
