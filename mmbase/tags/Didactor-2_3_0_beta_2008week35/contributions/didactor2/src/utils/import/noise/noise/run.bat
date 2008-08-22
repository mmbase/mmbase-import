rem RMMCI has to be enabled, for this
rem (a) mmbase-rmmci.jar, mmbase-rmmci-server.jar in lib
rem (b) rmmci.xml in config/modules
set JAVA_HOME=C:\apps\jdk1.4\jdk
set MMBASE_HOME=C:\data\didactor\webapps\didactor\WEB-INF
C:\apps\jdk1.4\jdk\bin\java.exe -classpath ..;%JAVA_HOME%\lib;%MMBASE_HOME%\lib\mmbase.jar;%MMBASE_HOME%\lib\mmbase-rmmci.jar;%MMBASE_HOME%\lib\mmbase-rmmci-server.jar;%MMBASE_HOME%\classes nl.didactor.utils.importer.noise.Importer
