<?xml version="1.0"?>
<xsl:stylesheet id="xml2html" 
  version="1.1"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="org.apache.xalan.xslt.extensions.Redirect"
  extension-element-prefixes="xalan"
 >

  <!-- 
       xalan extension used for xalan:write.

       According to xslt 1.1 you could simply use xsl:document, but xalan does not
       support this. Therefore in this stylesheet xsl:document and xalan:write are 
       used inside each other. This seems to work with xsltproc and xalan-j..     
   -->

  <xsl:output method="xml"
    version="1.0"
    encoding="UTF-8"
    omit-xml-declaration="yes"
    doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
    indent="yes"
    />

<xsl:param name="basedir"></xsl:param>
<xsl:param name="files">taglib</xsl:param>
<xsl:variable name="basedirfiles"><xsl:value-of select="$basedir" />/<xsl:value-of select="$files" /></xsl:variable><!-- make sure this directory exists! -->
    
<!-- some configuration -->
<xsl:variable name="extendscolor">blue</xsl:variable>
<xsl:variable name="attrcolor">green</xsl:variable>
<xsl:variable name="reqcolor">red</xsl:variable>

<!-- main entry point -->
<xsl:template match="taglib">
  <xsl:apply-templates select="tagtypes/type" />

  <!-- create a seperate file for every tag -->
  <xsl:apply-templates select="tag|taginterface" mode="file" />
    <!-- create a toc file -->
    <xsl:document href="{$basedirfiles}/toc.html">
      <xalan:write file="{$basedirfiles}/toc.html"><!-- xsl:document not supported by xalan.. Sigh..-->
      <html>
        <head>
          <title>MMBase taglib - Table Of Contents</title>
        </head>
        <body 
          bgcolor="#FFFFFF" text="#336699" link="#336699" vlink="#336699" alink="#336699">
          <h1>MMBase taglib <xsl:value-of select="/taglib/tlibversion" /> documentation</h1>
          <xsl:apply-templates select="info" />
          <xsl:for-each select="/taglib/tagtypes/type">
            <a href="mmbase-taglib-{@name}.html"><xsl:value-of select="description" /></a><br />
          </xsl:for-each>
          <hr />
          All 'tags' in seperate files (these are small jsp's, probably will be possible to add working examples): <br />
          groups: <br />
          <xsl:apply-templates select="taginterface" mode="toc" >
            <xsl:sort select="name" />
            <xsl:with-param name="file"  select="true()" />
          </xsl:apply-templates><br />
          tags: <br />
          <xsl:apply-templates select="tag" mode="toc" >
            <xsl:sort select="name" />
            <xsl:with-param name="file"  select="true()" />
          </xsl:apply-templates><br />
        </body>
      </html>
    </xalan:write>
  </xsl:document>
</xsl:template>

<!-- The several 'types' of tags -->
<xsl:template match="type">

  <!-- for backwards compatibility, the 'all' overview is written to stdout -->
  <xsl:if test="@name='all'">
    <xsl:apply-templates select="/taglib" mode="main">
      <xsl:with-param name="info" select="/taglib/info|info" />
      <xsl:with-param name="type" select="@name" />
    </xsl:apply-templates>
  </xsl:if>
  <!-- and overview files are generated for every type -->
  <xsl:document href="{$basedirfiles}/mmbase-taglib-{@name}.html">
    <xalan:write file="{$basedirfiles}/mmbase-taglib-{@name}.html">
     <xsl:apply-templates select="/taglib" mode="main">
        <xsl:with-param name="info" select="info" />
        <xsl:with-param name="type" select="@name" />
     </xsl:apply-templates>
   </xalan:write>
  </xsl:document>
</xsl:template>

<!-- Generate a list of tag documentation, with toc and info on top -->
<xsl:template match="taglib" mode="main">
  <xsl:param name="info" />
  <xsl:param name="type" />
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <title>MMBase taglib <xsl:value-of select="tlibversion" /> documentation</title>
      <xsl:if test="@author"><meta name="Author" value="{@author}"/></xsl:if>
    </head>
      <body 
        bgcolor="#FFFFFF" text="#336699" link="#336699" vlink="#336699" alink="#336699">
        <h1>MMBase taglib <xsl:value-of select="tlibversion" /> documentation</h1>
        <h2><xsl:value-of select="tagtypes/type[@name=$type]/description" /></h2>
      <table width="100%" cellpadding="5">
        <tr>
          <td width="30"></td>
	    <td>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
          <td width="30"></td>
          </tr>
        <tr>
          <td></td>
          <td><xsl:apply-templates select="$info/*"/></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <a name="toc"/>			
            <xsl:apply-templates select="taginterface[contains(type, $type) or $type='all']" mode="toc" >
                <xsl:sort select="name" />
            </xsl:apply-templates><br />
          </td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <a name="toc"/>			
            <xsl:apply-templates select="tag[contains(type, $type) or $type='all']" mode="toc" >
                <xsl:sort select="name" />
            </xsl:apply-templates><br />
          </td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <a href="#docinfo">info about the syntax of this document</a>
          </td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <xsl:apply-templates select="taginterface[contains(type, $type) or $type='all']" mode="full"><xsl:sort select="name" /></xsl:apply-templates>
            <xsl:apply-templates select="tag[contains(type, $type) or $type='all']" mode="full"><xsl:sort select="name" /></xsl:apply-templates>
          </td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
          <td></td>
        </tr>
         <tr>
          <td></td>
          <td>
            <a name="docinfo"/>
            <p>
            This document lists the current tags implemented for MMBase (version <xsl:value-of
            select="/taglib/tlibversion" />)
	    </p>
            <p>
                Attributes in <font color="{$reqcolor}"><xsl:value-of select="$reqcolor" /></font> are
                required.
	    </p>
              <p>
                <font color="{$extendscolor}"><xsl:value-of select="$extendscolor" /></font> entries
                are no tags, but describe a group of tags. Tags can belong to several groups.
              </p>
	    <p>
                If a tag definition contains a body section this means that the
                tag might do something with the content of the body.
              </p>
          </td>
          <td></td>
        </tr>
      </table>
    </body>
  </html>
</xsl:template>


<!-- To generate the right URL to a certain tag documentation it must be determined if this is not in the same document.
     If not, than the link must be to the seperate file. If so, then simply can be linked with #
     This template only generates the URL.
-->
<xsl:template name="tagref">
  <xsl:param name="file" select="false()" />
  <xsl:param name="tag" />
  <xsl:param name="attribute" />   
  <xsl:if test="$file"><xsl:value-of select="$tag" />.jsp</xsl:if>
  <xsl:if test="$attribute or not($file)">#<xsl:value-of select="$tag" /></xsl:if>
  <xsl:if test="$attribute">.<xsl:value-of select="$attribute" /></xsl:if>
</xsl:template>

<!-- Generates an entry for table of all tags or taginterfaces -->
<xsl:template match="tag|taginterface" mode="toc">
  <xsl:param name="file" select="false()" />
  <a>
    <xsl:attribute name="href">
      <xsl:call-template name="tagref">
        <xsl:with-param name="file" select="$file" /><xsl:with-param name="tag"  select="name"  />
      </xsl:call-template>      
    </xsl:attribute>
   <xsl:if test="name()='taginterface'"><font color="{$extendscolor}"><xsl:value-of select="name" /></font></xsl:if>
   <xsl:if test="name()='tag'"><xsl:value-of select="name" /></xsl:if>
  </a>
  <xsl:if test="position() != last()"> | </xsl:if>
</xsl:template>

<!-- -->
<xsl:template match="tag|taginterface" mode="tocext">
  <xsl:param name="testlast" select="false()" />
  <xsl:param name="file" select="false()" />
  <xsl:apply-templates select="/taglib/*[name()='tag' or name()='taginterface']/extends[.=current()/name]/parent::*" mode="tocext">
    <xsl:with-param name="file" select="$file" />
  </xsl:apply-templates>
   <xsl:if test="name()='tag'">
     <a>
       <xsl:attribute name="href">
         <xsl:call-template name="tagref">
           <xsl:with-param name="file" select="$file" /><xsl:with-param name="tag"  select="name"  />
         </xsl:call-template>
       </xsl:attribute>
       <xsl:value-of select="name" />  
     </a>
     <xsl:if test="(position() != last()) or not($testlast)"> | </xsl:if>     
   </xsl:if>
</xsl:template>

<!-- Tags can refer to other tags (and attributes) with a 'see' link. This template handles them. -->
<xsl:template match="see">
  <xsl:param name="file" select="false()" />
  <a>
    <xsl:attribute name="href">
      <xsl:call-template name="tagref">
        <xsl:with-param name="file" select="$file" />
        <xsl:with-param name="tag"  select="@tag"  /><xsl:with-param name="attribute"  select="@attribute" />
      </xsl:call-template>
    </xsl:attribute>
    <xsl:if test="@attribute"><xsl:value-of select="@attribute" /> attribute of </xsl:if><xsl:value-of select="@tag" />
  </a>
  <xsl:if test="position() != last()"> | </xsl:if>
</xsl:template>

<!-- Create a file for a tag -->
<xsl:template match="tag|taginterface" mode="file">
  <xsl:document href="{$basedirfiles}/{name}.jsp">
    <xalan:write file="{$basedirfiles}/{name}.jsp"><!-- xsl:document not supported by xalan-->
  <html>
    <head>
      <title>MMBase taglib - mm:<xsl:value-of select="name" /></title>
    </head>
    <body 
      bgcolor="#FFFFFF" text="#336699" link="#336699" vlink="#336699" alink="#336699">
    <h1><xsl:value-of select="name" /></h1>
    <xsl:apply-templates select="." mode="full">
      <xsl:with-param name="file" select="true()" />
    </xsl:apply-templates>
    </body>
  </html>
  </xalan:write>
  </xsl:document>
</xsl:template>

<!-- A description for one tag -->
<xsl:template match="tag|taginterface" mode="full">
  <xsl:param name="file" select="false()" /><!-- if true, reference to files -->
  <table bgcolor="#eeeeee" width="100%" cellpadding="5">
    <tr>
      <td colspan="2" bgcolor="white" align="right">
        <a name="{name}"/>
        <xsl:if test="$file">
          <a href="toc.html">toc</a>
        </xsl:if>
        <xsl:if test="not($file)">
          <a href="#toc">toc</a>
        </xsl:if>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <xsl:if test="name()='tag'"><b>&lt;mm:<xsl:value-of select="name"/>&gt;</b></xsl:if>
        <xsl:if test="name()='taginterface'"><b><font color="{$extendscolor}">`<xsl:value-of select="name"/>' tags</font></b></xsl:if>
        <br />
        <xsl:apply-templates select="info"/>
      </td>
    </tr>
    <xsl:if test="see">
      <tr>
        <td width="100" valign="top">see also</td>
        <td>
          <xsl:apply-templates select="see" >
            <xsl:with-param name="file" select="$file" />
          </xsl:apply-templates>
        </td>
      </tr>
    </xsl:if>
    <xsl:if test="attribute">
      <tr>
        <td width="100" valign="top">attributes</td>
        <td>
          <ul><xsl:apply-templates select="attribute" mode="full">
          <xsl:with-param name="file" select="$file" />
        </xsl:apply-templates>
        </ul>
        </td>
      </tr>
    </xsl:if>
    
    <xsl:apply-templates select="extends">
      <xsl:with-param name="file" select="$file" />
    </xsl:apply-templates>
      <xsl:if test="xxbodycontent"><!-- ignore the bodycontent, its no use -->
      <tr>
        <td width="100" valign="top">body</td>
        <td>
          <xsl:value-of select="bodycontent"/>
          <br />
          <xsl:apply-templates select="bodycontentinfo"/>
        </td>
      </tr>
    </xsl:if>       
    <xsl:if test="name()='taginterface'">
    <tr>
    <td>tags of this type</td><td>
           <xsl:apply-templates
              select="/taglib/*[name()='tag' or name()='taginterface']/extends[.=current()/name]/parent::*" 
              mode="tocext" >
             <xsl:with-param name="file" select="$file" />
             <xsl:with-param name="testlast" select="true()" />
              <xsl:sort select="name" />
            </xsl:apply-templates>
     </td>
    </tr>  
    </xsl:if>

    <xsl:apply-templates select="example" />
  </table>
</xsl:template>

<!-- Examples -->
<xsl:template match="example">
   <tr>
     <td width="100" valign="top">example</td>
     <td>
        <pre><xsl:value-of select="."/></pre>
     </td>
   </tr>
</xsl:template>

<xsl:template match="extends">
  <xsl:param name="file" select="false()" />
  <tr>
      <td width="100" valign="top"><xsl:if test="/taglib/taginterface/name[.=current()]"><a>
      <xsl:attribute name="href">
        <xsl:call-template name="tagref">
           <xsl:with-param name="file" select="$file" />
           <xsl:with-param name="tag"  select="current()" />
        </xsl:call-template>
      </xsl:attribute>
     <font color="{$extendscolor}"><xsl:value-of select="." /></font></a></xsl:if> attributes</td>
	  <td>
	  <xsl:if test="/taglib/*[name()='tag' or name()='taginterface']/name[.=current()]/parent::*/attribute">
      <ul>   	   
       <xsl:apply-templates select="/taglib/*[starts-with(name(), 'tag')]/name[.=current()]/parent::*/attribute"  mode="extends">
         <xsl:with-param name="file" select="$file" />
       </xsl:apply-templates>
      </ul>
      </xsl:if>
	  </td>
   </tr>
   <xsl:apply-templates select="/taglib/*[name()='tag' or name()='taginterface']/name[.=current()]/parent::*/extends" >
      <xsl:with-param name="file" select="$file" />
   </xsl:apply-templates>
</xsl:template>

<xsl:template match="attribute" mode="extends">
  <xsl:param name="file" select="false()" />
  <li><a>
  <xsl:attribute name="href">
    <xsl:call-template name="tagref">
      <xsl:with-param name="file" select="$file" />
      <xsl:with-param name="tag"  select="parent::*/name" />
      <xsl:with-param name="attribute"  select="name" />
    </xsl:call-template>          
  </xsl:attribute>
  <font color="{$attrcolor}"><xsl:value-of select="name" /></font></a></li>
</xsl:template>

<xsl:template match="attribute" mode="full">
  <li>
    <a name="{parent::*/name}.{name}" />
    <xsl:choose>
      <xsl:when test="requirednote">
        <xsl:if test="requirednote">
          <font color="ff9900"><xsl:apply-templates select="name"/></font>
          <font color="black" size="-1"> (<xsl:value-of select="requirednote"/>)</font>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="required='true'">
            <font color="{$reqcolor}"><xsl:apply-templates select="name"/></font>
          </xsl:when>
          <xsl:otherwise>
            <font color="{$attrcolor}"><xsl:apply-templates select="name"/></font>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>      
    </xsl:choose>
    <br />
    <xsl:if test="info">
      <xsl:apply-templates select="info"/>
    </xsl:if>
    <xsl:if test="possiblevalue">
      <table bgcolor="#99ccff">
        <xsl:apply-templates select="possiblevalue"/>
      </table>
    </xsl:if>
    <xsl:if test="examplevalue">
      <table bgcolor="#99ffff">
        <xsl:apply-templates select="examplevalue"/>
      </table>
    </xsl:if>
  </li>
</xsl:template>

<xsl:template match="possiblevalue">
  <tr>
    <td valign="top"><b><xsl:value-of select="value"/></b></td>
    <td valign="top"><xsl:apply-templates select="info"/></td>
  </tr>
</xsl:template>

<xsl:template match="examplevalue">
  <tr>
    <td valign="top"><b><xsl:value-of select="value"/></b></td>
    <td valign="top"><xsl:apply-templates select="info"/></td>
  </tr>
</xsl:template>

<xsl:template match="info">
  <xsl:apply-templates select="p|text()|em|a" />  
</xsl:template>

<xsl:template match="p|text()|a">
  <xsl:copy-of select="." />
</xsl:template>

<xsl:template match="em">
  <xsl:copy>
    <xsl:apply-templates />
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
