<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<jsp:useBean id="search" class="net.sf.mmapps.applications.htdigsearch.HTDigBean" scope="page"/>
<jsp:setProperty name="search" property="*" />

<mm:import externid="keywords"/>
<mm:import externid="restrict"/>
<form id="searchform">
 <input type="text" name="keywords" value="<mm:write referid="keywords"/>"/>
 <input type="submit" name="zoeken" value="zoeken"/>
 <select name="restrict">
   <option value="">full site</option>
   <option value="mmdocs" <mm:compare referid="restrict" value="mmdocs">selected="true"</mm:compare>>in documentation</option>
   <option value="mmdocs/reference/taglib/" <mm:compare referid="restrict" value="mmdocs/reference/taglib/">selected="true"</mm:compare>>in taglib documentation</option>
   <option value="lists.mmbase.org" <mm:compare referid="restrict" value="lists.mmbase.org">selected="true"</mm:compare>>mailing lists</option>
 </select>
</form>
<mm:present referid="keywords">
 <mm:isnotempty referid="keywords">
<mm:formatter>
	<mm:xslt>
  <xsl:output method="html" encoding="ISO-8859-1"/>
  <xsl:template match="htdig">
    <xsl:apply-templates select="search"/>
    <ul>
      <xsl:apply-templates select="result"/>
    </ul>
  </xsl:template>
  <xsl:template match="result">
    <li>
      <div class="result">
        <div class="score">score: <xsl:value-of select="percent"/> %</div>
        <a href="{url}">
          <xsl:value-of select="title"/>
        </a>
        <br/>
        <xsl:value-of select="excerpt" disable-output-escaping="yes"/>
        <br/>
      </div>
    </li>
  </xsl:template>

  <xsl:template match="search">
    <div class="documentXYZ">Documents <xsl:value-of select="firstdisplayed"/> - <xsl:value-of select="lastdisplayed"/> of <xsl:value-of select="matches"/> </div>
    <xsl:apply-templates select="pagelist"/>
  </xsl:template>

  <xsl:template match="pagelist">
    <table>
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
