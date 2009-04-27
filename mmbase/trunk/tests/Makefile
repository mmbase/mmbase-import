
export JAVA_HOME=/opt/jdk15

ant=ant -quiet -listener org.apache.tools.ant.listener.Log4jListener -lib lib:. -Dnoconnection=true
#ant=ant -lib lib:. -Dnoconnection=true

.PHONY: all bridge rmmci dbsq util datatypes clustering convert typerel richtext cloudcontext transactions
all:
	$(ant) run.all

#ok
bridge: dependencies
	$(ant) run.bridge | tee work/bridge.log


#many failures. rmmci sucks / is unmaintainable
rmmci:  dependencies
	$(ant) run.bridge.rmmci | tee work/rmmci.log

#ok
richtext:
	$(ant) run.richtext | tee work/richtext.log

#ok
dbsq:
	$(ant) run.dbsq | tee work/dbsq.log

#ok
util:
	$(ant) run.util | tee work/util.log

#ok
datatypes: dependencies
	$(ant) run.datatypes | tee work/datatypes.log

#
clustering:
	$(ant) run.clustering | tee work/clustering.log

#ok
cloudcontext:
	$(ant) run.cloudcontext | tee work/cloudcontext.log

#ok
convert: dependencies
	$(ant) run.convertimage | tee work/convertimage.log

#ok
typerel:
	$(ant) run.typerel | tee work/typerel.log

# not yet checked in
transactions:
	$(ant) run.transactions | tee work/transactions.log


dependencies:
	mvn dependency:copy-dependencies

.PHONY: compile
compile:
	mvn compile jar:jar
