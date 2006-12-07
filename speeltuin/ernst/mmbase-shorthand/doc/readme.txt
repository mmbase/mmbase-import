MMBase shorthand

introduction
MMBase shorthand was created to easily create MMBase applications and prototypes. It allows you to define all your builders, reldefs and typerels in one single xml file. Values are as much as possible derived, or reasonable defaults are used. Although you don't have to, most defaults are overridable, and the idea is that you can configure every thing in MMBase shorthand that you can configure in an apps1 xml file and the builder files. There is one notable exception. The MMBase builder file format allows for i19n by letting you define gui names and descriptions in multiple languages. This may be a nice feature for very general builders, that will be used in many projects, but it is not very interesting for builders that are developed with a specific project in mind. MMBase shorthand will take one locale, and all labels will be created for that locale.

what is it:
MMBase shorthand is an xml format (with schema validation) for describing an MMBase apps1 application. There is an xsl stylesheet to transform such files into an apps1 xml file and xml files for all the builders.

motivation:
I like to have a tool that liberates me from the tedious process of creating all the mmbase configuration files. On the other hand i find i use quite a lot of custom builders for MMBase projects, so reuse of previous builders is not always optimal. Therefore it is nice to have a tool that allows you to quickly set up a cloud. You can allso incorporate the generation of the application into the build process, which results in efficient iterative development.

versions and supported features:
MMBase shorthand aims to be used with MMBase 8, and generates files of the latest specification.
application: <!DOCTYPE application PUBLIC "-//MMBase//DTD application config 1.1//EN
All features are currently supported with the following exceptions:
- dependencies (will be implemented)

builders: http://www.mmbase.org/xmlns/builder (schema name space)
All features are currently supported with the following exceptions:
- properties (will be implemented)
- indexes (will be implemented)
- data types: the anonymous data types in field definitions are implemented, but global detatype definitions in builders does not seem useful to me. Better configure  those in config/datatypes.xml  (will not be implemented)

usage:

creating the application xml

The MMBase shorthand format is quite simple, it has a few elements, some mandatory attributes, and a lot of optional attributes that allow you to override derived or default values.
It is best to study the schema file, or better, use a schema aware xml editor, and the syntax will largely explain itself.

elements:

application (root)
--------------------------
mandatory attributes:
-name
-default language (used in the xml:lang attributes in builders)

optional attributes:
-class default base class for all builders, can be locally overridden(default: none)
-maintainer (default: mmbase.org)
-version (default: 1.0)
-description (default: name)
-autodeploy (default: false)

reldef  (child of application)
--------------------------------
This element is not mandatory, but it allows you to add more information to reldef configurations. The actual list of reldefs (neededreldeflist) is derived from the 'relation' elements (which also define the typerels).
If you want a relation to use an other builder than insrel, you have to create a reldef element for it, and at least suppy a builder attribute.
if a relation with 'related' as role is found but no reldef with 'insrel' as builder, an entry for insrel is created in the relationsource list of the application file. The same goes for posrel. So you dont have to define the relation roles 'related' or 'posrel' with reldef elements. For posrel you must define a builder element though (unless that builder is aleready available).

mandatory attributes:
- role

optional attributes:
- unidirection [true|false] (default: false)
- guiname (default: role)
- builder (default: insrel)

builder (child of application)
--------------------------------
mandatory attributes:
- name

optional attributes:
-searchage (default 3000)
-version (default 1)
-extends (default: Object)
-create [true|false](default is true). If false no builder file will be created for this builder element, but it will be used for the apps1 file.
-status ([active|inactive] default none).
-class (default none) Can be globally overridden at application level.
-sname singular name in default language (default builder name)
-pname pleural name in default language (default builder name)


field (child of builder)
--------------------------------
about the fieldpositions in input, seach and list views:
there are three ways to define them. they are checked for in the below order:

1 attributes input, search and list: when these are set they are used.

2 editor attribute. here you can set flags for in what lists you want this field.
'i' for input, 's' for search and 'l' for list. if the flag is set, the position will be the 
position of the field element in the field list.
Example: editor=?li? will create positive values for the input and list elements, and '-1' for the search element.

3 default: input, list and search are enabled, the position will be the 
position of the field element in the field list.

about notnull and key attributes of the type element:
These values are bundled in a single attribute: flags.
n = notnull (default is false)
k = key (default false)

type and size:
both type and size can be specified, but reasonable defaults are derived in the following way:

field types:

data types: text, line, string, eline, field, md5password, confirmpassword, dutch-zipcode, emailaddress will default to type STRING
data types: datetime,created,lastmodified,time,date,20th-century,20th-century-pedantic,birthdate,living-birthdate,historical,weeknumbers,eventtime will default DATETIME
data types: long, duration will default to LONG
data types: boolean, yesno, onoff will default to BOOLEAN
data types: filesize,weekday,byte,hour_of_day,minute_of_hour will default to INTEGER
data type: binary will default to BINARY
data types: node, nodenumber will default to NODE
data types: xml, xmlfield, html will default to XML

field sizes:

data types: string, field, html, xml, xmlfield will default to 3000
data types: line, or eline, or trimmedline will default to 300
data types: boolean, yesno, onoff wil default to 1
data type: binary will default to 16777215 (16mb)
any other datatype will default to 50.
Of course when you use custom data types you must set both type and size attributes.

mandatory attributes:
- name
- datatype (or a nested datatype element)

optional attributes:
-description (default: none)
-guiname    (default: name)
-type       (default: datatype in capitals, for fields like string and integer)
-editor     [i=input, l=list, s=seach]
-input      position field in input 
-search     position field in search
-list       position field in list
-state      (default: persistent)
-size       (default: 3000 for datatypes string and field, 300 for datatypes line, eline, trimmedline, otherwise 50)

relation (child of builder)
[TODO]



using the build

There are twoo properties you have to define:
-application: which contains the name of the mmbase application matching with the applicaton > name attribute.
-inputfile: pointing to the xml file.
It is easiest to create a build.properties file and set these properties there, or when you call the ant script from an other ant script just include them as an property elements.
The first time xalan is being downloaded so that should take a while.

The default ant task will perform the downloaded, compilation and transformation



Ernst Bunders
18-12-2006
