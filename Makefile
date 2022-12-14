default: run_app

# Parse the command line arguments by ignoring the first one
ARGS = $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

build:
	javac src/utils/*.java \
		src/config/*.java \
		src/sim/protocol/*.java \
		src/sim/*.java \
		src/ui/*.java \
		src/*.java

clean:
	find . -name "*.class" -type f -delete

start_manager:
	java -cp src Manager $(ARGS)

start_node:
	java -cp src Node $(ARGS)

start_app:
	java -cp src App $(ARGS)

run_manager: build start_manager
run_node: build start_node
run_app: build start_app
