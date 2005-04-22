Document: installation notes for the NOISe 2 Didactor importer
Author: H. Hangyi
Date: April 21, 2004

The NOISe 2 Didactor uploads a file from a remote installation to Didactor via RMMCI.

The utility consist of the following directories and files:
1.config: the rmmci config file (for Didactor installation)
2.lib: the rmmci jars (for Didactor installation)
3.java: the java classes for the remote installation
4.noise: example configuration and input file for the remote installation (and an example run.bat for Windows systems)

On Didactor RMMCI has to be installed and configured:
1. the directory WEB-INF/lib should contain mmbase-rmmci.jar and mmbase-rmmci-server.jar
2. the directory WEB-INF/config/modules should contain rmmci.xml (note: the bindname should be didactor)
3. add the correct ip address for your host to the file  \WEB-INF\config\modules\mmbaseroot.xml

On the remote installation:
1. the java package nl.didactor.utils.importer.noise should be available as a lib or as java classes
2. the directory from where the import runs should contain config.ini
3. the directory from where the import runs should contain the data file(s)

The importer can be started on the remote installation with:
java nl.didactor.utils.importer.noise.Importer

If necessary the classpath parameter can be added in the following way
set JAVA_HOME=C:\apps\jdk1.4\jdk
set MMBASE_HOME=C:\data\didactor\webapps\didactor\WEB-INF
java -classpath ..;%JAVA_HOME%\lib;%MMBASE_HOME%\lib\mmbase.jar;%MMBASE_HOME%\classes nl.didactor.utils.importer.noise.Importer