<xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0" xmlns="http://www.w3.org/TR/xhtml1">

<xsl:strip-space elements="doc chapter section"/>
<xsl:output method="xml" indent="yes"/>

<xsl:template match="doc">
 <html>
   <head>
     <title>
       <xsl:value-of select="title"/>
     </title>
   </head>
   <body bgcolor="#6666AA" text="#0000FF">
     <xsl:apply-templates/>
   </body>
 </html>
</xsl:template>

<xsl:template match="person/name">
  <TABLE BORDER="0">
    <TR>
	<TD BGCOLOR="#000066">
		Name of person
	</TD>
    </TR>
    <TR>
	<TD BGCOLOR="#000088">
    		<xsl:apply-templates/>
	</TD>
    </TR>
  </TABLE>
</xsl:template>


</xsl:stylesheet>
