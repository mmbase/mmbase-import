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
 </select>
</form>
<mm:present referid="keywords">
 <mm:isnotempty referid="keywords">
<mm:formatter>
	<mm:xslt>
	  <xsl:output method="html" encoding="ISO-8859-1"/>
		<xsl:template match = "htdig" >
			<xsl:apply-templates select="search"/>
			<ul>
			<xsl:apply-templates select="result"/>
			</ul>
		</xsl:template>

		<xsl:template match = "result" >
			<li>
			score: <xsl:value-of select="percent" /> % 
			<a href="{url}"><xsl:value-of select="title" /></a><br/>
			<xsl:value-of select="excerpt" disable-output-escaping="yes"/><br/>
			</li>
		</xsl:template>

		<xsl:template match = "search" >
			Documents <xsl:value-of select="firstdisplayed"/> - <xsl:value-of select="lastdisplayed"/> of <xsl:value-of select="matches" /> <br/>
			<xsl:apply-templates select="pagelist"/>
		</xsl:template>

		<xsl:template match="pagelist">
			<table>
			<tr>
			<xsl:apply-templates select="page"/>
			</tr>
			</table>
		</xsl:template>
		<xsl:template match="page">
			<td>
			  <xsl:apply-templates select="a"/>
			</td>
		</xsl:template>
		<xsl:template match="a">
			  <a href="{translate(@href,';','&amp;')}"><xsl:value-of select="."/></a>
		</xsl:template>
	</mm:xslt>
  <jsp:getProperty name="search" property="result" />
</mm:formatter>
 </mm:isnotempty>
</mm:present>
