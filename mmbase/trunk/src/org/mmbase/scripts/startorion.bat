# set the orion directory
set basepath=d:\ego\orion

# jump to the correct harddisk
d:

cd %basepath%

java -Dmmbase.config=%basepath%\org\mmbase\config\mysql -Dmmbase.htmlroot=%basepath%\default-web-app\ -jar orion.jar
