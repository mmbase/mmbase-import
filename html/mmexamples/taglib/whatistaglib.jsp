<html>
  <head>
    <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
    <%@ page contentType="text/html;charset=UTF-8" %>
    <title>What is MMBase taglib</title>
  </head>
  <body>
   <mm:timer>
   <mm:import externid="color">green</mm:import><!-- stupid example for xsl arguments -->
   <mm:log>Hello</mm:log>
    <mm:formatter options="color=$color">
      <mm:xslt>
        <!-- based on normal xslt, but a little changed -->
        <xsl:import href="mm:xslt/mmxf2xhtml.xslt" />
        <xsl:param name="color">green</xsl:param>
        <xsl:template match = "section" >
             <xsl:if test="count(ancestor::section)=0"><font color="{$color}"><h1><xsl:value-of select="@title" /></h1></font></xsl:if>
             <xsl:if test="count(ancestor::section)=1"><font color="red"><b><xsl:value-of select="@title" /></b></font></xsl:if>
       <xsl:apply-templates select = "section|p|ul" />
        </xsl:template>        
      </mm:xslt>
      <mmxf>
      <section title="What is MMBase Taglib?">
       <p>
         This question can be split up in two sub-questions.
       </p>
       <section title="What is MMBase?">
         <p>
           I think we can assume that we know what is MMBase, but here are some definitions:
         </p>
         <ul>
           <li>MMBase is an open source Content Managment System</li>
           <li>MMBase is a program that connects a database, which is filled by somebody else, to your webpages</li>
         </ul>  
       </section>
       <section title="What is a taglib?">
         <p>
          A taglib is a library of prefabricated functionality wrapped in an XML syntax, which can
          be used easily in other documents using XML (like) syntaxes like HTML.
         </p>
         <p>
          This is what we need, because we need to `enrich' our HTML with syntax to import the database data.
         </p>
         <p>
          The MMBase taglib is based on the MMBase 'bridge' (MMCI). This is java interface, which
          provides a clear and relatively simple way to use MMBase.
         </p>
         <p>
          Taglibs are very much associated with Java Server Pages (JSP)
         </p>
        </section>
      </section>
      </mmxf>
    </mm:formatter>
    </mm:timer>
    <hr />
    <a href="<mm:url page="showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
    <a href="http://java.sun.com/products/jsp/tags/11/tags11.html">JSP-syntax</a><br />
    <a href="<mm:url page="index.jsp" />">Taglib examples</a><br />
    <a href="<mm:url page="/mmdocs/mmbase-taglib.html" />">Taglib documentation</a><br />

  </body>
</html>

