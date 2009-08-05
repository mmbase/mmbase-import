<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@ taglib uri="http://www.opensymphony.com/oscache" prefix="os"
%><%@ include file="nocache.jsp"
%><mm:import id="language">nl</mm:import
><% boolean needsRefresh = false;
    int cacheperiod = 36000;
%></mm:present><mm:import id="jsps">/mmbase/edit/wizard/jsp/</mm:import
>
