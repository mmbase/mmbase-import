<%@ page language="java" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<jsp:useBean id="search" class="net.sf.mmapps.applications.htdigsearch.HTDigBean" scope="page" />
<jsp:setProperty name="search" property="*" />

<mm:import externid="keywords" />
<mm:import externid="sort">score</mm:import>
<mm:import externid="restrict" />
<form id="searchform" action="<mm:url />">
 <input type="hidden" name="exclude" value="testing" />
 <input type="text" name="keywords" value="<mm:write referid="keywords"/>" />
 <input type="submit" name="zoeken" value="Search" />
 <select name="restrict" onChange="document.forms.searchform.submit();">
   <option value="">all indexes</option>
   <option value="/?" <mm:compare referid="restrict" value="/?">selected="true"</mm:compare>>this website only</option>
   <option value="mmdocs" <mm:compare referid="restrict" value="mmdocs">selected="true"</mm:compare>>in documentation</option>
   <option value="mmdocs/reference/taglib/" <mm:compare referid="restrict" value="mmdocs/reference/taglib/">selected="true"</mm:compare>>in taglib documentation</option>
   <option value="lists.mmbase.org" <mm:compare referid="restrict" value="lists.mmbase.org">selected="true"</mm:compare>>mailing lists</option>
 </select><br />
 orderby
 <mm:import id="sortList" vartype="list">score,time,date,title,revscore,revtime,revdate,revtitle</mm:import>
 <select name="sort" onChange="document.forms.searchform.submit();">
  <mm:stringlist referid="sortList">
   <option value="<mm:write/>" <mm:compare referid2="sort">selected="selected"</mm:compare>><mm:write/></option>
  </mm:stringlist>
 </select>
</form>

<mm:present referid="keywords">
 <mm:isnotempty referid="keywords">
<mm:formatter>
	<mm:xslt>
  <xsl:output method="html" encoding="ISO-8859-1"/>
  <xsl:template match="htdig">
    <div id="results">
    <xsl:apply-templates select="search"/>
     <ul>
      <xsl:apply-templates select="result"/>
    </ul>
    </div>
  </xsl:template>
  <xsl:template match="result">
    <li class="result">
        <a href="{url}">
          <xsl:value-of select="title" />
        </a>
        <xsl:value-of select="excerpt" disable-output-escaping="yes" />
        <div class="resultcomment"><xsl:value-of select="percent" /> % - 
        <xsl:value-of select="modified" /> -
        <xsl:value-of select="url" /> - <xsl:value-of select="sizek" />k</div>
    </li>
  </xsl:template>

  <xsl:template match="search">
    <div class="documentXYZ">
	  Documents <xsl:value-of select="firstdisplayed"/> - <xsl:value-of select="lastdisplayed"/> of <xsl:value-of select="matches"/>
	  <xsl:apply-templates select="pagelist"/>
	</div>
  </xsl:template>

  <xsl:template match="pagelist">
    <table border="0" cellspacing="0" cellpadding="4">
      <tr>
        <xsl:for-each select="page">
          <td>
            <a>
              <xsl:attribute name="href">
                <xsl:apply-templates select="a" mode="pagelist"/>
              </xsl:attribute>
              <xsl:value-of select="position()"/>
            </a>
          </td>
        </xsl:for-each>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="a" mode="pagelist">
    <xsl:value-of select="translate(@href,';','&amp;')"/>
  </xsl:template>
	</mm:xslt>
  <jsp:getProperty name="search" property="result" />
</mm:formatter>
 </mm:isnotempty>
</mm:present>
