export JAVA_HOME=/opt/jdk15
ant=ant -quiet -listener org.apache.tools.ant.listener.Log4jListener -lib lib:. -Dnoconnection=true
#ant=ant -lib lib:. -Dnoconnection=true

.PHONY: all bridge rmmci dbsq util clustering convert typerel richtext cloudcontext transactions
all:
	$(ant) run.all
bridge:
	$(ant) run.bridge | tee work/bridge.log

richtext:
	$(ant) run.richtext
rmmci:
	$(ant) run.bridge.rmmci
dbsq:
	$(ant) run.dbsq
util:
	$(ant) run.util
clustering:
	$(ant) run.clustering
cloudcontext:
	$(ant) run.cloudcontext
convert:
	$(ant) run.convertimage
typerel:
	$(ant) run.typerel
transactions:
	$(ant) run.transactions


