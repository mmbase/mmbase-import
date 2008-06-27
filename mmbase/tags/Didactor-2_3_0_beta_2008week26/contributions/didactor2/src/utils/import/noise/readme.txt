Document: installation notes for the NOISe 2 Didactor importer
Author: H. Hangyi
Date: September 11, 2006

The utility can work both with a local cload and a remote cloud (rmi)

The utility consist of the following directories and files:
1.config: the rmmci config file (for Didactor installation)
2.lib: the rmmci jars (for Didactor installation)
3.java: the java classes for the remote installation
4.noise: example configuration and input file for the remote installation (and an example run.bat for Windows systems)

USING NOISE IMPORTER WITH A LOCAL CLOUD (THE FILES TO BE IMPORTERD HAVE TO BE ON THE SERVER)

1. Place the compiled classes in your WEB-INF/classes directory
2. Place the /noise directory with the ini and data files somewhere on the server.
3. Add the the noise-importer.jsp to your webapp and set the path to the data files correctly.
4. Call noise-importer.jsp from a browser
5. See the log-file for the of the import

USING NOISE IMPORTER FROM A REMOTE COMPUTER

The NOISe 2 Didactor uploads a file from a remote computer to Didactor via RMMCI.

This means that on the remote computer:

1. A Java runtime environment has to be installed
2. the MMBase library mmbase.jar should be available
3. the java package nl.didactor.utils.importer.noise should be available as a lib or as java classes
4. the directory from where the import runs should contain config.ini
5. the directory from where the import runs should contain the data file(s)

The importer can be started on the remote installation from a prompt / batch file with:
   
   java nl.didactor.utils.importer.noise.Importer

The Importer will output to screen: when it start and when it finishes. (no idea whether this still works now i replaced System.out.println by log.info)

After the import the student, classes and workgroups can be inspected.

If the application startpage is http://localhost:8080/didactor/index.jsp, then the url to inspect
the classes is http://localhost:8080/didactor/classes

The newly imported student can be given the role "student" by using the url: http://localhost:8080/didactor/classes/giverole.jsp.
Before you can use this url the role student should be given the object alias "studentrole".

If necessary the classpath parameter can be added in the following way
set JAVA_HOME=C:\apps\jdk1.4\jdk
set MMBASE_HOME=C:\data\didactor\webapps\didactor\WEB-INF
java -classpath ..;%JAVA_HOME%\lib;%MMBASE_HOME%\lib\mmbase.jar;%MMBASE_HOME%\classes nl.didactor.utils.importer.noise.Importer

On Didactor on which the data has to be imported RMMCI has to be installed and configured:
1. the directory WEB-INF/lib should contain mmbase-rmmci.jar and mmbase-rmmci-server.jar
2. the directory WEB-INF/config/modules should contain rmmci.xml (note: the bindname should be didactor)
3. add the correct ip address for your host to the file  \WEB-INF\config\modules\mmbaseroot.xml
