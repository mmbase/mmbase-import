<mm:formatter>
<mm:field name="title" />
<mm:xslt>
  <xsl:template match="field" >
    <h1>
    <xsl:value-of select="substring(., 0, 5)" />
    ...</h1>
    </xsl:template>
</mm:xslt>
</mm:formatter>

