<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud jspvar="cloud" name="mmbase">
    <mm:listnodes type="pages" id="page">
        <mm:list nodes="$page" path="pages,insrel,attachments">
            <% int pos = 0; %>
            <mm:field name="insrel.number" id="relnum"/>
            <mm:field name="attachments.number" id="attnum"/>
            <mm:node number="$relnum">
                <mm:nodeinfo type="type">
                    <mm:write/>... 
                    <mm:compare value="insrel">
                        <mm:node number="$attnum" id="attachment"/>
                        <mm:createrelation source="page" destination="attachment" role="posrel">
                            <mm:setfield name="pos"><%= pos++ %></mm:setfield>
                        </mm:createrelation>
                        <mm:deletenode deleterelations="false"/>
                        migrated<br>
                    </mm:compare>
                    <mm:compare value="insrel" inverse="true">
                        skipped<br>
                    </mm:compare>
                </mm:nodeinfo>
            </mm:node>
        </mm:list>
        
        <mm:remove referid="attnum"/>
        <mm:remove referid="relnum"/>
        <mm:remove referid="attachment"/>

        <mm:list nodes="$page" path="pages,insrel,urls">
            <% int pos = 0; %>
            <mm:field name="insrel.number" id="relnum"/>
            <mm:field name="urls.number" id="attnum"/>
            <mm:node number="$relnum">
                <mm:nodeinfo type="type">
                    <mm:write/>... 
                    <mm:compare value="insrel">
                        <mm:node number="$attnum" id="attachment"/>
                        <mm:createrelation source="page" destination="attachment" role="posrel">
                            <mm:setfield name="pos"><%= pos++ %></mm:setfield>
                        </mm:createrelation>
                        <mm:deletenode deleterelations="false"/>
                        migrated<br>
                    </mm:compare>
                    <mm:compare value="insrel" inverse="true">
                        skipped<br>
                    </mm:compare>
                </mm:nodeinfo>
            </mm:node>
        </mm:list>

        <mm:remove referid="attnum"/>
        <mm:remove referid="relnum"/>
        <mm:remove referid="attachment"/>

         <mm:list nodes="$page" path="pages,insrel,audiotapes">
            <% int pos = 0; %>
            <mm:field name="insrel.number" id="relnum"/>
            <mm:field name="audiotapes.number" id="attnum"/>
            <mm:node number="$relnum">
                <mm:nodeinfo type="type">
                    <mm:write/>... 
                    <mm:compare value="insrel">
                        <mm:node number="$attnum" id="attachment"/>
                        <mm:createrelation source="page" destination="attachment" role="posrel">
                            <mm:setfield name="pos"><%= pos++ %></mm:setfield>
                        </mm:createrelation>
                        <mm:deletenode deleterelations="false"/>
                        migrated<br>
                    </mm:compare>
                    <mm:compare value="insrel" inverse="true">
                        skipped<br>
                    </mm:compare>
                </mm:nodeinfo>
            </mm:node>
        </mm:list>

        <mm:remove referid="attnum"/>
        <mm:remove referid="relnum"/>
        <mm:remove referid="attachment"/>


        <mm:list nodes="$page" path="pages,insrel,videotapes">
            <% int pos = 0; %>
            <mm:field name="insrel.number" id="relnum"/>
            <mm:field name="videotapes.number" id="attnum"/>
            <mm:node number="$relnum">
                <mm:nodeinfo type="type">
                    <mm:write/>... 
                    <mm:compare value="insrel">
                        <mm:node number="$attnum" id="attachment"/>
                        <mm:createrelation source="page" destination="attachment" role="posrel">
                            <mm:setfield name="pos"><%= pos++ %></mm:setfield>
                        </mm:createrelation>
                        <mm:deletenode deleterelations="false"/>
                        migrated<br>
                    </mm:compare>
                    <mm:compare value="insrel" inverse="true">
                        skipped<br>
                    </mm:compare>
                </mm:nodeinfo>
            </mm:node>
        </mm:list>
 
 
        
       
    </mm:listnodes>
    <br>
    Ok
</mm:cloud>
