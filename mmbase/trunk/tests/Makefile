export JAVA_HOME=/opt/jdk15
ant=ant -quiet -listener org.apache.tools.ant.listener.Log4jListener -lib lib:. -Dnoconnection=true
#ant=ant -lib lib:. -Dnoconnection=true

.PHONY: all bridge rmmci dbsq util clustering convert typerel richtext cloudcontext transactions
all:
	$(ant) run.all

#failures
bridge:
	$(ant) run.bridge | tee work/bridge.log


#
rmmci:
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


clustering:
	$(ant) run.clustering | tee work/clustering.log

#ok
cloudcontext:
	$(ant) run.cloudcontext | tee work/cloudcontext.log

#failures
convert:
	$(ant) run.convertimage | tee work/convertimage.log

#ok
typerel:
	$(ant) run.typerel | tee work/typerel.log

# not yet checked in
transactions:
	$(ant) run.transactions | tee work/transactions.log


