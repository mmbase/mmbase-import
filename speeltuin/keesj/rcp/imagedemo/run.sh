rm -rf out
mkdir out
cp src/*.xml out/
export CLASSPATH=out:simplexml-1.0.jar:thinlet.jar
javac -d out src/*.java
java  Images

