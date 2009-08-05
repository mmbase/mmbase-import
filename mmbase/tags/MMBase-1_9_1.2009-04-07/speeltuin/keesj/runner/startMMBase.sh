CLASSPATH=.
for i in ../WEB-INF/lib/*.jar lib/*.jar
do
	CLASSPATH=$CLASSPATH:$i
done
export CLASSPATH
javac Start.java
java -classpath $CLASSPATH -Dmmbase.htmlroot=`pwd`/../ -Dmmbase.config=`pwd`/../WEB-INF/config Start
