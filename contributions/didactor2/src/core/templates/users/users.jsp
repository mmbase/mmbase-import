<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <!--
  This is the list of users with role student, as used in the big (main) cockpit.
  It lists all users from the classes of which the current user is a member.

  If the user is connected directly to education it shows all useres in all classes
  -->
  <mm:content postprocessor="none">
    <mm:hasrank minvalue="didactor user">

      <mm:import externid="mode"/>

      <mm:node number="$user">
        <mm:hasrelationmanager
            sourcemanager="people"
            destinationmanager="educations" role="classrel">
          <mm:relatednodes role="classrel" type="educations" id="directly_related" />
        </mm:hasrelationmanager>

        <mm:relatednodes
            add="directly_related?"
            element="educations"
            path="classrel,classes,classrel,educations">
          <!-- Do check: is anybody online for this education -->
          <mm:import id="show_this_item" reset="true">false</mm:import>
          <mm:related path="classrel,people">
            <jsp:directive.include file="online_check.jsp" />
          </mm:related>
          <mm:related path="classrel,classes,classrel,people">
           <jsp:directive.include file="online_check.jsp" />
         </mm:related>
         <mm:compare referid="show_this_item" value="true">
           <di:translate key="core.users_educationheader" /><b><mm:field name="name"/></b>
           <br/>
           <mm:relatednodes role="classrel" type="people">
             <jsp:directive.include file="add_person.jsp" />
           </mm:relatednodes>

           <mm:related path="classrel,classes" orderby="classes.name">
             <mm:node element="classes">

               <mm:import id="show_this_item" reset="true">false</mm:import>
               <mm:related path="classrel,people">
                 <jsp:directive.include file="online_check.jsp" />
               </mm:related>
               <mm:compare referid="show_this_item" value="true">
                 <di:translate key="core.users_classheader" />
                 <mm:treefile write="false" page="/index.jsp" objectlist="$includePath" referids="_node@class">
                   <b><a href="${_}"><mm:field name="name"/></a></b>
                 </mm:treefile>
                 <br/><!-- brs are evil! -->
                 <mm:relatednodes role="classrel" type="people">
                   <jsp:directive.include file="add_person.jsp" />
                 </mm:relatednodes>
               </mm:compare>
             </mm:node>
           </mm:related>
         </mm:compare>
        </mm:relatednodes>
      </mm:node>
    </mm:hasrank>
  </mm:content>
</jsp:root>
