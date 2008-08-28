<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:directive.page buffer="100kb" />
  <mm:content postprocessor="none" expires="0">
    <mm:cloud rank="didactor user">
      <mm:isgreaterthan referid="user" value="0">

        <mm:node referid="user">
          <mm:field id="oldLastActivity" name="lastactivity" write="false"/>
          <mm:islessthan referid="oldLastActivity" value="2">
            <mm:import id="oldLastActivity" reset="true"><mm:time time="now" /></mm:import>
          </mm:islessthan>
          <mm:setfield name="lastactivity"><mm:time time="now" /></mm:setfield>
          <mm:setfield name="islogged">1</mm:setfield>
        </mm:node>


        <!--
            If the parameter 'add' is present (which is if di:setting("core", "pagereporter") is true.
            Then also a pagestays objet will be updated (and created if necessary).
            These objects can be used to generate reports about page stay durations.
        -->
        <mm:import externid="add" />
        <mm:present referid="add">
          <mm:import externid="content" />
          <mm:import externid="page" />
          <mm:listnodescontainer type="pagestays">
            <mm:constraint field="user" value="$user" />
            <mm:constraint field="content" value="$content"  />
            <mm:constraint field="page" value="$page"  />
            <mm:listnodes max="1">
              <mm:node id="pagestay" />
            </mm:listnodes>
            <mm:notpresent referid="pagestay">
              <mm:createnode type="pagestays" id="pagestay">
                <mm:setfield name="user">${user}</mm:setfield>
                <mm:setfield name="content">${content}</mm:setfield>
                <mm:setfield name="page">${page}</mm:setfield>
              </mm:createnode>
            </mm:notpresent>
            <mm:node referid="pagestay">
              <mm:field name="duration" write="false">
                <mm:setfield name="duration">${_ + add}</mm:setfield>
              </mm:field>
            </mm:node>
          </mm:listnodescontainer>
        </mm:present>

        <mm:present referid="education">
          <mm:present referid="class">
            <mm:compare referid="class" value="">
              <mm:list fields="classrel.number,classrel.lastlogin" path="people,classrel,educations"
                       max="1" constraints="people.number=${user} and educations.number=${education}"
                       orderby="classrel.lastlogin" directions="down">
                <jsp:directive.include file="onlineStat.jsp" />
              </mm:list>
            </mm:compare>
            <mm:compare referid="class" value="" inverse="true">
              <mm:list fields="classrel.number,classrel.lastlogin" path="people,classrel,classes"
                       max="1" constraints="people.number=${user} and classes.number=${class}"
                       orderby="classrel.lastlogin" directions="down">
                <jsp:directive.include file="onlineStat.jsp" />
              </mm:list>
            </mm:compare>
          </mm:present>
        </mm:present>
      </mm:isgreaterthan>
    </mm:cloud>
  </mm:content>
</jsp:root>
