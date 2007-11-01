<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:variable name="REASON-RIGHTS">您没有操作该对象所属频道的权限</xsl:variable>

  <xsl:variable name="prompt_search_link" >搜索</xsl:variable>
  <xsl:variable name="prompt_new_link" >新建</xsl:variable>
  <xsl:variable name="prompt_edit_link" >更新</xsl:variable>

  <xsl:template name="prompt_invalid_list">
    <xsl:param name="minoccurs">0</xsl:param>
    <xsl:param name="maxoccurs">*</xsl:param>
    <xsl:choose>
	    <xsl:when test="$minoccurs = '1' and $minoccurs = $maxoccurs">选择 <xsl:value-of select="$maxoccurs"/> 元素</xsl:when>
	    <xsl:when test="$minoccurs = $maxoccurs">选择 <xsl:value-of select="$maxoccurs"/> 元素</xsl:when>
	    <xsl:when test="not($minoccurs = '0') and not($maxoccurs = '*')">需选择 <xsl:value-of select="minoccurs"/> 到 <xsl:value-of select="maxoccurs"/>个元素</xsl:when>
	    <xsl:when test="not($minoccurs = '0')">至少选择 <xsl:value-of select="minoccurs"/>个元素</xsl:when>
	    <xsl:when test="not($maxoccurs = '*')">最多选择 <xsl:value-of select="maxoccurs"/> 个元素</xsl:when>
    </xsl:choose>
  </xsl:template>
	
  <xsl:template name="prompt_new">新建</xsl:template>

</xsl:stylesheet>
