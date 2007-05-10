<!--
  This translates mmbase XML, normally containing an objects tag. The XML related to this XSL is generated by
  org.mmbase.bridge.util.Generator, and the XSL is invoked by FormatterTag.

  @author:  Michiel Meeuwissen
  @version: $Id: 2xhtml-mmbase18.xslt,v 1.1 2007-05-10 13:14:28 michiel Exp $
  @since:   MMBase-1.6
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
    xmlns:taglib="org.mmbase.bridge.jsp.taglib.functions.Functions"
    xmlns:o="http://www.mmbase.org/xmlns/objects"
    xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
    exclude-result-prefixes="node mmxf o taglib"
    version="1.0" >

  <xsl:import href="mmxf2xhtml.xslt" />   <!-- dealing with mmxf is done there -->

  <xsl:output method="xml" omit-xml-declaration="yes" /> <!-- xhtml is a form of xml -->

  <xsl:param name="cloud">mmbase</xsl:param>
  <xsl:param name="request"></xsl:param>
  <xsl:param name="formatter_requestcontext">/</xsl:param>

  <!--
      The thumbwidth parameter specifies the size of inline images.
      If an image is bigger then this, then it is clickable, to a popup.
  -->
  <xsl:param name="thumbwidth">100</xsl:param>
  <xsl:param name="thumbheight"></xsl:param>
  <xsl:param name="thumbsize">s(<xsl:value-of select="$thumbwidth" /><xsl:value-of select="$thumbheight" />&gt;)</xsl:param>

  <xsl:param name="thumbwidth-big">300</xsl:param>
  <xsl:param name="thumbheight-big"></xsl:param>
  <xsl:param name="thumbsize-big">s(<xsl:value-of select="$thumbwidth-big" /><xsl:value-of select="$thumbheight-big" />&gt;)</xsl:param>


  <!--
      If a popupwidth parameter is specified, then image popups will be maximally that size.
      If is empty, then simply a link to the original images will be created.
  -->
  <xsl:param name="popupwidth"></xsl:param>
  <xsl:param name="popupheight"></xsl:param>
  <xsl:param name="popupsize">s(<xsl:value-of select="$popupwidth" /><xsl:value-of select="$popupheight" />&gt;)</xsl:param>



  <xsl:variable name="newstype">xmlnews</xsl:variable>
  <!-- I had an 'xmlnews' type... Can easily switch beteen them like
       this.  Perhaps you prefer 'news' itself to contain XML fields. -->

   <!-- If objects is the entrance to this XML, then only handle the root child of it -->
  <xsl:template match="o:objects">
    <div class="objects">
      <xsl:apply-templates select="o:object[1]" />
    </div>
  </xsl:template>


  <!-- how to present a node -->
  <xsl:template match="o:object">
    <xsl:apply-templates select="o:field" mode="top" />
  </xsl:template>


   <!-- how to present a news node -->
   <xsl:template match="o:object[@type=$newstype and not(o:unfilledField)]" mode="top">
     <xsl:apply-templates select="o:field[@name='title']"    mode="top"/>
     <xsl:apply-templates select="o:field[@name='subtitle']" mode="top"/>
     <xsl:apply-templates select="o:field[@name='intro']" mode="top"/>
     <xsl:apply-templates select="o:field[@name='body']" mode="top"/>
   </xsl:template>


  <xsl:template match="o:object[@type=$newstype]/o:field[@name='title']" mode="top">
    <h1><xsl:value-of select="." /></h1>
  </xsl:template>
  <xsl:template match="o:object[@type=$newstype]/o:field[@name='subtitle']" mode="top">
    <h2><xsl:value-of select="." /></h2>
  </xsl:template>


  <xsl:template match="o:field[@format='xml']" mode="top">
    <xsl:choose>
      <xsl:when test="mmxf:mmxf">
        <xsl:choose>
          <xsl:when test="*">
            <xsl:apply-templates  />
          </xsl:when>
          <xsl:otherwise>
            <!-- make sure not to spit out something empty, because that may confuse certain browers -->
            <p></p>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise><!-- null -->
        <!-- make sure not to spit out something empty, because that may confuse certain browers -->
        <p></p>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>


  <!--
      Produces an URL to point to a certain object.
  -->
  <xsl:template match="o:object" mode="url">
    <xsl:value-of select="node:function($cloud, string(@id ), 'url', $request)" />
  </xsl:template>

  <xsl:template match="o:object[@type = 'images']|o:object[@type ='attachments']|o:object[@type='icaches']" mode="url">   
    <xsl:value-of select="node:saxonFunction($cloud, string(@id), 'servletpath')" />
  </xsl:template>


  <xsl:template match="o:object[@type = 'urls']" mode="url">
    <xsl:value-of select="./o:field[@name='url']" />
  </xsl:template>


  <!-- Produces output for one object 
       Required argument: relation, the relation object which made this necessary.
       position, last: if used in a list, these can be provided.
  -->
  <xsl:template match="o:object" mode="inline">
    <xsl:param name="relation" />
    <xsl:param name="position" />
    <xsl:param name="last" />
    <a>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:attribute name="class">generated</xsl:attribute>
      <xsl:apply-templates select="." mode="title" />
    </a>
    <xsl:if test="$position != $last">,</xsl:if>
  </xsl:template>


  <!-- Produces output for one object, given a body for the resulting a-tag.
       Required argument: relation, the relation object which made this necessary
       Required argument: body, the body element
  -->
  <xsl:template match="o:object" mode="inline_body">
    <xsl:param name="relation" />
    <xsl:param name="body" />
    <a>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:if test="not($body)">
        <xsl:attribute name="class">generated</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="$body">
        <xsl:with-param name="in_a">yes</xsl:with-param>
      </xsl:apply-templates>
    </a>
  </xsl:template>

  <!-- produces output for one url object,   -->
  <xsl:template match="o:object[@type='urls']" mode="inline_body">
    <xsl:param name="relation" />
    <xsl:param name="body" />
    <xsl:element name="a">
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="title"><xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:apply-templates select="$body" >
        <xsl:with-param name="relation" select="$relation" />
        <xsl:with-param name="in_a">yes</xsl:with-param>
      </xsl:apply-templates>
    </xsl:element>
  </xsl:template>




  <!--
       Produces one img-tag for an o:object of type images.
       params: relation
  -->
  <xsl:template match="o:object" mode="img">
    <xsl:param name="relation" />
    <xsl:variable name="thumb">
      <xsl:choose>
        <xsl:when test="contains($relation/o:field[@name = 'class'], 'big')">
          <xsl:value-of select="$thumbsize-big" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$thumbsize" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="icache" select="node:nodeFunction(., $cloud, string(./@id), 'cachednode', $thumb)" />
    <img src="{node:saxonFunction($cloud, string($icache/@id ), 'servletpath')}" >
      <xsl:attribute name="alt"><xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:attribute name="class"><xsl:value-of select="$relation/o:field[@name='class']"  /></xsl:attribute>
      <xsl:if test="$icache/o:field[@name='width']">
        <xsl:attribute name="height"><xsl:value-of select="$icache/o:field[@name='height']" /></xsl:attribute>
        <xsl:attribute name="width"><xsl:value-of select="$icache/o:field[@name='width']" /></xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>

  <!-- 
       Produces the presentation for an object which is related somewhere already inside an a
       tag. This means that it has to be careful to produce more a-tags.
  -->
  <xsl:template match="o:object" mode="in_a">
    <xsl:param name="relation" />    
    <xsl:apply-templates select="." mode="title" />
    <xsl:apply-templates select="." mode="inline">
      <xsl:with-param name="relation" select="$relation" />
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="o:object[@type = 'images' or contains(@ancestors, ' images ')]" mode="in_a">
    <xsl:param name="relation" />    
    <xsl:apply-templates select="." mode="img" >
      <xsl:with-param name="relation" select="$relation" />
    </xsl:apply-templates>
  </xsl:template>




  <!-- 
       produces an icon for an object 
       Used for nodes of the type attachments, of course, but it can als be imaginable for other objects
  -->
  <xsl:template match="o:object" mode="icon">
    <img width="16" height="16" class="icon">
      <xsl:attribute name="alt"><xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:attribute name="src"><xsl:value-of select="node:saxonFunction($cloud, string(@id), 'iconurl')" /></xsl:attribute>
    </img>
  </xsl:template>

  <!--
       Produces output for one o:object of type images.
       params: relation, position, last
  -->
  <xsl:template match="o:object[@type = 'images' or contains(@ancestors, ' images ')]" mode="inline">    
    <xsl:param name="relation" />
    <xsl:param name="position" />
    <xsl:param name="last" />
    <xsl:variable name="thumbw">
      <xsl:choose>
        <xsl:when test="contains($relation/o:field[@name = 'class'], 'big')">
          <xsl:value-of select="$thumbwidth-big" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$thumbwidth" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="o:field[@name='width'] &gt; $thumbw + 20">
        <!-- if thumb is smaller than actual image, produce a link to popup -->
        <xsl:choose>         
          <xsl:when test="$popupwidth = '-1'">
            <xsl:apply-templates select="." mode="img">
              <xsl:with-param name="relation" select="$relation" />
              <xsl:with-param name="position" select="$position"  />
              <xsl:with-param name="last"  select="$last"  />
            </xsl:apply-templates>
          </xsl:when>
          <xsl:when test="$popupwidth = ''">
            <!-- original image -->
            <xsl:variable name="width"><xsl:value-of select="node:saxonFunction($cloud, string(@id ), 'width')" /></xsl:variable>
            <xsl:variable name="height"><xsl:value-of select="node:saxonFunction($cloud, string(@id ), 'height')" /></xsl:variable>
            <a onclick="window.open(this.href, '{taglib:escape('identifier',./o:field[@name = 'title'])}', 'width={$width + 20},height={$height + 20}'); return false;">
              <xsl:attribute name="id">
                <xsl:value-of select="$relation/o:field[@name = 'id']" />
              </xsl:attribute>
              <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
              <xsl:apply-templates select="." mode="img">
                <xsl:with-param name="relation" select="$relation" />
                <xsl:with-param name="position" select="$position"  />
                <xsl:with-param name="last"  select="$last"  />
              </xsl:apply-templates>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="icache" select="node:nodeFunction(., $cloud, string(./@id), 'cachednode', $popupsize)" />
            <xsl:variable name="href"><xsl:value-of select="node:saxonFunction($cloud, string($icache/@id ), 'servletpath')" /></xsl:variable>
            <xsl:variable name="width"><xsl:value-of select="node:saxonFunction($cloud, string($icache/@id ), 'width')" /></xsl:variable>
            <xsl:variable name="height"><xsl:value-of select="node:saxonFunction($cloud, string($icache/@id ), 'height')" /></xsl:variable>
            <a href="{$href}"
               onclick="window.open(this.href, '{taglib:escape('identifier',./o:field[@name = 'title'])}', 'width={$width + 20},height={$height + 20}'); return false;">

              <xsl:attribute name="title"><xsl:value-of select="./o:field[@name = 'title']" /></xsl:attribute>
              <xsl:attribute name="id">
                <xsl:value-of select="$relation/o:field[@name = 'id']" />
                <xsl:value-of select="$position" />                
              </xsl:attribute>
              <xsl:apply-templates select="." mode="img">
                <xsl:with-param name="relation" select="$relation" />
                <xsl:with-param name="position" select="$position"  />
                <xsl:with-param name="last"  select="$last"  />
              </xsl:apply-templates>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="." mode="img">
          <xsl:with-param name="relation" select="$relation" />
          <xsl:with-param name="position" select="$position"  />
          <xsl:with-param name="last"  select="$last"  />
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="$position != $last">,</xsl:if>
  </xsl:template>

  


  <!--
       Produces output for one o:object of type attachments
       params: relation, position, last
  -->

  <xsl:template match="o:object[@type = 'attachments']" mode="inline">
    <xsl:param name="relation" />
    <xsl:param name="position" />
    <xsl:param name="last" />
    <a>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:attribute name="title"><xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="class">generated</xsl:attribute>
      <xsl:apply-templates select="." mode="icon" />
    </a>
    <xsl:if test="$position != $last">,</xsl:if>
  </xsl:template>


  <xsl:template match="o:object[@type = 'blocks']" mode="class">
    <xsl:param name="relation" />
    <xsl:value-of select="$relation/o:field[@name='class']" />
  </xsl:template>
  <!--
       Produces output for one o:object of type 'blocks'
       params: relation
  -->
  <xsl:template match="o:object[@type = 'blocks']" mode="inline">
    <xsl:param name="relation" />
    <div>
      <xsl:attribute name="id">
        <xsl:apply-templates select="." mode="id">
          <xsl:with-param name="relation" select="$relation" />
        </xsl:apply-templates>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:apply-templates select="." mode="class">
          <xsl:with-param name="relation" select="$relation" />
        </xsl:apply-templates>
      </xsl:attribute>
      <xsl:choose>
        <xsl:when test="contains($relation/o:field[@name='class'], 'quote')">
          <xsl:apply-templates select="." mode="quote" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="o:field[@name = 'body']" />
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="o:object[@type = 'blocks']" mode="id">
  </xsl:template>

  <xsl:template match="o:object[@type = 'blocks']" mode="quote">
    <span style="font-size: +30pt; float: left;">&#x2018;</span>
       <xsl:apply-templates select="o:field[@name = 'body']" />
    <span style="font-size: +30pt; float: right;">&#x2019;</span>    
  </xsl:template>


  <!--
       Produces output for one o:object of type urls
       params: relation, position, last
  -->
  <xsl:template match="o:object[@type = 'urls']|o:object[@type = 'segments']" mode="inline">
    <xsl:param name="relation" />
    <xsl:param name="position" />
    <xsl:param name="last" />
    <a>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:apply-templates select="." mode="title" />
    </a>
    <xsl:if test="$position != $last">,</xsl:if>
  </xsl:template>





  <!--
       Produces at title for one o:object
  -->
  <xsl:template match="o:object" mode="title">
    <xsl:choose>
      <xsl:when test="./o:field[@name='title'] != ''" >
        <xsl:value-of select="./o:field[@name='title']" />
      </xsl:when>
      <xsl:when test="./o:field[@name='name'] != ''" >
        <xsl:value-of select="./o:field[@name='name']" />
      </xsl:when>
      <xsl:when test="./o:field[@name='description'] != ''" >
        <xsl:value-of select="./o:field[@name='description']" />
      </xsl:when>
      <xsl:when test="./o:field[@name='alt'] != ''" >
        <xsl:value-of select="./o:field[@name='alt']" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="./o:field[@name='url']" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="o:object[@type = 'segments']" mode="title">
    <xsl:variable name="index" select="node:saxonFunction($cloud, string(@id), 'index')" />
    <xsl:choose>
      <xsl:when test="$index = ''">
        <xsl:value-of select="./o:field[@name='title']" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$index" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="mmxf:*" mode="in_a">
    <xsl:apply-templates select=".">      
      <xsl:with-param name="in_a">yes</xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>

  <!--
       Template to override mmxf tags with an 'id', we support links to it here This contains code
       to determine the relations and calls jumps to with_relations mode, where the 'relations'
       parameter becomes available (all relations pointing to this element).
  -->
  
  <xsl:template match="mmxf:section[@id != '']|mmxf:p[@id != '']|mmxf:a" >
    <xsl:param name="in_a" />
     <!-- store all 'relation' nodes of this node for convenience in $rels:-->
    <xsl:variable name="rels"   select="ancestor::o:object/o:relation[@role='idrel']" />

    <!-- also for conveniences: all related nodes to this node-->
    <xsl:variable name="related_to_node"   select="/*[@id = $rels/@related]" />

    <xsl:variable name="relations" select="//o:objects/o:object[@id=$rels/@object and o:field[@name='id'] = current()/@id]" />
    

    <xsl:apply-templates select="." mode="with_relations">
      <xsl:with-param name="relations" select="$relations" />
      <xsl:with-param name="in_a" select="$in_a" />
    </xsl:apply-templates>
  </xsl:template>

  <!--
      Presents an mmxf:section with relations.
      Does sections specific stuff and jumps 'relations' mode which is a more generic template to handle inline relations.
  -->
  <xsl:template match="mmxf:section" mode="with_relations">
    <xsl:param name="relations" />
    <xsl:param name="in_a" />
    <xsl:apply-templates select="mmxf:h" />
    <xsl:apply-templates select="." mode="relations">
      <xsl:with-param name="relations" select="$relations" />
      <xsl:with-param name="in_a" select="$in_a" />
    </xsl:apply-templates>
    <xsl:apply-templates select="mmxf:section|mmxf:p|mmxf:ul|mmxf:ol|mmxf:table|mmxf:sub|mmxf:sup">
      <xsl:with-param name="in_a" select="$in_a" />
    </xsl:apply-templates>
  </xsl:template>

  <!--
      Presents an mmxf:p with relations.
      Does p specific stuff and jumps 'relations' mode which is a more generic template to handle inline relations.
  -->
  <xsl:template match="mmxf:p" mode="with_relations">
    <xsl:param name="relations" />
    <xsl:param name="in_a" />
    <xsl:element name="{name()}">
      <xsl:copy-of select="@class" />
      <xsl:apply-templates select="." mode="relations">
        <xsl:with-param name="relations" select="$relations" />
        <xsl:with-param name="in_a" select="$in_a" />
      </xsl:apply-templates>
      <xsl:apply-templates select="node()" />
    </xsl:element>
  </xsl:template>

  <!--
      Presents an mmxf:a with relations.
      Does p specific stuff and jumps 'relations' mode which is a more generic template to handle inline relations.
  -->
  <xsl:template match="mmxf:a" mode="with_relations">
    <xsl:param name="relations" />
    <xsl:param name="in_a" />
    <xsl:choose>
      <!-- it has body, and precisely one relation, make body clickable -->
      <xsl:when test="node() and count($relations) = 1">
        <xsl:variable name="toNodeNumber" select="ancestor::o:object/o:relation[@object = $relations[1]/@id]/@related" />
	<xsl:apply-templates select="//o:objects/o:object[@id = $toNodeNumber]" mode="inline_body">
          <xsl:with-param name="relation" select="$relations[1]" />
          <xsl:with-param name="body"     select="node()" />
        </xsl:apply-templates>
      </xsl:when>
      <xsl:when test="not(node()) and count($relations) = 1 and $in_a = 'yes'">        
        <xsl:variable name="toNodeNumber" select="ancestor::o:object/o:relation[@object = $relations[1]/@id]/@related" />
	<xsl:apply-templates select="//o:objects/o:object[@id = $toNodeNumber]" mode="in_a">
          <xsl:with-param name="relation" select="$relations[1]" />
        </xsl:apply-templates>
      </xsl:when>
      <xsl:when test="not(node()) and count($relations) = 1">
        <xsl:variable name="toNodeNumber" select="ancestor::o:object/o:relation[@object = $relations[1]/@id]/@related" />
	<xsl:apply-templates select="//o:objects/o:object[@id = $toNodeNumber]" mode="inline">
          <xsl:with-param name="relation" select="$relations[1]" />
        </xsl:apply-templates>
      </xsl:when>
      <!-- otherwise, things get a bit different -->
      <xsl:otherwise>
        <xsl:apply-templates select="node()" />
        <xsl:apply-templates select="." mode="relations">          
          <xsl:with-param name="relations" select="$relations" />
          <xsl:with-param name="body"     select="node()" />
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  
  <!--
    Presents only relations. Iterates over all given relations, and applies the 'inline' mode template on every related node.
    It provides three parameters then:
    'relation': the current used relation, it's fields may be needed
    'position': the current position
    'last':     maximal position. 'position' and 'last' can be used to produce separators or so.
  -->
  <xsl:template match="mmxf:section|mmxf:p|mmxf:a" mode="relations">
    <xsl:param name="relations" />
    <xsl:param name="body" />
    <xsl:param name="in_a" />
    <xsl:variable name="fromNode" select="ancestor::o:object" />
    <xsl:for-each select="$relations">
      <xsl:variable name="toNodeNumber" select="$fromNode/o:relation[@object = current()/@id]/@related" />
      <xsl:variable name="position" select="position()" />
      <xsl:variable name="last"     select="last()" />
      <xsl:choose>
        <xsl:when test="$in_a = 'yes'">
	  <xsl:apply-templates select="//o:objects/o:object[@id = $toNodeNumber]" mode="in_a">
            <xsl:with-param name="relation" select="." />
            <xsl:with-param name="position" select="$position" />
            <xsl:with-param name="last"     select="$last" />
            <xsl:with-param name="body"     select="$body" />
          </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
	  <xsl:apply-templates select="//o:objects/o:object[@id = $toNodeNumber]" mode="inline">
            <xsl:with-param name="relation" select="." />
            <xsl:with-param name="position" select="$position" />
            <xsl:with-param name="last"     select="$last" />
            <xsl:with-param name="body"     select="$body" />
          </xsl:apply-templates>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>






</xsl:stylesheet>
