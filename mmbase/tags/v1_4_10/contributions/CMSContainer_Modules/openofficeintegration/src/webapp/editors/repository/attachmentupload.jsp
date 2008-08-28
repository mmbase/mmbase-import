<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<%@page import="com.finalist.cmsc.openoffice.service.OODocUploadUtil,com.finalist.cmsc.repository.RepositoryUtil"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:cloud jspvar="cloud" >
<%   // retrieve list op node id's from either the recent upload
    // or from the request url to enable a return url
    // TODO move this to a struts action there are some issue with HttpUpload
    // in combination with struts which have to be investigated first
    if ("post".equalsIgnoreCase(request.getMethod())) {
		String dir = pageContext.getServletContext().getRealPath("/")+"tempDir";
		request.setAttribute("root", RepositoryUtil.getRoot(cloud));
		OODocUploadUtil docUpload = OODocUploadUtil.getInstance(); 
		docUpload.upload(request,dir);
		out.println("root="+RepositoryUtil.getRoot(cloud));
    } 
%>
</mm:cloud>
</mm:content>