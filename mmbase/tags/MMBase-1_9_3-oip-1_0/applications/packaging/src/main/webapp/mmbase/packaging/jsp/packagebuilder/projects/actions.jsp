<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />

<mm:compare value="packagtarget" referid="action">
	<mm:import externid="createnew" vartype="list" />
	A='<mm:write referid="createnew" />'
</mm:compare>


<mm:compare value="packagetarget" referid="action">
	<mm:functioncontainer>
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="target" />
	<mm:import externid="createnew" vartype="list" />
	<mm:import externid="latest" vartype="list" />
	<mm:import externid="newversion" />
	<mm:import externid="publishprovider" />
	<mm:import externid="publishstate" />
	<mm:import externid="publishsharepassword" />
	 <mm:booleanfunction set="mmpb" name="packageTarget" referids="project,target,newversion,latest,createnew,publishprovider,publishstate,publishsharepassword">
	</mm:booleanfunction>
	</mm:functioncontainer>
</mm:compare>


<mm:compare value="publishmodeon" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="target" />
	<mm:import externid="publishprovider" />
	<mm:import externid="publishsharepassword" />
	 <mm:booleanfunction set="mmpb" name="setPublishModeOn" referids="project,target,publishprovider,publishsharepassword" />
</mm:compare>

<mm:compare value="publishmodeoff" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="target" />
	 <mm:booleanfunction set="mmpb" name="setPublishModeOff" referids="project,target" />
</mm:compare>

<mm:compare value="setincludedversion" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	 <mm:import externid="bid" id="id" />
	 <mm:import externid="newversion" />
	 <mm:booleanfunction set="mmpb" name="setIncludedVersion" referids="project,target,id,newversion">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagedescription" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newdescription" />
	 <mm:booleanfunction set="mmpb" name="setPackageDescription" referids="project,target,newdescription">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="setpackagename" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newname" />
	 <mm:booleanfunction set="mmpb" name="setPackageName" referids="project,target,newname">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagevalue" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newname" />
	 <mm:import externid="newvalue" />
	 <mm:booleanfunction set="mmpb" name="setPackageValue" referids="project,target,newname,newvalue">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="changeprojectsettings" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="newname" externid="newname" />
	<mm:import id="newpath" externid="newpath" />
	 <mm:booleanfunction set="mmpb" name="changeProjectSettings" referids="project,newname,newpath">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagemaintainer" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newmaintainer" />
	 <mm:booleanfunction set="mmpb" name="setPackageMaintainer" referids="project,target,newmaintainer">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagelicenseversion" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newlicenseversion" />
	 <mm:booleanfunction set="mmpb" name="setPackageLicenseVersion" referids="project,target,newlicenseversion">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagelicensetype" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newlicensetype" />
	 <mm:booleanfunction set="mmpb" name="setPackageLicenseType" referids="project,target,newlicensetype">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagelicensename" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newlicensename" />
	 <mm:booleanfunction set="mmpb" name="setPackageLicenseName" referids="project,target,newlicensename">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackageinitiator" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newname" />
	 <mm:import externid="oldname" />
	 <mm:import externid="newcompany" />
	 <mm:import externid="oldcompany" />
	 <mm:booleanfunction set="mmpb" name="setPackageInitiator" referids="project,target,oldname,newname,oldcompany,newcompany">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addpackageinitiator" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newname" />
	 <mm:import externid="newcompany" />
	 <mm:booleanfunction set="mmpb" name="addPackageInitiator" referids="project,target,newname,newcompany">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addpackagescreenshot" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newname" />
	 <mm:import externid="newfile" />
	 <mm:import externid="newdescription" />
	 <mm:booleanfunction set="mmpb" name="addPackageScreenshot" referids="project,target,newname,newfile,newdescription">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="delpackageinitiator" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="oldname" />
	 <mm:import externid="oldcompany" />
	 <mm:booleanfunction set="mmpb" name="delPackageInitiator" referids="project,target,oldname,oldcompany">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="setpackagedeveloper" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newname" />
	 <mm:import externid="oldname" />
	 <mm:import externid="newcompany" />
	 <mm:import externid="oldcompany" />
	 <mm:booleanfunction set="mmbm" name="setPackageDeveloper" referids="project,target,oldname,newname,oldcompany,newcompany">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addpackagedeveloper" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newname" />
	 <mm:import externid="newcompany" />
	 <mm:booleanfunction set="mmpb" name="addPackageDeveloper" referids="project,target,newname,newcompany">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="delpackagedeveloper" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="oldname" />
	 <mm:import externid="oldcompany" />
	 <mm:booleanfunction set="mmpb" name="delPackageDeveloper" referids="project,target,oldname,oldcompany">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagecontact" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newreason" />
	 <mm:import externid="oldreason" />
	 <mm:import externid="newname" />
	 <mm:import externid="oldname" />
	 <mm:import externid="newemail" />
	 <mm:import externid="oldemail" />
	 <mm:booleanfunction set="mmpb" name="setPackageContact" referids="project,target,oldreason,newreason,oldname,newname,oldemail,newemail">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addpackagecontact" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newreason" />
	 <mm:import externid="newname" />
	 <mm:import externid="newemail" />
	 <mm:booleanfunction set="mmpb" name="addPackageContact" referids="project,target,newreason,newname,newemail">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="delpackagecontact" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="oldreason" />
	 <mm:import externid="oldname" />
	 <mm:import externid="oldemail" />
	 <mm:booleanfunction set="mmpb" name="delPackageContact" referids="project,target,oldreason,oldname,oldemail">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagesupporter" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newcompany" />
	 <mm:import externid="oldcompany" />
	 <mm:booleanfunction set="mmpb" name="setPackageSupporter" referids="project,target,oldcompany,newcompany">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addpackagesupporter" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="newcompany" />
	 <mm:booleanfunction set="mmpb" name="addPackageSupporter" referids="project,target,newcompany">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="delpackagesupporter" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="oldcompany" />
	 <mm:booleanfunction set="mmpb" name="delPackageSupporter" referids="project,target,oldcompany">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="delincludedpackage" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:import externid="bid" id="id" />
	 <mm:booleanfunction set="mmpb" name="delIncludedPackage" referids="project,target,id">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="deltarget" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	 <mm:booleanfunction set="mmpb" name="delTarget" referids="project,target">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="delproject" referid="action">
	<mm:import id="project" externid="name" />
	 <mm:booleanfunction set="mmpb" name="delProject" referids="project">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="addtargetpackage" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import id="target" externid="bundle" />
	<mm:notpresent referid="target"><mm:import id="target" externid="package" reset="true" /></mm:notpresent>
	<mm:import id="newpackage" externid="newpackage" />
	 <mm:booleanfunction set="mmpb" name="addTargetPackage" referids="project,target,newpackage">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addproject" referid="action">
	<mm:import externid="newprojectname" />
	<mm:import externid="newprojectpath" />
	 <mm:booleanfunction set="mmpb" name="addProject" referids="newprojectname,newprojectpath">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addbundletarget" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import externid="newtargetname" />
	<mm:import externid="newtargettype" />
	<mm:import externid="newtargetpath" />
	 <mm:booleanfunction set="mmpb" name="addBundleTarget" referids="project,newtargetname,newtargettype,newtargetpath">
	</mm:booleanfunction>
</mm:compare>


<mm:compare value="addpackagetarget" referid="action">
	<mm:import id="project" externid="name" />
	<mm:import externid="newtargetname" />
	<mm:import externid="newtargettype" />
	<mm:import externid="newtargetpath" />
	 <mm:booleanfunction set="mmpb" name="addPackageTarget" referids="project,newtargetname,newtargettype,newtargetpath">
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="addpackagedepends" referid="action">
        <mm:import id="project" externid="name" />
	<mm:import id="target" externid="package" />
	<mm:import externid="newversion">best</mm:import>
	<mm:import externid="newpackageid" />
         <mm:booleanfunction set="mmpb" name="addPackageDepends" referids="project,target,newpackageid,newversion">
        </mm:booleanfunction>
</mm:compare>


<mm:compare value="delpackagedepends" referid="action">
        <mm:import id="project" externid="name" />
	<mm:import id="target" externid="package" />
	<mm:import externid="delpackageid" />
	<mm:import externid="delversion" />
	<mm:import externid="delversionmode" />
         <mm:booleanfunction set="mmpb" name="delPackageDepends" referids="project,target,delpackageid,delversion,delversionmode">
        </mm:booleanfunction>
</mm:compare>


<mm:compare value="setpackagedepends" referid="action">
        <mm:import id="project" externid="name" />
	<mm:import id="target" externid="package" />
	<mm:import externid="packageid" />
	<mm:import externid="oldversion" />
	<mm:import externid="oldversionmode" />
	<mm:import externid="newversion" />
	<mm:import externid="newversionmode" />
         <mm:booleanfunction set="mmpb" name="setPackageDepends" referids="project,target,packageid,oldversion,oldversionmode,newversion,newversionmode">
        </mm:booleanfunction>
</mm:compare>


</mm:cloud>
