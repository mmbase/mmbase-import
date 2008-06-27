<%@page session="false" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mn" 
%><mn:content type="text/html" language="en" expires="600" postprocessor="none">
<html>
<head>
  <title><mn:write id="title" value="MMBase Taglib 2.0" /></title>
  <link href="../style.css" rel="stylesheet" type="text/css"/>
  <style>
    table.toc {
    position: absolute;
    top: 0px;
    left: 0px;
    height: 50px;
    overflow: auto;
    background-color: #eee;
}
 div.content {
    position: absolute;
    margin-left: auto;
    margin-right: auto;
    background: #eee;
    visibility: hidden;
    top: 50px;
    height: 100%;
}
 div.toc span {
   margin-left: 3ex;
   }
  </style>
</head>
<body>
  <mn:formatter  escape="none">
    <mn:xslt>
      <xsl:import href="../1.1/xslt/slides.xslt" />  
      <!-- support blink for the sake of the argument -->
      <xsl:template match="blink|pre"><xsl:copy-of select="." /></xsl:template>

    </mn:xslt>
    <mmxf>
      <section>
        <h><mn:write referid="title" /></h>
        <section>
          <h>Integration with JSTL/EL/JSPX</h>
          <p>
            Taglib variables are EL compatible now, which means that mmbase taglib can work much
            better together with JSTL tags.
          </p>
          <p>
            EL, together tags like mm:link, and the implicit variable _ and _node make it easier to
            make your source valid XML, which makes it possible to use JSPX to produce XHTML.
          </p>
        </section>
        <section>
          <h>Missing tags</h>
          <p>
            has-tags.
          </p>
        </section>
        <section>
          <h>On-the-fly escapers</h>
          <p>
          <pre>
<![CDATA[
            <mm:escaper id="replace" type="regexps">
                <mm:param name="patterns">
                  <mm:param name="(?i)(.*)($search)(.*)"
                            value="$$1&lt;span style='background-color: yellow; color:black;' id='searchresult'&gt;$$2&lt;/span&gt;$$3" />
                </mm:param>
                <mm:param name="mode" value="XMLTEXT" />
              </mm:escaper>
]]>
          </pre>
          </p>
        </section>
        <section>
          <h>EditTag</h>
          <p>
            To generate task-editors by taglib.
          </p>
        </section>
      </section>
    </mmxf>
  </mn:formatter>
</body>
</html>
</mn:content>
