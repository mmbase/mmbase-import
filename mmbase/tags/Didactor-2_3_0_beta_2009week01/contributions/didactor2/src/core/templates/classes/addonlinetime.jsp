<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="adminstrator">
<mm:listnodes type="classes" id="class">
   <mm:field name="name" jspvar="class_name" vartype="String" write="false">
      <mm:remove referid="has_online_event" />
      <mm:relatednodes type="mmevents" max="1"><mm:import id="has_online_event" /></mm:relatednodes>
      <mm:notpresent referid="has_online_event">
         Creating online event for <%= class_name %><br/>
         <mm:createnode type="mmevents" id="online_event">
            <mm:setfield name="name"><%= "Online event for " + class_name %></mm:setfield>
            <mm:setfield name="start">954792041</mm:setfield>
            <mm:setfield name="stop">2145906041</mm:setfield>
         </mm:createnode>
         <mm:createrelation source="class" destination="online_event" role="related" />
      </mm:notpresent>
   </mm:field>
</mm:listnodes>
Done.<br/>
</mm:cloud>

