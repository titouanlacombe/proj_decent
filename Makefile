default: run_manager

# Parse the command line arguments by ignoring the first one
ARGS = $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

build:
	javac -d bin -cp bin src/**/*.java src/*.java

run_manager: build
	java -cp bin Manager $(ARGS)

run_node: build
	java -cp bin Node $(ARGS)

clean:
	rm -rf bin/*
