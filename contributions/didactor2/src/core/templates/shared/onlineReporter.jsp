<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
  <mm:cloud>
    <jsp:directive.include file="setImports.jsp" />
    <mm:isgreaterthan referid="user" value="0">
      <mm:node referid="user">
        <mm:field id="oldLastActivity" name="lastactivity" write="false"/>
        <mm:islessthan referid="oldLastActivity" value="2">
          <mm:import id="oldLastActivity" reset="true"><mm:time time="now" /></mm:import>
        </mm:islessthan>

        <mm:setfield name="lastactivity"><mm:time time="now" /></mm:setfield>
        <mm:setfield name="islogged">1</mm:setfield>
      </mm:node>

      <mm:present referid="education">
        <mm:present referid="class">
          <mm:compare referid="class" value="null">
            <mm:list fields="classrel.number,classrel.lastlogin" path="people,classrel,educations"
                     max="1" constraints="people.number=${user} and educations.number=${education}"
                     orderby="classrel.lastlogin" directions="down">
              <jsp:directive.include file="onlineStat.jsp" />
            </mm:list>
          </mm:compare>
          <mm:compare referid="class" value="null" inverse="true">
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
