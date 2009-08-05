CLASSPATH=.
for i in ../../WEB-INF/lib/*.jar
do
	CLASSPATH=$CLASSPATH:$i
done
export CLASSPATH
javac  AbstractImport.java SyncNodes.java Convert.java && java -classpath $CLASSPATH Convert
