call cmd\env-maven.cmd %5
call cmd\env-tomcat.cmd %4
call cmd\env-ant.cmd %3

call cmd\env.cmd %1 %2
start cmd /k cd %1

call cmd\env-tomcat55.cmd %2
start cmd /k cd %4\bin
