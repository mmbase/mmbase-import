<%--
  Map this file to jsp
  <servlet-mapping>
    <servlet-name>jsp</servlet-name>
    <url-pattern>*.rm</url-pattern>
  </servlet-mapping>

--%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:import externid="fragment" required="true"  
/><mm:import externid="environment"
>true</mm:import><mm:cloud
><mm:node  number="$fragment"
><%response.setHeader("Content-Type", "audio/x-pn-realaudio"); 
%><mm:nodeinfo type="nodemanager"
><mm:compare referid="environment" value="true"
><mm:relatednodes type="$_" directions="destination" role="previous"><mm:field name="url(rm)"  />
</mm:relatednodes></mm:compare><mm:field name="url(rm)" />
<mm:compare referid="environment" value="true"
><mm:relatednodes type="$_" directions="source" role="previous"
><mm:field name="url(rm)" />
</mm:relatednodes>
</mm:compare>
</mm:nodeinfo>
</mm:node></mm:cloud>