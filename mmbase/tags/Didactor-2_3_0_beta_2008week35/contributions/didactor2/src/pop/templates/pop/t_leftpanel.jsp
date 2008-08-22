<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<%@include file="getids.jsp" %>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">

  <mm:import externid="t_rights"/>

  <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids">
             <mm:param name="t_mode">false</mm:param>
           </mm:treefile>"><u>to private mode</u>
  </a><br/><br/>


  <mm:import externid="wgroup"/>
  <mm:compare referid="wgroup" value=""><mm:import id="wgroup" reset="true">0</mm:import></mm:compare>
  <form name="teacherform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids">
      </mm:treefile>" method="post">
    <input type="hidden" name="whatselected" value="0">

<mm:compare referid="whatselected" value="class">
  <mm:compare referid="class" value="0">
    <mm:import id="whatselected" reset="true">0</mm:import>
  </mm:compare>
</mm:compare>
<mm:compare referid="whatselected" value="wgroup">
  <mm:compare referid="wgroup" value="0">
    <mm:import id="whatselected" reset="true">0</mm:import>
  </mm:compare>
</mm:compare>
<mm:compare referid="whatselected" value="student">
  <mm:compare referid="student" value="0">
    <mm:import id="whatselected" reset="true">0</mm:import>
  </mm:compare>
</mm:compare>

<mm:compare referid="whatselected" value="0">
  <di:translate key="pop.selectclass" /><br/>
  <select name="class" class="popteacherformselect" onChange="teacherform.whatselected.value='class';teacherform.submit()">
    <option value=0><di:translate key="pop.allclasses" /></option>
    <mm:list nodes="$user" path="people,classes" orderby="classes.name" directions="UP">
      <option value="<mm:field name="classes.number"/>"><mm:field name="classes.name"/></option>
    </mm:list>
  </select>
  <br/>
  <di:translate key="pop.selectworkgroup" /><br/>
  <select name="wgroup" class="popteacherformselect" onChange="teacherform.whatselected.value='wgroup';teacherform.submit()">
    <option value=0><di:translate key="pop.allworkgroups" /></option>
    <mm:list nodes="$user" path="people,classes,workgroups" fields="workgroups.number" orderby="workgroups.name" 
        directions="UP" distinct="true">
      <option value="<mm:field name="workgroups.number"/>"><mm:field name="workgroups.name"/></option>
    </mm:list>
  </select>
  <br/>
  <di:translate key="pop.selectstudent" /><br/>
  <select name="student" class="popteacherformselect" onChange="teacherform.whatselected.value='student';teacherform.submit()">
    <option value=0><di:translate key="pop.allstudents" /></option>
    <mm:list nodes="$user" path="people1,classes,people2,roles" fields="people2.number" orderby="people2.lastname" 
        directions="UP" distinct="true" constraints="roles.name='student'">
      <option value="<mm:field name="people2.number"/>"><mm:field name="people2.firstname"/> <mm:field name="people2.lastname"/></option>
    </mm:list>
  </select>
</mm:compare>

<mm:compare referid="whatselected" value="class">
  <mm:import id="mainconstraint" reset="true">classes.number='<mm:write referid="class"/>'</mm:import>
  <mm:import id="studentconstr" reset="true">AND <mm:write referid="mainconstraint"/></mm:import>
  <di:translate key="pop.selectclass" /><br/>
  <select name="class" class="popteacherformselect" onChange="teacherform.whatselected.value='class';teacherform.submit()">
    <option value=0><di:translate key="pop.allclasses" /></option>
    <mm:list nodes="$user" path="people,classes" orderby="classes.name" directions="UP">
      <mm:import id="dummy" reset="true"><mm:field name="classes.number"/></mm:import>
      <option value="<mm:write referid="dummy"/>" <mm:compare referid="class" referid2="dummy">selected</mm:compare>><mm:field name="classes.name"/></option>
    </mm:list>
  </select>
  <br/>
  <di:translate key="pop.selectworkgroup" /><br/>
  <select name="wgroup" class="popteacherformselect" onChange="teacherform.whatselected.value='wgroup';teacherform.submit()">
    <mm:list nodes="$user" path="people,classes,workgroups" fields="workgroups.number" orderby="workgroups.name" 
        directions="UP" distinct="true" constraints="$mainconstraint">
      <option value="<mm:field name="workgroups.number"/>"><mm:field name="workgroups.name"/></option>
    </mm:list>
  </select>
  <br/>
  <di:translate key="pop.selectstudent" /><br/>
  <select name="student" class="popteacherformselect" onChange="teacherform.whatselected.value='student';teacherform.submit()">
    <mm:list nodes="$user" path="people1,classes,people2,roles" fields="people2.number" orderby="people2.lastname" 
        directions="UP" distinct="true" constraints="roles.name='student' $studentconstr">
      <option value="<mm:field name="people2.number"/>"><mm:field name="people2.firstname"/> <mm:field name="people2.lastname"/></option>
    </mm:list>
  </select>
</mm:compare>

<mm:compare referid="whatselected" value="wgroup">
  <mm:import id="mainconstraint" reset="true">workgroups.number='<mm:write referid="wgroup"/>'</mm:import>
    
  <di:translate key="pop.selectclass" /><br/>
  <select name="class" class="popteacherformselect" onChange="teacherform.whatselected.value='class';teacherform.submit()">
    <mm:list nodes="$user" path="people,classes,workgroups" fields="classes.number" orderby="classes.name"
        directions="UP" distinct="true" constraints="$mainconstraint">
      <option value="<mm:field name="classes.number"/>"><mm:field name="classes.name"/></option>
    </mm:list>
  </select>
  <br/>
  <di:translate key="pop.selectworkgroup" /><br/>
  <select name="wgroup" class="popteacherformselect" onChange="teacherform.whatselected.value='wgroup';teacherform.submit()">
    <option value=0><di:translate key="pop.allworkgroups" /></option>
    <mm:list nodes="$user" path="people,classes,workgroups" fields="workgroups.number" orderby="workgroups.name" 
        directions="UP" distinct="true">
      <mm:import id="dummy" reset="true"><mm:field name="workgroups.number"/></mm:import>
      <option value="<mm:write referid="dummy"/>" <mm:compare referid="wgroup" referid2="dummy">selected</mm:compare>><mm:field name="workgroups.name"/></option>
    </mm:list>
  </select>
  <br/>
  <di:translate key="pop.selectstudent" /><br/>
  <select name="student" class="popteacherformselect" onChange="teacherform.whatselected.value='student';teacherform.submit()">
    <mm:list nodes="$wgroup" path="workgroups,people,roles" fields="people.number" orderby="people.lastname" 
        directions="UP" distinct="true" constraints="roles.name='student'">
      <option value="<mm:field name="people2.number"/>"><mm:field name="people2.firstname"/> <mm:field name="people2.lastname"/></option>
    </mm:list>
  </select>
</mm:compare>

<mm:compare referid="whatselected" value="student">
  <di:translate key="pop.selectclass" /><br/>
  <select name="class" class="popteacherformselect" onChange="teacherform.whatselected.value='class';teacherform.submit()">
    <mm:list nodes="$student" path="people,classes" orderby="classes.name" directions="UP">
      <option value="<mm:field name="classes.number"/>"><mm:field name="classes.name"/></option>
    </mm:list>
  </select>
  <br/>
  <di:translate key="pop.selectworkgroup" /><br/>
  <select name="wgroup" class="popteacherformselect" onChange="teacherform.whatselected.value='wgroup';teacherform.submit()">
    <mm:list nodes="$student" path="people,workgroups" orderby="workgroups.name" directions="UP">
      <option value="<mm:field name="workgroups.number"/>"><mm:field name="workgroups.name"/></option>
    </mm:list>
  </select>
  <br/>
  <di:translate key="pop.selectstudent" /><br/>
  <select name="student" class="popteacherformselect" onChange="teacherform.whatselected.value='student';teacherform.submit()">
    <option value=0><di:translate key="pop.allstudents" /></option>
    <mm:list nodes="$user" path="people1,classes,people2,roles" fields="people2.number" orderby="people2.lastname" 
        directions="UP" distinct="true" constraints="roles.name='student'">
      <mm:import id="dummy" reset="true"><mm:field name="people2.number"/></mm:import>
      <option value="<mm:write referid="dummy"/>" <mm:compare referid="student" referid2="dummy">selected</mm:compare>><mm:field name="people2.firstname"/> <mm:field name="people2.lastname"/></option>
    </mm:list>
  </select>
</mm:compare>
<br/>
<br/>
<input type="button" class="formbutton" onClick="teacherform.whatselected.value='student';teacherform.submit()" value="<di:translate key="pop.searchbutton" />"> 
<input type="button" class="formbutton" onClick="teacherform.whatselected.value='0';teacherform.submit()" value="<di:translate key="pop.resetbutton" />"> 
</form>
<br/>
<br/>

<mm:compare referid="whatselected" value="0" inverse="true">
<mm:compare referid="whatselected" value="student" inverse="true">
  <mm:import id="currentfolder" reset="true">1</mm:import>
</mm:compare>
<mm:compare referid="whatselected" value="student">
<%-- folder is open --%>
<mm:compare referid="currentfolder" value="-1">
  <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderopened" />" alt="<di:translate key="pop.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="currentfolder" value="-1" inverse="true">
  <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderclosed" />" alt="<di:translate key="pop.folderclosed" />" />
</mm:compare>
<a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,whatselected,wgroup,class"/>"><di:translate key="pop.competencies" /></a><br/>

     <mm:node number="$student">
     	<mm:relatedcontainer path="pop,profiles">
     	  <mm:related>

            <mm:import id="currentnumber"><mm:field name="profiles.number"/></mm:import>

            <%-- folder is open --%>
            <mm:compare referid="currentprofile" referid2="currentnumber">
               &nbsp;<img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderopened" />" alt="<di:translate key="pop.folderopened" />" />
            </mm:compare>

            <%-- folder is closed --%>
            <mm:compare referid="currentprofile" referid2="currentnumber" inverse="true">
              &nbsp;<img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$popreferids"/>"  title="<di:translate key="pop.folderclosed" />" alt="<di:translate key="pop.folderclosed" />" />
            </mm:compare>

            <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,whatselected,wgroup,class">
                <mm:param name="currentprofile"><mm:field name="profiles.number" /></mm:param>
		        </mm:treefile>">
			  <mm:field name="profiles.name" />
            </a><br />

          </mm:related>
     	</mm:relatedcontainer>
     </mm:node>
</mm:compare>
<%-- folder is open --%>
<mm:compare referid="currentfolder" value="1">
  <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderopened" />" alt="<di:translate key="pop.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="currentfolder" value="1" inverse="true">
  <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderclosed" />" alt="<di:translate key="pop.folderclosed" />" />
</mm:compare>
<a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,whatselected,wgroup,class">
    <mm:param name="currentfolder">1</mm:param>
  </mm:treefile>"><di:translate key="pop.progressmonitor" />
</a><br/>

<mm:compare referid="whatselected" value="student">
<%-- folder is open --%>
<mm:compare referid="currentfolder" value="2">
  <img src="<mm:treefile page="/pop/gfx/mapopen.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderopened" />" alt="<di:translate key="pop.folderopened" />" />
</mm:compare>
<%-- folder is closed --%>
<mm:compare referid="currentfolder" value="2" inverse="true">
  <img src="<mm:treefile page="/pop/gfx/mapdicht.gif" objectlist="$includePath" referids="$popreferids"/>" title="<di:translate key="pop.folderclosed" />" alt="<di:translate key="pop.folderclosed" />" />
</mm:compare>
<a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,whatselected,wgroup,class">
    <mm:param name="currentfolder">2</mm:param>
  </mm:treefile>"><di:translate key="pop.todoitems" />
</a><br />
</mm:compare>
</mm:compare>

  </div>
</div>

</mm:cloud>
</mm:content>
