default: build run

build:
	javac -d bin -cp bin src/**/*.java src/*.java

run:
	java -cp bin Main

clean:
	rm -rf bin/*
