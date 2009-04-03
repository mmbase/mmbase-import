    <di:hasrole role="teacher" inverse="true">
        <mm:import id="emaildomain" reset="true" escape="trimmer"><mm:treeinclude write="true" page="/email/init/emaildomain.jsp" objectlist="$includePath"/></mm:import>
        <mm:import externid="_readrights"/>
        <mm:compare referid="_readrights" value="2">
            <%-- 
                student made content available to teachers 
                notify teachers: send mail to each of them
            --%>
            <mm:list nodes="$user" path="people1,classes,people2,roles" constraints="roles.name='teacher' and people2.email != ''">
                <mm:field name="people2.email" id="email">
                    <mm:field name="people1.firstname" id="studentfname">
                        <mm:field name="people1.lastname" id="studentlname">
                            <mm:createnode type="emails">
                                <mm:setfield name="to"><mm:write referid="email"/></mm:setfield>
                                <mm:setfield name="from">portfoliobot<mm:write referid="emaildomain"/></mm:setfield>
                                <mm:setfield name="subject"><mm:write referid="studentfname"/>  <mm:write referid="studentlname"/> heeft een nieuw portfolioitem</mm:setfield>

<mm:setfield name="body"><mm:write referid="studentfname"/>  <mm:write referid="studentlname"/> heeft een nieuw portfolioitem voor docenten zichtbaar gemaakt,
of een zichtbaar item aangepast.

Zie <http://<%= request.getServerPort() == 80 || request.getServerPort() == 0 ? request.getServerName() :  request.getServerName()+":"+request.getServerPort() %><mm:treefile write="true" page="/portfolio/showitem.jsp" objectlist="$includePath"/>?currentitem=<mm:write referid="currentitem"/>&provider=<mm:write referid="provider"/>&contact=<mm:write referid="user"/>&currentfolder=<mm:write referid="currentfolder"/>>
</mm:setfield>
                                <mm:setfield name="type">1</mm:setfield> 
                           </mm:createnode>            
                        </mm:field>
                    </mm:field>
                </mm:field>
            </mm:list>
        </mm:compare>
    </di:hasrole>

