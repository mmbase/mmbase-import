# @version: $Id: mysql_40.sql,v 1.1 2008-01-17 07:48:24 mguo Exp $
# ################
# PermissionControl
# ################
PermissionControl.deleteAllRoleValues = DELETE jforum_role_values \
	FROM jforum_role_values rv, jforum_roles r \
	WHERE r.role_id = rv.role_id \
	AND r.group_id = ?
