--
-- Groups
--
INSERT INTO jforum_groups (group_name, group_description ) VALUES ('General', 'General Users')
INSERT INTO jforum_groups (group_name, group_description ) VALUES ('Administration', 'Admin Users')

-- 
-- Users
--
INSERT INTO jforum_users (username, user_password, user_regdate) VALUES ('Anonymous', 'nopass', GETDATE())
INSERT INTO jforum_users (username, user_password, user_regdate) VALUES ('Admin', '21232f297a57a5a743894a0e4a801fc3', GETDATE())

--
-- User Groups
--
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (1, 1)
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (2, 2)

--
-- Roles
--
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_administration', 0)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_moderation', 0)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_moderation_post_remove', 0)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_moderation_post_edit', 0)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_moderation_topic_move', 0)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_moderation_topic_lockUnlock', 0)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_vote', 1)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_create_poll', 0)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_read_only_forums', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_anonymous_post', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_forum', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_category', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (1, 'perm_html_disabled', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

--
-- Admin
--
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_administration', 1)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_moderation', 1)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_moderation_post_remove', 1)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_moderation_post_edit', 1)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_vote', 1)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_create_poll', 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_read_only_forums', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_anonymous_post', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_forum', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_create_sticky_announcement_topics', 1)
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_moderation_topic_move', 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_category', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_moderation_topic_lockUnlock', 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_moderation_forums', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

INSERT INTO jforum_roles (group_id, name, role_type) VALUES (2, 'perm_html_disabled', 0)
INSERT INTO jforum_role_values (role_id, role_value, role_type) VALUES (SCOPE_IDENTITY(), 0, 1)

--
-- Smilies
--
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':)','<img src="#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif" alt=\":)\" />','3b63d1616c5dfcf29f8a7a031aaa7cad.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-)','<img src="#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif" alt=\":-)\" />','3b63d1616c5dfcf29f8a7a031aaa7cad.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':D','<img src="#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif" alt=\":D\" />','283a16da79f3aa23fe1025c96295f04f.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-D','<img src="#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif" alt=\":-D\" />','283a16da79f3aa23fe1025c96295f04f.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':(','<img src="#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif" alt=\":(\" />','9d71f0541cff0a302a0309c5079e8dee.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-(','<img src="#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif" alt=\":-(\" />','9d71f0541cff0a302a0309c5079e8dee.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-o','<img src="#CONTEXT#/images/smilies/47941865eb7bbc2a777305b46cc059a2.gif" alt=\":-o\" />','47941865eb7bbc2a777305b46cc059a2.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':shock:','<img src="#CONTEXT#/images/smilies/385970365b8ed7503b4294502a458efa.gif" alt=\":shock:\" />','385970365b8ed7503b4294502a458efa.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':?','<img src="#CONTEXT#/images/smilies/136dd33cba83140c7ce38db096d05aed.gif" alt=\":?\" />','136dd33cba83140c7ce38db096d05aed.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES ('8)','<img src="#CONTEXT#/images/smilies/b2eb59423fbf5fa39342041237025880.gif" alt=\"8)\" />','b2eb59423fbf5fa39342041237025880.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':lol:','<img src="#CONTEXT#/images/smilies/97ada74b88049a6d50a6ed40898a03d7.gif" alt=\":lol:\" />','97ada74b88049a6d50a6ed40898a03d7.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':x','<img src="#CONTEXT#/images/smilies/1069449046bcd664c21db15b1dfedaee.gif" alt=\":x\" />','1069449046bcd664c21db15b1dfedaee.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':P','<img src="#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif" alt=\":P\" />','69934afc394145350659cd7add244ca9.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':-P','<img src="#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif" alt=\":-P\" />','69934afc394145350659cd7add244ca9.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':oops:','<img src="#CONTEXT#/images/smilies/499fd50bc713bfcdf2ab5a23c00c2d62.gif" alt=\":oops:\" />','499fd50bc713bfcdf2ab5a23c00c2d62.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':cry:','<img src="#CONTEXT#/images/smilies/c30b4198e0907b23b8246bdd52aa1c3c.gif" alt=\":cry:\" />','c30b4198e0907b23b8246bdd52aa1c3c.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':evil:','<img src="#CONTEXT#/images/smilies/2e207fad049d4d292f60607f80f05768.gif" alt=\":evil:\" />','2e207fad049d4d292f60607f80f05768.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':twisted:','<img src="#CONTEXT#/images/smilies/908627bbe5e9f6a080977db8c365caff.gif" alt=\":twisted:\" />','908627bbe5e9f6a080977db8c365caff.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':roll:','<img src="#CONTEXT#/images/smilies/2786c5c8e1a8be796fb2f726cca5a0fe.gif" alt=\":roll:\" />','2786c5c8e1a8be796fb2f726cca5a0fe.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':wink:','<img src="#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif" alt=\":wink:\" />','8a80c6485cd926be453217d59a84a888.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (';)','<img src="#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif" alt=\";)\" />','8a80c6485cd926be453217d59a84a888.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES ('-)','<img src="#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif" alt=\"-)\" />','8a80c6485cd926be453217d59a84a888.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':!:','<img src="#CONTEXT#/images/smilies/9293feeb0183c67ea1ea8c52f0dbaf8c.gif" alt=\":!:\" />','9293feeb0183c67ea1ea8c52f0dbaf8c.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':?:','<img src="#CONTEXT#/images/smilies/0a4d7238daa496a758252d0a2b1a1384.gif" alt=\":?:\" />','0a4d7238daa496a758252d0a2b1a1384.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':idea:','<img src="#CONTEXT#/images/smilies/8f7fb9dd46fb8ef86f81154a4feaada9.gif" alt=\":idea:\" />','8f7fb9dd46fb8ef86f81154a4feaada9.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':arrow:','<img src="#CONTEXT#/images/smilies/d6741711aa045b812616853b5507fd2a.gif" alt=\":arrow:\" />','d6741711aa045b812616853b5507fd2a.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':mrgreen:','<img src="#CONTEXT#/images/smilies/ed515dbff23a0ee3241dcc0a601c9ed6.gif" alt=\":mrgreen:\" />','ed515dbff23a0ee3241dcc0a601c9ed6.gif')
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':hunf:','<img src=\"#CONTEXT#/images/smilies/0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif\" alt=\":hunf:\" />','0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':XD:','<img src=\"#CONTEXT#/images/smilies/49869fe8223507d7223db3451e5321aa.gif\" alt=\":XD:\" />','49869fe8223507d7223db3451e5321aa.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':thumbup:','<img src=\"#CONTEXT#/images/smilies/e8a506dc4ad763aca51bec4ca7dc8560.gif\" alt=\":thumbup:\" />','e8a506dc4ad763aca51bec4ca7dc8560.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':thumbdown:','<img src=\"#CONTEXT#/images/smilies/e78feac27fa924c4d0ad6cf5819f3554.gif\" alt=\":thumbdown:\" />','e78feac27fa924c4d0ad6cf5819f3554.gif');
INSERT INTO jforum_smilies (code, url, disk_name) VALUES (':|','<img src=\"#CONTEXT#/images/smilies/1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif\" alt=\":|\" />','1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif');
