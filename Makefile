default: run_manager

NB_NODES = 3

build:
	javac -d bin -cp bin src/**/*.java src/*.java

run_manager: build
	java -cp bin Manager $(NB_NODES)

run_node: build
	java -cp bin Node

run: build
	java -cp bin Main

clean:
	rm -rf bin/*
