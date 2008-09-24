<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="modelfilename" id="prefix" />
<mm:import externid="sub" >none</mm:import>
<mm:import externid="id" >none</mm:import>
<mm:import externid="help" >on</mm:import>
<mm:import externid="name" />
<mm:import externid="package" />

<mm:nodefunction set="mmpb" name="getProjectInfo" referids="name">
        <mm:import id="dir"><mm:field name="dir" /></mm:import>
</mm:nodefunction>

<mm:import id="modelfilename"><mm:write referid="dir" /><mm:write referid="prefix" /></mm:import>


<mm:compare value="addneededbuilder" referid="action">
	<mm:import externid="newbuilder" />
	<mm:import externid="newmaintainer" />
	<mm:import externid="newversion" />
	<mm:booleanfunction set="mmpb" name="addNeededBuilder" referids="modelfilename,newbuilder,newmaintainer,newversion" />
</mm:compare>


<mm:compare value="deleteneededbuilder" referid="action">
	<mm:import externid="oldbuilder" />
	<mm:import externid="oldmaintainer" />
	<mm:import externid="oldversion" />
	<mm:booleanfunction set="mmpb" name="deleteNeededBuilder" referids="modelfilename,oldbuilder,oldmaintainer,oldversion" />
</mm:compare>

<mm:compare value="addneededreldef" referid="action">
	<mm:import externid="newsource" />
	<mm:import externid="newtarget" />
	<mm:import externid="newdirection" />
	<mm:import externid="newguisourcename" />
	<mm:import externid="newguitargetname" />
	<mm:import externid="newbuilder" />
	<mm:booleanfunction set="mmpb" name="addNeededRelDef" referids="modelfilename,newbuilder,newsource,newtarget,newdirection,newguisourcename,newguitargetname" />
</mm:compare>


<mm:compare value="deleteneededreldef" referid="action">
	<mm:import externid="oldsource" />
	<mm:import externid="oldtarget" />
	<mm:import externid="olddirection" />
	<mm:import externid="oldguisourcename" />
	<mm:import externid="oldguitargetname" />
	<mm:import externid="oldbuilder" />
	<mm:booleanfunction set="mmpb" name="deleteNeededRelDef" referids="modelfilename,oldbuilder,oldsource,oldtarget,olddirection,oldguisourcename,oldguitargetname" />
</mm:compare>

<mm:compare value="addallowedrelation" referid="action">
	<mm:import externid="newfrom" />
	<mm:import externid="newto" />
	<mm:import externid="newtype" />
	<mm:booleanfunction set="mmpb" name="addAllowedRelation" referids="modelfilename,newfrom,newto,newtype" />
</mm:compare>

<mm:compare value="deleteallowedrelation" referid="action">
	<mm:import externid="oldfrom" />
	<mm:import externid="oldto" />
	<mm:import externid="oldtype" />
	<mm:booleanfunction set="mmpb" name="deleteAllowedRelation" referids="modelfilename,oldfrom,oldto,oldtype" />
</mm:compare>

<mm:compare value="setbuilderdescription" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="language" />
	<mm:import externid="newdescription" />
	<mm:booleanfunction set="mmpb" name="setBuilderDescription" referids="modelfilename,builder,language,newdescription" />
</mm:compare>


<mm:compare value="setbuilderfielddescription" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="field" />
	<mm:import externid="language" />
	<mm:import externid="newdescription" />
	<mm:booleanfunction set="mmpb" name="setBuilderFieldDescription" referids="modelfilename,builder,field,language,newdescription" />
</mm:compare>


<mm:compare value="addbuilderfield" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="newdbname" />
	<mm:import externid="newdbtype" />
	<mm:import externid="newdbstate" />
	<mm:import externid="newdbsize" />
	<mm:booleanfunction set="mmpb" name="addBuilderField" referids="modelfilename,builder,newdbname,newdbtype,newdbstate,newdbsize" />
</mm:compare>


<mm:compare value="deleteneededbuilderfield" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="oldfield" />
	<mm:booleanfunction set="mmpb" name="deleteNeededBuilderField" referids="modelfilename,builder,oldfield" />
</mm:compare>


<mm:compare value="setbuilderfieldpositions" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="field" />
	<mm:import externid="newinputpos" />
	<mm:import externid="newsearchpos" />
	<mm:import externid="newlistpos" />
	<mm:booleanfunction set="mmpb" name="setBuilderFieldPositions" referids="modelfilename,builder,field,newinputpos,newsearchpos,newlistpos" />
</mm:compare>

<mm:compare value="setbuilderfielddbvalues" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="field" />
	<mm:import externid="newdbname" />
	<mm:import externid="newdbtype" />
	<mm:import externid="newdbstatus" />
	<mm:import externid="newdbsize" />
	<mm:import externid="newdbkey" />
	<mm:import externid="newdbnotnull" />
	<mm:booleanfunction set="mmpb" name="setBuilderFieldDBValues" referids="modelfilename,builder,field,newdbname,newdbtype,newdbstatus,newdbsize,newdbkey,newdbnotnull" />
</mm:compare>


<mm:compare value="setbuilderfieldguiname" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="field" />
	<mm:import externid="language" />
	<mm:import externid="newguiname" />
	<mm:booleanfunction set="mmpb" name="setBuilderFieldGuiName" referids="modelfilename,builder,field,language,newguiname" />
</mm:compare>

<mm:compare value="setbuildernames" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="language" />
	<mm:import externid="newsingularname" />
	<mm:import externid="newpluralname" />
	<mm:booleanfunction set="mmpb" name="setBuilderSingularName" referids="modelfilename,builder,language,newsingularname@newname" />
	<mm:booleanfunction set="mmpb" name="setBuilderPluralName" referids="modelfilename,builder,language,newpluralname@newname" />
</mm:compare>


<mm:compare value="setbuilderssc" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="newstatus" />
	<mm:import externid="newsearchage" />
	<mm:import externid="newclassname" />
	<mm:booleanfunction set="mmpb" name="setBuilderStatus" referids="modelfilename,builder,newstatus" />
	<mm:booleanfunction set="mmpb" name="setBuilderSearchAge" referids="modelfilename,builder,newsearchage" />
	<mm:booleanfunction set="mmpb" name="setBuilderClassName" referids="modelfilename,builder,newclassname" />
</mm:compare>


<mm:compare value="setbuildernmve" referid="action">
	<mm:import externid="builder" />
	<mm:import externid="newname" />
	<mm:import externid="newmaintainer" />
	<mm:import externid="newversion" />
	<mm:import externid="newextends" />
	<mm:booleanfunction set="mmpb" name="setBuilderMaintainer" referids="modelfilename,builder,newmaintainer" />
	<mm:booleanfunction set="mmpb" name="setBuilderVersion" referids="modelfilename,builder,newversion" />
	<mm:booleanfunction set="mmpb" name="setBuilderExtends" referids="modelfilename,builder,newextends" />
	<mm:booleanfunction set="mmpb" name="setBuilderName" referids="modelfilename,builder,newname" />
</mm:compare>

</mm:cloud>
