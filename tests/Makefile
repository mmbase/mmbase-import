
ant=ant -quiet -listener org.apache.tools.ant.listener.Log4jListener -lib lib:.

.PHONY: all bridge rmmci dbsq util
all:
	$(ant) run.all
bridge:
	$(ant) run.bridge
rmmci:
	$(ant) run.bridge.rmmci
dbsq:
	$(ant) run.dbsq
util:
	$(ant) run.util
