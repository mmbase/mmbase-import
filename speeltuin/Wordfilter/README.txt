These java source files clean node fields with HTML content.
The tags which are still present in the fields after the cleanup are:
	<table>, <tr>, <td>, <p>, <br>
	<b>, <i>, <u>, <strong>
	<ul>, <ol>, <li>
	<a>

How to use
----------------
The builder.xml should add org.mmbase.application.wordfilter.BasicBuilder
inside the classfile tag or a derived class from BasicBuilder.

The property "cleanfields" defines the fields to be cleaned.

   <properties>
       <property name="cleanfields">intro,body</property>
   </properties>