<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<!-- ********************************************************************
     $Id: changebars.xsl,v 1.1.1.1 2005-04-21 13:19:11 keesj Exp $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://nwalsh.com/docbook/xsl/ for copyright
     and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->
<xsl:import href="docbook.xsl"/>

<xsl:param name="show.revisionflag" select="'1'"/>

<xsl:template name="system.head.content">
<style type="text/css">
<xsl:text>
div.added    { background-color: #ffff99; }
div.deleted  { text-decoration: line-through;
               background-color: #FF7F7F; }
div.changed  { background-color: #99ff99; }
div.off      {  }

span.added   { background-color: #ffff99; }
span.deleted { text-decoration: line-through;
               background-color: #FF7F7F; }
span.changed { background-color: #99ff99; }
span.off     {  }
</xsl:text>
</style>
</xsl:template>

<xsl:template match="*[@revisionflag]">
  <xsl:choose>
    <xsl:when test="local-name(.) = 'para'
                    or local-name(.) = 'simpara'
                    or local-name(.) = 'formalpara'
                    or local-name(.) = 'section'
                    or local-name(.) = 'sect1'
                    or local-name(.) = 'sect2'
                    or local-name(.) = 'sect3'
                    or local-name(.) = 'sect4'
                    or local-name(.) = 'sect5'
                    or local-name(.) = 'chapter'
                    or local-name(.) = 'preface'
                    or local-name(.) = 'itemizedlist'
                    or local-name(.) = 'varlistentry'
                    or local-name(.) = 'glossary'
                    or local-name(.) = 'bibliography'
                    or local-name(.) = 'index'
                    or local-name(.) = 'appendix'">
      <div class='{@revisionflag}'>
	<xsl:apply-imports/>
      </div>
    </xsl:when>
    <xsl:when test="local-name(.) = 'phrase'
                    or local-name(.) = 'ulink'
                    or local-name(.) = 'link'
                    or local-name(.) = 'filename'
                    or local-name(.) = 'literal'
                    or local-name(.) = 'member'
                    or local-name(.) = 'glossterm'
                    or local-name(.) = 'sgmltag'
                    or local-name(.) = 'quote'
                    or local-name(.) = 'emphasis'
                    or local-name(.) = 'command'
                    or local-name(.) = 'xref'">
      <span class='{@revisionflag}'>
	<xsl:apply-imports/>
      </span>
    </xsl:when>
    <xsl:when test="local-name(.) = 'listitem'
                    or local-name(.) = 'entry'
                    or local-name(.) = 'title'">
      <!-- nop; these are handled directly in the stylesheet -->
      <xsl:apply-imports/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message>
	<xsl:text>Revisionflag on unexpected element: </xsl:text>
	<xsl:value-of select="local-name(.)"/>
	<xsl:text> (Assuming block)</xsl:text>
      </xsl:message>
      <div class='{@revisionflag}'>
	<xsl:apply-imports/>
      </div>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
