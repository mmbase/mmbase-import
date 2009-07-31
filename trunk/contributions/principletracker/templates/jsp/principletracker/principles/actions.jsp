<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="basic user">
<mm:import externid="action" />
<mm:compare value="updateprinciple" referid="action">
	<mm:import externid="principleset" jspvar="principleset" />
	<mm:import externid="principlerelid" />
	<mm:import externid="principleid" />
	<mm:import externid="newversion" />
	<mm:import externid="newqualification" />
	<mm:import externid="newname" />
	<mm:import externid="newexplanation" />
	<mm:import externid="newargumentation" />
	<mm:import externid="newconsequence" />
	<mm:import externid="newallowedimpl" />
	<mm:import externid="newsource" />
	<mm:import externid="newprinciplenumber" />
	<mm:import externid="newtheme" />
	<mm:import externid="newstate" />
	<mm:node referid="principlerelid"><mm:field id="oldstate" name="state" write="false" /></mm:node>
	<mm:import externid="autoversion">false</mm:import>
	<mm:compare referid="autoversion" value="true">
	        <mm:compare referid="oldstate" referid2="newstate">
		    <mm:import id="newversion" reset="true"><mm:function set="principletracker" name="getNextPatchLevel" referids="newversion@version" /></mm:import>
		</mm:compare>
        </mm:compare>
	<mm:node referid="principleid">
	  <mm:field name="version">
          <mm:compare referid2="newversion">
	  <mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
	  <mm:setfield name="qualification"><mm:write referid="newqualification" /></mm:setfield>
	  <mm:setfield name="name"><mm:write referid="newname" /></mm:setfield>
	  <mm:setfield name="explanation"><mm:write referid="newexplanation" /></mm:setfield>
	  <mm:setfield name="argumentation"><mm:write referid="newargumentation" /></mm:setfield>
	  <mm:setfield name="consequence"><mm:write referid="newconsequence" /></mm:setfield>
	  <mm:setfield name="allowedimpl"><mm:write referid="newallowedimpl" /></mm:setfield>
	  <mm:setfield name="source"><mm:write referid="newsource" /></mm:setfield>
	  <mm:setfield name="principlenumber"><mm:write referid="newprinciplenumber" /></mm:setfield>
	  <mm:setfield name="theme"><mm:write referid="newtheme" /></mm:setfield>
	   <mm:compare referid="oldstate" referid2="newstate" inverse="true">
	   <mm:node referid="principlerelid">
		<mm:setfield name="state"><mm:write referid="newstate" /></mm:setfield>
	   </mm:node>
	   </mm:compare>
          </mm:compare>
          <mm:compare referid2="newversion" inverse="true">
          <!--- create a new principle based on the old one -->
	  <mm:createnode id="newprinciplenode" type="principle">
	  <mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
	  <mm:setfield name="qualification"><mm:write referid="newqualification" /></mm:setfield>
	  <mm:setfield name="name"><mm:write referid="newname" /></mm:setfield>
	  <mm:setfield name="explanation"><mm:write referid="newexplanation" /></mm:setfield>
	  <mm:setfield name="argumentation"><mm:write referid="newargumentation" /></mm:setfield>
	  <mm:setfield name="consequence"><mm:write referid="newconsequence" /></mm:setfield>
	  <mm:setfield name="allowedimpl"><mm:write referid="newallowedimpl" /></mm:setfield>
	  <mm:setfield name="source"><mm:write referid="newsource" /></mm:setfield>
	  <mm:setfield name="principlenumber"><mm:write referid="newprinciplenumber" /></mm:setfield>
	  <mm:setfield name="theme"><mm:write referid="newtheme" /></mm:setfield>
	  </mm:createnode>

          <!--- create a relation between the new principle and the set -->
	  <mm:node id="principlesetnode" referid="principleset" />
	  <mm:createrelation id="newprinciplerelnode" source="principlesetnode" destination="newprinciplenode" role="principlerel">
		<mm:setfield name="state"><mm:write referid="newstate" /></mm:setfield>
          </mm:createrelation>

          <!-- if the old node was active then archive it -->
          <mm:compare referid="newstate" value="active">
	  <mm:node referid="principlerelid">
            <mm:field name="state">
              <mm:compare value="active">
		<mm:setfield name="state">archived</mm:setfield>
              </mm:compare>
            </mm:field>
          </mm:node>
	  </mm:compare>

	  <!-- we have a new node so jump to that one -->
	  <mm:import id="tnn" jspvar="tnn"><mm:node referid="newprinciplenode"><mm:field name="number" /></mm:node></mm:import>
	  <mm:import id="tnr" jspvar="tnr"><mm:node referid="newprinciplerelnode"><mm:field name="number" /></mm:node></mm:import>
	  <%response.sendRedirect("index.jsp?main=principles&sub=principle&principleset="+principleset+"&principlerelid="+tnr+"&principleid="+tnn);%>

	  </mm:compare>
	  </mm:field>
	</mm:node>
</mm:compare>


<mm:compare value="createprinciple" referid="action">
	<mm:import externid="principleset" jspvar="principleset" />
	<mm:import externid="newversion" />
	<mm:import externid="newqualification" />
	<mm:import externid="newname" />
	<mm:import externid="newexplanation" />
	<mm:import externid="newargumentation" />
	<mm:import externid="newconsequence" />
	<mm:import externid="newallowedimpl" />
	<mm:import externid="newsource" />
	<mm:import externid="newprinciplenumber" />
	<mm:import externid="newtheme" />
	<mm:import externid="newstate" />

          <!--- create a new principle  -->
	  <mm:createnode id="newprinciplenode" type="principle">
	  <mm:setfield name="version"><mm:write referid="newversion" /></mm:setfield>
	  <mm:setfield name="qualification"><mm:write referid="newqualification" /></mm:setfield>
	  <mm:setfield name="name"><mm:write referid="newname" /></mm:setfield>
	  <mm:setfield name="explanation"><mm:write referid="newexplanation" /></mm:setfield>
	  <mm:setfield name="argumentation"><mm:write referid="newargumentation" /></mm:setfield>
	  <mm:setfield name="consequence"><mm:write referid="newconsequence" /></mm:setfield>
	  <mm:setfield name="allowedimpl"><mm:write referid="newallowedimpl" /></mm:setfield>
	  <mm:setfield name="source"><mm:write referid="newsource" /></mm:setfield>
	  <mm:setfield name="principlenumber"><mm:write referid="newprinciplenumber" /></mm:setfield>
	  <mm:setfield name="theme"><mm:write referid="newtheme" /></mm:setfield>
	  </mm:createnode>

          <!--- create a relation between the new principle and the set -->
	  <mm:node id="principlesetnode" referid="principleset" />
	  <mm:createrelation id="newprinciplerelnode" source="principlesetnode" destination="newprinciplenode" role="principlerel">
		<mm:setfield name="state"><mm:write referid="newstate" /></mm:setfield>
          </mm:createrelation>

	  <!-- we have a new node so jump to that one -->
	  <mm:import id="tnn" jspvar="tnn"><mm:node referid="newprinciplenode"><mm:field name="number" /></mm:node></mm:import>
	  <mm:import id="tnr" jspvar="tnr"><mm:node referid="newprinciplerelnode"><mm:field name="number" /></mm:node></mm:import>
	  <%response.sendRedirect("index.jsp?main=principles&sub=principle&principleset="+principleset+"&principlerelid="+tnr+"&principleid="+tnn);%>

</mm:compare>

<mm:compare value="deleteprinciple" referid="action">
	<mm:import externid="deleteallversions">false</mm:import>
	<mm:import externid="deleteolderversions">false</mm:import>
	<mm:import externid="deleteolderpatchlevels">false</mm:import>
	<mm:compare referid="deleteallversions" value="true">
	<mm:import externid="principleid" />
	<mm:import externid="principleset" />
	<mm:node referid="principleid">
	   ** deleted principle <mm:field name="principlenumber" id="principlenumber" /> **
	    <mm:listcontainer path="principlesets,principlerel,principle" fields="principle.principlenumber,principle.name,principlerel.state,principlesets.number">
            <mm:constraint field="principlesets.number" operator="EQUAL" value="$principleset" />	
	    <mm:constraint field="principle.principlenumber" operator="EQUAL" value="$principlenumber" />
	     <mm:list>
		<mm:node element="principle">
			<mm:deletenode deleterelations="true" />
		</mm:node>
             </mm:list>
            </mm:listcontainer>
	 </mm:node>
	</mm:compare>
	<mm:compare referid="deleteallversions" value="false">
	  <mm:compare referid="deleteolderversions" value="true">
		** delete older versions **
	    <mm:import externid="principleid" />
	    <mm:import externid="principleset" />
	    <mm:node referid="principleid">
	    <mm:field name="principlenumber" id="principlenumber" write="false"/> 
	    <mm:field name="version" id="currentversion" write="false" /> 
	    <mm:listcontainer path="principlesets,principlerel,principle" fields="principle.principlenumber,principle.name,principlerel.state,principlesets.number">
            <mm:constraint field="principlesets.number" operator="EQUAL" value="$principleset" />
	    <mm:constraint field="principle.principlenumber" operator="EQUAL" value="$principlenumber" />
	     <mm:list>
		<mm:node element="principle">
			<mm:import id="checkversion"><mm:field name="version" /></mm:import>
			<mm:import id="older" reset="true"><mm:function set="principletracker" name="isOlderVersion" referids="currentversion,checkversion" /></mm:import>
			a=<mm:write referid="older" />
			<mm:compare referid="older" value="older">
			   <mm:deletenode deleterelations="true" />
			</mm:compare>
		</mm:node>
             </mm:list>
            </mm:listcontainer>
	    </mm:node>
	  </mm:compare>
	  <mm:compare referid="deleteolderversions" value="false">
	  	<mm:compare referid="deleteolderpatchlevels" value="true">
			** deleted patchlevels **
	                <mm:import externid="principleid" />
	                <mm:import externid="principleset" />
	                <mm:node referid="principleid">
	                <mm:field name="principlenumber" id="principlenumber" write="false"/> 
	                <mm:field name="version" id="currentversion" write="false" /> 
	                <mm:listcontainer path="principlesets,principlerel,principle" fields="principle.principlenumber,principle.name,principlerel.state,principlesets.number">
		        <mm:constraint field="principlesets.number" operator="EQUAL" value="$principleset" />
	                <mm:constraint field="principle.principlenumber" operator="EQUAL" value="$principlenumber" />
	                 <mm:list>
		            <mm:node element="principle">
			           <mm:import id="checkversion"><mm:field name="version" /></mm:import>
			          <mm:import id="older" reset="true"><mm:function set="principletracker" name="isOlderPatchLevel" referids="currentversion,checkversion" /></mm:import>
		          	  <mm:compare referid="older" value="older">
			             <mm:deletenode deleterelations="true" />
			          </mm:compare>
		          </mm:node>
                       </mm:list>
                      </mm:listcontainer>
	           </mm:node>
		</mm:compare>
	  	<mm:compare referid="deleteolderpatchlevels" value="false">
		<mm:import externid="principleid" />
		<mm:deletenode referid="principleid" deleterelations="true" />
		** deleted principle **
		</mm:compare>
	  </mm:compare>
	</mm:compare>
</mm:compare>
</mm:cloud>
