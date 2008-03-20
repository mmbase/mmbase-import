<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content postprocessor="none" expires="0" type="text/html">
    <mm:cloud method="asis">

      <mm:import externid="reset" />

      <mm:hasnode number="component.portal">
        <mm:hasrank value="anonymous">
          <mm:redirect page="/portal" />
        </mm:hasrank>

        <mm:hasrank minvalue="didactor user">
          <!--
              Would want to drop /index.jspx, but:

              http://www.mmbase.org/jira/browse/MMB-1440
          -->
          <mm:treefile page="/cockpit/index/index.jspx" objectlist="$includePath" referids="$referids,reset?"
                       id="redirpage" write="false" />
          <mm:redirect referid="redirpage" />
        </mm:hasrank>

      </mm:hasnode>

      <mm:hasnode number="component.portal" inverse="true">
        <mm:treefile page="/cockpit/index/index.jspx" objectlist="$includePath" referids="$referids,reset?"
                     id="redirpage" write="false" />
        <mm:redirect referid="redirpage" />
      </mm:hasnode>
    </mm:cloud>
  </mm:content>
</jsp:root>
