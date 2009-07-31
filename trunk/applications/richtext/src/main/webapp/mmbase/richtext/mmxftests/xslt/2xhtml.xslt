<!--                                                                                                                                                                                  
     This is a very common way to override an xslt.

  @version: $Id: 2xhtml.xslt,v 1.2 2007-06-20 14:29:28 michiel Exp $                                                                                                               
-->
<xsl:stylesheet
  xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
  xmlns:node ="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:o="http://www.mmbase.org/xmlns/objects"
  xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns=""
  exclude-result-prefixes="node o mmxf html"
  version = "1.0"
>
  <xsl:import href="mm:xslt/2xhtml.xslt" />

  <xsl:output method="xml" omit-xml-declaration="yes" /><!-- xhtml is a form of xml -->

  

</xsl:stylesheet>

