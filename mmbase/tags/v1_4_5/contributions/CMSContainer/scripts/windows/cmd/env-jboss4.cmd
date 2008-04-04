echo JBOSS_HOME = %1
set JBOSS_HOME=%1

set JAVA_OPTS=-Xmx256M -Dfile.encoding=utf-8 -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n
