<%-- dit is een test
	we gaan nu even de variable directive parser testen
--%>
<%@ tag   	
	display-name="some name"
	body-content="scriptless"
	dynamic-attributes="att1,att2"
	small-icon="bla/image1.jpg"
	large-icon="la/image2.jpg"
	description="dit is een mooi verhaal \nEn dat is zo." 
	example="example"
	language="java" 
	import="java.disco.*, java.punkrock.*" ]
	pageEncoding="utf-8"
	isELIgnored="false"	
%>
<%@variable name-given="een" 
	name-from-attribute= "twee"
	alias="drie"
	variable-class="java.util.List"
	declare="true"
	scope="AT_End"
	description="vier" 
	%>