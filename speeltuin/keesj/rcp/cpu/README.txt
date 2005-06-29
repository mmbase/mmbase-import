cpustats.jsp monitors the cpu load of a linux machine by readin the /proc file
system. it creates a thread in the jsp and keeps monitoring the load. When
called the jsp produces xml.

This xml can be read using the provided gui

When using java 1.5 this is not the best way to monitor cpu 
you should use JMX for that
