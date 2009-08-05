Nodegraph
---------

This is a tool to display a time-based history of all builders. 
I have included a few example images in this directory. 

Requirements:

 cewolf - the render engine

  - http://cewolf.sourceforge.net/

 oscache - caching of those rendered images

  - http://www.opensymphony.com/oscache/


Copy the lib/ from cewolf directory to your webapp/WEB-INF/lib and restart
the server.

Add an extra line to your mmbase startup script, to let java think you're
running headless. To display images, java will actually 'talk' to X or your
windows subsystem and crash if this is not running: 

 export JAVA_OPTS='-Djava.awt.headless=true'

You can now view a history of all your builders in mmbase. It is quite crude,
in that it will not show the 'day-of-week' but rather '1,2,3'. 

parameters
----------

timeframe=['week'/'month'/'year']

Specify a timeframe to display; if none is selected a year is assumed. 
(This will actually display 6 years so that the vpro will see its whole 
history. This is hard-coded into the NodeGraphProducer.java)


 timeframe=week  - show a whole week, stepsize = day
 timeframe=month - show a whole month, stepsize = day
 timeframe=year  - show 6 years, stepsize = month

refresh=true

This will refresh the page and all of its images.

example
-------

 http://yourserver.somewhere.com/jsp/nodegraph/graph.jsp?timeframe=week&refresh=true


todo
----

 - the page displayes all of the typedefs, not the active builders, this will
   in some cases display too many builders.

 - timeframe=year will display 6 years of history, this should also be a
   parameter 

 - each image has a weird filename, would be nice to give it the name of the
   builder it is displaying, so that you can search on the page
