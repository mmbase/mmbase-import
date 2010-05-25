# This file just is for me to remember how to start a standalone hsqldb

# Actually the complete recipe is:

# - Start 3 terminals
# - In terminal 1 type: make
# - In terminal 2 type: mvn jetty:run -DjdbcUrl=jdbc:hsqldb:hsql://localhost/mmbase
# - In terminal 3 type: mvn jetty:run -DjdbcUrl=jdbc:hsqldb:hsql://localhost/mmbase -Djetty.port=8081 -Dmmbaseroot.machinename=webapp2

# And then you have the 'example webapp' running in a mini mmbase cluster.


data/mmbase.lck:
	java org.hsqldb.Server -database.0 data/mmbase -dbname.0 mmbase

