<?xml version="1.0" encoding="utf-8"?>
<!-- conversion from xhtml to xsl-fo, hk 2004/05/22 -->
<xsl:stylesheet 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:fo="http://www.w3.org/1999/XSL/Format"
   xmlns:html="http://www.w3.org/1999/xhtml"
   version="1.0" >

  <xsl:output encoding="utf-8" method="xml" />

  <xsl:template match="html:html">
    <fo:root>

      <fo:layout-master-set>
        <fo:simple-page-master master-name="seite"
                  margin-top="1cm" 
                  margin-bottom="1cm" 
                  margin-left="2cm" 
                  margin-right="2cm" >
           <fo:region-body region-name="body" 
                  margin-top="1.5cm" margin-bottom="2.5cm" />
           <fo:region-before region-name="top" extent="2.5cm"/>
           <fo:region-after region-name="bottom" extent="1.5cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="seite" >
        <fo:static-content flow-name="top" >
            <fo:block font-size="18pt" 
              font-family="sans-serif" 
              line-height="24pt"
              space-after.optimum="5pt"
              background-color="silver"
              color="white"
              text-align="center"
              padding-top="3pt">
              <!-- Processing XHTML to XSL-FO to PDF -->
              <xsl:apply-templates select="html:body/html:h1/text()" />
            </fo:block>
        </fo:static-content>
        <fo:static-content flow-name="bottom" >
            <fo:block font-size="12pt" 
              font-family="sans-serif" 
              line-height="14pt"
              background-color="white"
              color="black"
              text-align="center"
              padding-top="3pt">
              <xsl:apply-templates select="html:head/html:title" />
              <xsl:text> - </xsl:text>
              <fo:page-number format="1" />
            </fo:block>
        </fo:static-content>
        <fo:flow flow-name="body" >
            <fo:block font-size="12pt" 
                font-family="sans-serif" 
                line-height="15pt"
                space-after.optimum="3pt"
                text-align="left">
                <xsl:apply-templates select="html:body" />
            </fo:block>
         </fo:flow>
      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="html:body">
      <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="html:p">
    <fo:block space-before="8pt" space-after="4pt">
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:li/html:p">
    <fo:block space-after="4pt">
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:h1">
    <fo:block space-before="4pt" space-after="1pt"
              font-weight="bold" 
              font-size="18pt" color="#000" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:h2">
    <fo:block space-before="4pt" space-after="1pt"
              font-weight="bold" 
              font-size="16pt" color="#000" >
      <xsl:apply-templates  />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:h3">
    <fo:block space-before="4pt" space-after="1pt"
              font-weight="bold" 
              font-size="14pt" color="#000" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:h4">
    <fo:block space-before="4pt" space-after="1pt"
              font-weight="bold" 
              font-size="12pt" color="#000" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:hr">
    <fo:block space-before="8pt" space-after="8pt"
              border="1px"
              width="50%"
              border-style="inset" />
  </xsl:template>

  <xsl:template match="html:address">
    <fo:block space-before="4pt" space-after="4pt" 
			  font-size="8pt"
              font-style="italic" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:a">
    <fo:basic-link 
        color="blue"
        external-destination="url('{@href}')" >
      <xsl:apply-templates />
    </fo:basic-link>
  </xsl:template>

  <xsl:template match="html:img">
    <fo:external-graphic src="url('{@src}')" >
    </fo:external-graphic>
  </xsl:template>

  <xsl:template match="html:pre">
    <fo:block font-family="monospace" space-before="4pt" space-after="4pt"
              background-color="#cfcfcf" 
              white-space-collapse="false"
              wrap-option="no-wrap"
              start-indent="0.4cm"
              text-align="left" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:br">
    <fo:block>
    </fo:block>
  </xsl:template>

  <xsl:template match="html:code">
    <fo:inline font-family="monospace" 
              white-space-collapse="false" >
      <xsl:apply-templates />
    </fo:inline>
  </xsl:template>

  <xsl:template match="html:em">
    <fo:inline font-style="italic" >
      <xsl:apply-templates />
    </fo:inline>
  </xsl:template>

  <xsl:template match="html:strong">
    <fo:inline font-weight="bold" >
      <xsl:apply-templates />
    </fo:inline>
  </xsl:template>

  <xsl:template match="html:ul">
    <fo:list-block space-before="8pt" space-after="2pt" >
      <xsl:apply-templates select="html:li" />
    </fo:list-block>
  </xsl:template>

  <xsl:template match="html:ul/html:li">
    <fo:list-item space-before="1pt" space-after="1pt" >
      <fo:list-item-label start-indent="0.1cm" >
        <fo:block>&#x2022;</fo:block>
      </fo:list-item-label>
      <fo:list-item-body  start-indent="body-start()" >
        <fo:block space-after="3pt" >
         <xsl:apply-templates  />
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <xsl:template match="html:ol">
    <fo:list-block space-before="8pt" space-after="2pt" >
      <xsl:apply-templates select="html:li" />
    </fo:list-block>
  </xsl:template>

  <xsl:template match="html:ol/html:li">
    <fo:list-item space-before="1pt" space-after="1pt" >
      <fo:list-item-label start-indent="0.1cm" >
        <fo:block>      
           <xsl:number format="1." />
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body  start-indent="body-start()" >
        <fo:block space-after="3pt" >
         <xsl:apply-templates  />
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>

  <xsl:template match="html:dl">
    <fo:block space-before="8pt" space-after="3pt"
              >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:dt">
    <fo:block space-before="3pt" space-after="1pt"
              font-weight="bold" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>

  <xsl:template match="html:dd">
    <fo:block space-before="1pt" space-after="1pt" 
              start-indent="0.5cm" >
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>


  <xsl:template match="html:table">
      <fo:table border-top-width="0px"
				border-right-width="0px"
				border-bottom-width="0px"
				border-left-width="0px"
                background-color="white"
                text-align="left" 
                table-layout="fixed" >
         <fo:table-column column-width="5cm"/>
         <fo:table-column column-width="5cm"/>
         <fo:table-column column-width="5cm"/>
        <fo:table-body padding-left="2pt"
          padding-right="2pt"
          padding-top="2pt"
          padding-bottom="2pt" >
          <xsl:apply-templates select="html:tr" />
        </fo:table-body>
      </fo:table>
  </xsl:template>
  
  <xsl:template match="html:tr">
    <fo:table-row padding-left="2pt"
          padding-right="2pt"
          padding-top="2pt"
          padding-bottom="2pt" >
       <xsl:apply-templates select="html:th|html:td"/>
    </fo:table-row>
  </xsl:template>
  
  <xsl:template match="html:th">
    <fo:table-cell font-weight="bold"
        text-align="center" 
        border-width="1px"
        border-style="solid" >
      <fo:block>         
          <xsl:apply-templates />
      </fo:block>
    </fo:table-cell>
  </xsl:template>
  
  <xsl:template match="html:td">
    <fo:table-cell border-width="1px"
        border-style="solid" >
      <fo:block padding-left="2pt"
          padding-right="2pt"
          padding-top="2pt"
          padding-bottom="2pt" 
          > 
      <xsl:apply-templates />
      </fo:block>
    </fo:table-cell>
  </xsl:template>
  
</xsl:stylesheet>