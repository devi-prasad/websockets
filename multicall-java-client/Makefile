all:
	javac src/multicall/*.java  -cp lib/jetty-all-9.0.5.v20130815.jar -d out

clean:
	rm -rf out/*
	rm -f src/multicall/*.class

run:
	cd out; java -ea -classpath .:..:multicall:../lib/jetty-all-9.0.5.v20130815.jar multicall/MulticallUser

