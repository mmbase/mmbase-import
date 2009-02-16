# @version: $Id: mysql_40.sql,v 1.3 2008-07-03 12:02:59 kevinshen Exp $
# ################
# PermissionControl
# ################
PermissionControl.deleteAllRoleValues = DELETE jforum_role_values \
	FROM jforum_role_values rv, jforum_roles r \
	WHERE r.role_id = rv.role_id \
	AND r.group_id = ?
