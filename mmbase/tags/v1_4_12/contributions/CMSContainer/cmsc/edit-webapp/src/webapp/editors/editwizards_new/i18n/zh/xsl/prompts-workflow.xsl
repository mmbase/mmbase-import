<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:variable name="REASON-WORKFLOW">该对象已经处在被接受的工作流中,您没有权限发布.</xsl:variable>
  <xsl:variable name="REASON-PUBLISH">对象发布中,并且将在发布完成前保持锁定状态.</xsl:variable>

  <xsl:variable name="tooltip_finish">保存所有修改.</xsl:variable>
  <xsl:variable name="tooltip_no_finish">
    不能保存修改,请检查您填写的内容
  </xsl:variable>

  <xsl:variable name="tooltip_accept">接受所有修改.</xsl:variable>
  <xsl:variable name="tooltip_no_accept">
    不能接受修改,请检查您填写的内容
  </xsl:variable>
  <xsl:variable name="tooltip_reject">退回.</xsl:variable>
  <xsl:variable name="tooltip_no_reject">
    不能退回修改,请检查您填写的内容
  </xsl:variable>

  <xsl:variable name="tooltip_publish">发布所有修改.</xsl:variable>
  <xsl:variable name="tooltip_no_publish">
    不能发布修改,请检查您填写的内容
  </xsl:variable>

  <xsl:template name="prompt_finish">
    完成
  </xsl:template>

  <xsl:template name="prompt_accept">
    接受
  </xsl:template>

  <xsl:template name="prompt_reject">
    退回
  </xsl:template>
  
  <xsl:template name="prompt_publish">
    发布
  </xsl:template>
</xsl:stylesheet>
