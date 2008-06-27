echo CATALINA_HOME = %1
set CATALINA_HOME=%1
set TOMCAT_HOME=%1

set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=8000
set JAVA_OPTS=-Xmx256M -Dfile.encoding=utf-8
