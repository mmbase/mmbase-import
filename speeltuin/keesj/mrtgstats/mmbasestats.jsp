<%@page import="java.io.*" %><%@
page import="java.net.*" %><%@
page import="org.mmbase.cache.*" %><%@
page import="org.mmbase.module.*" %><%@
page import="org.mmbase.module.core.*" %><%!
    /**
     * the time at witch mmbase was started. For the moment this is the time the class was loaded;
     * at time of writing we are is a feature freze. since this is actualy
     * a new feature we will have to keep this data here for the moment
     * a later time we should move this code to the org.mmbase.mocules.core.MMBase class
     **/
    private static int mmbaseStartTime;
    //remove this once mmbase has this data avaiable
    static {
        mmbaseStartTime = (int)(System.currentTimeMillis()/1000);
    }

    /**
     * print the memory stats
     * output:
     * <ul>
     *   <li>current memory used by application</li>
     *   <li>memorry used by the java virtual machine</li>
     *</ul>
     **/
    public void getMemoryStats(PrintWriter out){
        Runtime runtime = Runtime.getRuntime();
        long freeMemory =runtime.freeMemory();
        long maxMemory =runtime.totalMemory();
        
        //avaiable in jvm
        out.println("" + (maxMemory - freeMemory));
        
        //used
        out.println("" + maxMemory);
    }
    
    /**
     * print carche stats
     * @param type the type of cache requested
     * output:
     * <ul>
     *   <li>current cache hits count</li>
     *   <li>total cache requests</li>
     *</ul>
     **/
    public void getCacheStats(PrintWriter out,String type){
        Cache cache = Cache.getCache(type);
        out.println(cache.getHits());
        out.println((cache.getHits() + cache.getMisses()));
    }
%><%
  response.setContentType("text/plain");
  MMBase mmbase = (MMBase)Module.getModule("MMBASEROOT");
  String host = mmbase.getMachineName();
  String action=request.getParameter("action");
  if (action == null){
	  action="memory";
	}

  if (action.equals("memory")){
    getMemoryStats(new PrintWriter(out));
  } else if (action.equals("cache")){
    String cacheType = request.getParameter("cachetype");
    if (cacheType == null) {
		   cacheType="Nodes";
		}
    getCacheStats(new PrintWriter(out),cacheType);
  }
        
  //now add the uptime ans machine name (required by mrtg)
  int timeDiff =  (int)((System.currentTimeMillis()/1000) - mmbaseStartTime);
        
  int days = timeDiff / (60 * 60 * 24);
  int hours =(timeDiff / (60  * 60)) % 24;
  int minutes = (timeDiff / 60) % 60 ;
  int seconds = timeDiff % 60;
  out.println("" + days +" days " + hours +":" + minutes +":" + seconds +" (hours:minutes:seconds)");
  out.println(host);
%>
