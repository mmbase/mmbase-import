<%@page contentType="text/html; charset=utf-8"
%><%!
    String settings_basedir = null;
	int settings_sessiontimeout = 60 * 60 * 24; // 24 hours
	int settings_default_list_pagelength = 50;
	int settings_list_maxpagecount = 10;
	int settings_maxupload = 4 * 1024 * 1024; // 1 MByte max uploadsize
%><%
    if(settings_basedir == null) {
        // get the requested jsp...
        java.io.File f = new java.io.File(request.getRequestURI());
        // remove the jsp, and add the 'data' dir to it....
        String webDataDir = f.getParent() + java.io.File.separator + "data";
        // ask how this dir is absolute on the server... 
        String diskDataDir = getServletConfig().getServletContext().getRealPath(webDataDir);
        f = new java.io.File(diskDataDir);
        settings_basedir = f.getAbsolutePath();
    }
%>
