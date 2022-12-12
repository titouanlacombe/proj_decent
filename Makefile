default: run_manager

# Parse the command line arguments by ignoring the first one
ARGS = $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

build:
	javac -d bin -cp bin src/**/*.java src/*.java

clean:
	rm -rf bin/*

start_manager:
	java -cp bin Manager $(ARGS)

start_node:
	java -cp bin Node $(ARGS)

run_manager: build start_manager
run_node: build start_node
