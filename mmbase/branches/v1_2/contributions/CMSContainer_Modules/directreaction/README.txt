DESCRIPTION:
This module allows users to make reactions on a piece of content.
There also is a backend screen in which reactions on the live server can be removed.



CONFIGURATION:
Voeg "struts-reactions.xml" toe bij de struts init-param "config", zoals hieronder:
		<init-param>
			<param-name>config</param-name>
			<param-value>...other struts config files...,/WEB-INF/struts-reactions.xml</param-value>
		</init-param>

TODO:

- Deze module gebruikt het can_react veld zoals gebruikt in Nijmegen bij article en newsitem. Mooier zou zijn als dat veldje naar CMSContainer verhuist, bijvoorbeeld bij contentelement.
- De bundle cmsc-reactions.properties gebruikt sommige algemene beschrijvingen die gecopieerd zijn uit cmsc-repository.properties. De JSP's zouden beiden moet kunnen gebruiken.
- Icoontje in selector.jsp
