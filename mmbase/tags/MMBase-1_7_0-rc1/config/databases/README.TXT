Tags to be used in database descriptions

$Id: README.TXT,v 1.1 2003-04-18 13:48:36 michiel Exp $
--------------------------------------------------------------------------------
	 <mmbasedriver>
				Defines the database driver that handles the abstraction between
				mmbase and the given jdbs/database we are using.
--------------------------------------------------------------------------------
	 <sqlhandler>
				Defines the SqlHandler that converts SearchQuery objects to sql statements.
--------------------------------------------------------------------------------
	<chainedsqlhandler>
				Defines the chained SqlHandler that extend the sqlhandler's functionality.
				This enables database extensions to be used.
--------------------------------------------------------------------------------
    <scheme>
				Defines the strings for the different scheme database
				statements.

        <create>  (storage)
    create scheme 
    Defines a statement for creating a non-extended table.
    Parameters:
    {0} the name of the table name to create.
    {1} the list of field definitions (seperated by commas)

		<create-extended) (storage) 
    create-extended scheme
    Defines a statement for creating a table that extends from anotehr table
    Parameters:
    {0} the name of the table to create.
    {1} the list of field definitions (seperated by commas)
    {2} the name of the table to extend

		<not-null> (storage) 
    Defines the code for creating a non-nullable field
    Parameters:
    {0} the name of the field to create.
    {1} the field type definitions (i.e. varchar(30))
    NOTE: if you ommit this scheme all fields are nullable


		<primary-key> (storage)
    Defines the code for creating a field that is also a unique secondary key
    Parameters:
    {0} the name of the field to create.
    {1} the field type definitions (i.e. int(11))
    NOTE: if you ommit this scheme no secondary keys are created


       <foreign-key> (storage)

    Defines the code for creating a field that is a secondary (or foreign) key
    {0} the name of the field to create.
    {1} the field type definitions (i.e. int(11))
    NOTE: if you ommit this scheme no foreign keys are created

		<key> (storage)
    Defines the code for creating a field that is also a unique secondary key
    Parameters:
    {0} the name of the field to create.
    {1} the field type definitions (i.e. int(11))
    NOTE: if you ommit this scheme no secondary keys are created

--------------------------------------------------------------------------------
    <mapping>
				defines how mmbase should map the different types mmbase to this
				database.

				mmbase-type, the internal mmbase datatypes allways in caps
				min-size, minimal size this value is valid for
				max-size, maximum size this value is valid for
--------------------------------------------------------------------------------
	
    <disallowed>
				defines disallowed fields, tables etc etc for this database
				if possible it also explains to mmbase what todo with it.
				illegal fieldnames for example will be automapped to a
				replacement if one is defined. This way apps made for mmbase
				don't need to be changes when running on a different database.
