<%@page session="false" contentType="text/html;charset=UTF-8"  import="org.w3c.dom.*,org.xml.sax.*,javax.xml.parsers.DocumentBuilder,javax.xml.transform.*,javax.xml.transform.stream.*,javax.xml.transform.dom.*,org.mmbase.cache.xslt.*,java.io.*"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/xml"  postprocessor="none">
<%
 
 StringWriter writer = new StringWriter();
 InputSource source = new InputSource(org.mmbase.bridge.jsp.taglib.ContentTag.class.getResourceAsStream("resources/taglibcontent.xml"));
 DocumentBuilder b =  org.mmbase.util.XMLBasicReader.getDocumentBuilder();
 Document node = b.parse(source);
Transformer serializer = FactoryCache.getCache().getDefaultFactory().newTransformer();
serializer.setOutputProperty(OutputKeys.INDENT, "yes");
serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
serializer.transform(new DOMSource(node),  new StreamResult(writer));
%>
<%=writer.toString() %>
</mm:content>