<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:variable name="REASON-WORKFLOW">Dit object staat in workflow en is goedgekeurd. U heeft geen publiceer rechten op de rubriek waartoe dit object behoort.</xsl:variable>
  <xsl:variable name="REASON-PUBLISH">Dit object staat klaar om gepubliceerd te worden. Zolang dit niet gebeurd is kan dit object niet bewerkt worden.</xsl:variable>

  <xsl:variable name="tooltip_finish">Voltooi het object en bied het ter goedkeuring aan</xsl:variable>
  <xsl:variable name="tooltip_no_finish">
  	De wijzigingen kunnen niet bewaard worden, omdat nog niet alle data correct is ingevuld.
  </xsl:variable>

  <xsl:variable name="tooltip_accept">Keur het object goed</xsl:variable>

  <xsl:variable name="tooltip_no_accept">
  	De wijzigingen kunnen niet goedgekeurd worden, omdat nog niet alle data correct is ingevuld.
  </xsl:variable>

  <xsl:variable name="tooltip_reject">Afwijzen.</xsl:variable>
  <xsl:variable name="tooltip_no_reject">
  	De wijzigingen kunnen niet afgewezen worden, omdat nog niet alle data correct is ingevuld.
  </xsl:variable>

  <xsl:variable name="tooltip_publish">Publiceer het object</xsl:variable>
  <xsl:variable name="tooltip_no_publish">
  	De wijzigingen kunnen niet gepubliceerd worden, omdat nog niet alle data correct is ingevuld.
  </xsl:variable>

  <xsl:template name="prompt_finish">
    Voltooien
  </xsl:template>

  <xsl:template name="prompt_accept">
    Goedkeuren
  </xsl:template>

  <xsl:template name="prompt_reject">
    Afwijzen
  </xsl:template>
  
  <xsl:template name="prompt_publish">
    Publiceren
  </xsl:template>
</xsl:stylesheet>
