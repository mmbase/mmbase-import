<%@page contentType="text/html; charset=utf-8"
%><%!
    String settings_basedir = "";
	int settings_sessiontimeout = 60 * 60 * 24; // 24 hours
	int settings_default_list_pagelength = 50;
	int settings_list_maxpagecount = 10;
	int settings_maxupload = 4 * 1024 * 1024; // 1 MByte max uploadsize
%><%
	settings_basedir = application.getRealPath("/mmapps/editwizard/data");
%>
