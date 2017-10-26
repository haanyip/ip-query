javac -d build -cp libs/netty-all-4.1.6.Final.jar:libs/log4j-1.2.8.jar:libs/json-20140107.jar:libs/jackson-annotations-2.8.0-20160405.011944-16.jar:libs/jackson-core-2.8.0-20160405.012243-62.jar:libs/jackson-databind-2.8.0-20160405.013229-76.jar:src/:. src/com/maxmind/db/*.java src/mm/server/*/*.java

#/usr/local/bin/java -cp libs/netty-all-4.1.6.Final.jar:libs/log4j-1.2.8.jar:libs/json-20140107.jar:libs/jackson-annotations-2.8.0-20160405.011944-16.jar:libs/jackson-core-2.8.0-20160405.012243-62.jar:libs/jackson-databind-2.8.0-20160405.013229-76.jar:src/:.  mm.server.engine.HttpServer

#find src -name '*.class' -print |cut -sd / -f 2-  > classes.list
jar cfm ipquery.jar Manifest.txt  -C build .
