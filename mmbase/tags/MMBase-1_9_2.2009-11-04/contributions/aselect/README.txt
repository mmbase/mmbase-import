
This is an 'ASelect' (http://www.a-select.org) authentication implementation for 
MMBase (http://www.mmbase.org)

- needs MMBase.1.8.0, aselect 1.4.1

- It can work with or without the aselect agent.

- For Authorisation you can use: 

  1. 'org.mmbase.security.implementation.basic.OwnerAuthorisation':
     - you can only edit nodes which you created yourself
     - every 'basic user' can create nodes
     - all 'possible contexts' are defined by the 'accounts.properties' of OwnerAuthorisation 
       (so only the keys are of importance, the values are ignored)

     - accounts.properties can also be used to give alternative ranks (during Authentication)
     - ranks.properties can be used to create new ranks (during Authentication)

  2. 'org.mmbase.security.implementation.cloudcontext.Verify':
     - See cloudcontext security.
     - If mmbaseuser object does not exist, but use is authenicated by A-Select, the user object
       will be created with default rights. The editors of cloud context security can be used to
       grant rights to the user.

- See documentation/mmbase-aselect.html for documentation	

- To build, place org.aselect.system.jar in this dir.


2005-01-32
Michiel Meeuwwissen 

