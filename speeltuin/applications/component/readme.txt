Application Component
Date: 21-febr-2006

This application shows the outline of how components are implemented in Didactor.

Contents:

src\core
--> the components implementation

src\education\templates\educations\wizards\code.jsp
--> example jsp that contains pieces of code like:

<mm:node number="component.isbo" notfound="skip">
      ....
</mm:node>

<mm:import externid="mode">components</mm:import>
...
<mm:compare referid="mode" value="content_metadata">
      ....
</mm:compare>

src\education\templates\editwizards\data\config\attachment
--> the basic attachment editwizard

src\email\config\components\email.xml
--> component specific settings for the email component

src\metadata\config\applications
-> the metadata component including the metadata component object, which is created at install

src\competence\templates\editwizards\data\config\education
-> extensions to the attachment editwizard in case the metadata component is installed
