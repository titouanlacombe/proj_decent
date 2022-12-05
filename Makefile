default: build run

build:
	javac -d bin -cp bin **/*.java

run:
	java -cp bin Main

clean:
	rm -rf bin/*
