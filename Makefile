default: run_app

# Parse the command line arguments by ignoring the first one
ARGS = $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
export BIN_DIR=bin

data:
	mkdir -p data

build:
	javac -d $(BIN_DIR) $$(find src -name "*.java")

clean:
	rm -rf data
	rm -rf $(BIN_DIR)

start_manager: data
	java -cp $(BIN_DIR) Manager $(ARGS)

start_node: data
	java -cp $(BIN_DIR) Node $(ARGS)

start_app: data
	java -cp $(BIN_DIR) App $(ARGS)

run_manager: build start_manager
run_node: build start_node
run_app: build start_app
