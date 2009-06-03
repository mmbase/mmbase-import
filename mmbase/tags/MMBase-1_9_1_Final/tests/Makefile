
export JAVA_HOME=/home/nightly/jdk

mvn=/home/nightly/maven2/bin/mvn
#mvn=mvn
ant=ant -quiet -listener org.apache.tools.ant.listener.Log4jListener -lib target/dependency:. -Dnoconnection=true
#ant=ant -lib lib:. -Dnoconnection=true

.PHONY: all bridge rmmci dbsq util datatypes clustering convert typerel richtext cloudcontext transactions
all: compile dependencies
	$(ant) run.all | tee work/all.log

#ok
bridge: dependencies
	$(ant) run.bridge | tee work/bridge.log


#1 or 2 failures
rmmci:  dependencies
	$(ant) run.bridge.rmmci | tee work/rmmci.log

#ok
richtext: dependencies
	$(ant) run.richtext | tee work/richtext.log

#ok
dbsq: dependencies
	$(ant) run.dbsq | tee work/dbsq.log

#ok
util: dependencies
	$(ant) run.util | tee work/util.log


rmmciutil: dependencies
	$(ant) run.rmmciutil | tee work/rmmciutil.log

#ok
datatypes: dependencies
	$(ant) run.datatypes | tee work/datatypes.log

#
clustering:  dependencies
	$(ant) run.clustering | tee work/clustering.log

#ok
cloudcontext: dependencies
	$(ant) run.cloudcontext | tee work/cloudcontext.log

#ok
convert: dependencies
	$(ant) run.convertimage | tee work/convertimage.log

#ok
typerel: dependencies
	$(ant) run.typerel | tee work/typerel.log

# not yet checked in
transactions:
	$(ant) run.transactions | tee work/transactions.log


dependencies:
	mkdir -p work
	$(mvn) dependency:copy-dependencies

.PHONY: compile
compile:
	$(mvn) compile jar:jar
