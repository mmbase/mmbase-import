# ################
# PermissionControl
# ################
PermissionControl.deleteRoleValuesByRoleId = DELETE FROM jforum_role_values WHERE role_id IN (?)

PermissionControl.getRoleIdsByGroup = SELECT DISTINCT rv.role_id \
	FROM jforum_role_values rv, jforum_roles r \
	WHERE r.role_id = rv.role_id \
	AND r.group_id = ?