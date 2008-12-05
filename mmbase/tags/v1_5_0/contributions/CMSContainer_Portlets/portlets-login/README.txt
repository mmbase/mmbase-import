DESCRIPTION:
  Login portlet
  check the <prefix>_portletparameter ,be sure that the value of field "m_value" is varchar (4000),if not ,run the following script to do it
  alter table <prefix>_portletparameter modify  m_value varchar(4000)

