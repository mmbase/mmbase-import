# Set default user/group ids
SET @GENERAL_GROUP_ID = 1;
SET @ADMIN_GROUP_ID = 2;
SET @ANONYMOUS_ID = 1;
SET @ADMIN_ID = 2;

#
# Groups
#
INSERT INTO jforum_groups ( group_id, group_name, group_description ) VALUES (@GENERAL_GROUP_ID, 'General', 'General Users');

INSERT INTO jforum_groups ( group_id, group_name, group_description ) VALUES (@ADMIN_GROUP_ID, 'Administration', 'Admin Users');

# 
# Users
#
INSERT INTO jforum_users ( user_id, username, user_password, user_regdate ) VALUES (@ANONYMOUS_ID, 'Anonymous', 'nopass', NOW());

INSERT INTO jforum_users ( user_id, username, user_password, user_regdate, user_posts ) VALUES (@ADMIN_ID, 'Admin', '21232f297a57a5a743894a0e4a801fc3', NOW(), 1);

#
# User Groups
#
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (@GENERAL_GROUP_ID, @ANONYMOUS_ID);
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (@ADMIN_GROUP_ID, @ADMIN_ID);

#
# Roles
#
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@GENERAL_GROUP_ID, 'perm_administration', 0);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@GENERAL_GROUP_ID, 'perm_moderation', 0);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@GENERAL_GROUP_ID, 'perm_moderation_post_remove', 0);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@GENERAL_GROUP_ID, 'perm_moderation_post_edit', 0);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@GENERAL_GROUP_ID, 'perm_moderation_topic_move', 0);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@GENERAL_GROUP_ID, 'perm_moderation_topic_lockUnlock', 0);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@GENERAL_GROUP_ID, 'perm_vote', 1);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@GENERAL_GROUP_ID, 'perm_create_poll', 0);

# Admin
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@ADMIN_GROUP_ID, 'perm_administration', 1);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@ADMIN_GROUP_ID, 'perm_moderation', 1);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@ADMIN_GROUP_ID, 'perm_moderation_post_remove', 1);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@ADMIN_GROUP_ID, 'perm_moderation_post_edit', 1);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@ADMIN_GROUP_ID, 'perm_moderation_topic_move', 1);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@ADMIN_GROUP_ID, 'perm_moderation_topic_lockUnlock', 1);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@ADMIN_GROUP_ID, 'perm_vote', 1);
INSERT INTO jforum_roles (group_id, name, role_type) VALUES (@ADMIN_GROUP_ID, 'perm_create_poll', 1);

SET @ROLE_ID = LAST_INSERT_ID();

#
# Smilies
#
INSERT INTO jforum_smilies VALUES (1, ':)', '<img src=\"#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif\" />', '3b63d1616c5dfcf29f8a7a031aaa7cad.gif');
INSERT INTO jforum_smilies VALUES (2, ':-)', '<img src=\"#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif\"/>', '3b63d1616c5dfcf29f8a7a031aaa7cad.gif');
INSERT INTO jforum_smilies VALUES (3, ':D', '<img src=\"#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif\" />', '283a16da79f3aa23fe1025c96295f04f.gif');
INSERT INTO jforum_smilies VALUES (4, ':-D', '<img src=\"#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif\" />', '283a16da79f3aa23fe1025c96295f04f.gif');
INSERT INTO jforum_smilies VALUES (5, ':(', '<img src=\"#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif\" />', '9d71f0541cff0a302a0309c5079e8dee.gif');
INSERT INTO jforum_smilies VALUES (6, ':mrgreen:', '<img src=\"#CONTEXT#/images/smilies/ed515dbff23a0ee3241dcc0a601c9ed6.gif\" />', 'ed515dbff23a0ee3241dcc0a601c9ed6.gif');
INSERT INTO jforum_smilies VALUES (7, ':-o', '<img src=\"#CONTEXT#/images/smilies/47941865eb7bbc2a777305b46cc059a2.gif\"  />', '47941865eb7bbc2a777305b46cc059a2.gif');
INSERT INTO jforum_smilies VALUES (8, ':shock:', '<img src=\"#CONTEXT#/images/smilies/385970365b8ed7503b4294502a458efa.gif\" />', '385970365b8ed7503b4294502a458efa.gif');
INSERT INTO jforum_smilies VALUES (9, ':?:', '<img src=\"#CONTEXT#/images/smilies/0a4d7238daa496a758252d0a2b1a1384.gif\" />', '0a4d7238daa496a758252d0a2b1a1384.gif');
INSERT INTO jforum_smilies VALUES (10, '8)', '<img src=\"#CONTEXT#/images/smilies/b2eb59423fbf5fa39342041237025880.gif\"  />', 'b2eb59423fbf5fa39342041237025880.gif');
INSERT INTO jforum_smilies VALUES (11, ':lol:', '<img src=\"#CONTEXT#/images/smilies/97ada74b88049a6d50a6ed40898a03d7.gif\" />', '97ada74b88049a6d50a6ed40898a03d7.gif');
INSERT INTO jforum_smilies VALUES (12, ':x', '<img src=\"#CONTEXT#/images/smilies/1069449046bcd664c21db15b1dfedaee.gif\"  />', '1069449046bcd664c21db15b1dfedaee.gif');
INSERT INTO jforum_smilies VALUES (13, ':P', '<img src=\"#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif\" />', '69934afc394145350659cd7add244ca9.gif');
INSERT INTO jforum_smilies VALUES (14, ':-P', '<img src=\"#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif\" />', '69934afc394145350659cd7add244ca9.gif');
INSERT INTO jforum_smilies VALUES (15, ':oops:', '<img src=\"#CONTEXT#/images/smilies/499fd50bc713bfcdf2ab5a23c00c2d62.gif\" />', '499fd50bc713bfcdf2ab5a23c00c2d62.gif');
INSERT INTO jforum_smilies VALUES (16, ':cry:', '<img src=\"#CONTEXT#/images/smilies/c30b4198e0907b23b8246bdd52aa1c3c.gif\" />', 'c30b4198e0907b23b8246bdd52aa1c3c.gif');
INSERT INTO jforum_smilies VALUES (17, ':evil:', '<img src=\"#CONTEXT#/images/smilies/2e207fad049d4d292f60607f80f05768.gif\" />', '2e207fad049d4d292f60607f80f05768.gif');
INSERT INTO jforum_smilies VALUES (18, ':twisted:', '<img src=\"#CONTEXT#/images/smilies/908627bbe5e9f6a080977db8c365caff.gif\" />', '908627bbe5e9f6a080977db8c365caff.gif');
INSERT INTO jforum_smilies VALUES (19, ':roll:', '<img src=\"#CONTEXT#/images/smilies/2786c5c8e1a8be796fb2f726cca5a0fe.gif\" />', '2786c5c8e1a8be796fb2f726cca5a0fe.gif');
INSERT INTO jforum_smilies VALUES (20, ':wink:', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies VALUES (21, ';)', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies VALUES (22, ';-)', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies VALUES (23, ':!:', '<img src=\"#CONTEXT#/images/smilies/9293feeb0183c67ea1ea8c52f0dbaf8c.gif\" />', '9293feeb0183c67ea1ea8c52f0dbaf8c.gif');
INSERT INTO jforum_smilies VALUES (24, ':?', '<img src=\"#CONTEXT#/images/smilies/136dd33cba83140c7ce38db096d05aed.gif\" />', '136dd33cba83140c7ce38db096d05aed.gif');
INSERT INTO jforum_smilies VALUES (25, ':idea:', '<img src=\"#CONTEXT#/images/smilies/8f7fb9dd46fb8ef86f81154a4feaada9.gif\" />', '8f7fb9dd46fb8ef86f81154a4feaada9.gif');
INSERT INTO jforum_smilies VALUES (26, ':arrow:', '<img src=\"#CONTEXT#/images/smilies/d6741711aa045b812616853b5507fd2a.gif\" />', 'd6741711aa045b812616853b5507fd2a.gif');
INSERT INTO jforum_smilies VALUES (32, ':hunf:', '<img src=\"#CONTEXT#/images/smilies/0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif\" />', '0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif');
INSERT INTO jforum_smilies VALUES (31, ':-(', '<img src=\"#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif\"  />', '9d71f0541cff0a302a0309c5079e8dee.gif');
INSERT INTO jforum_smilies VALUES (33, ':XD:', '<img src=\"#CONTEXT#/images/smilies/49869fe8223507d7223db3451e5321aa.gif\" />', '49869fe8223507d7223db3451e5321aa.gif');
INSERT INTO jforum_smilies VALUES (34, ':thumbup:', '<img src=\"#CONTEXT#/images/smilies/e8a506dc4ad763aca51bec4ca7dc8560.gif\" />', 'e8a506dc4ad763aca51bec4ca7dc8560.gif');
INSERT INTO jforum_smilies VALUES (35, ':thumbdown:', '<img src=\"#CONTEXT#/images/smilies/e78feac27fa924c4d0ad6cf5819f3554.gif\" />', 'e78feac27fa924c4d0ad6cf5819f3554.gif');
INSERT INTO jforum_smilies VALUES (36, ':|', '<img src=\"#CONTEXT#/images/smilies/1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif\" />', '1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif');

#
# Demonstration Forum
#
INSERT INTO jforum_categories VALUES (1,'Category Test',1,0);
INSERT INTO jforum_forums VALUES (1,1,'Test Forum','This is a test forum',1,1,1,0);
INSERT INTO jforum_topics VALUES (1,1,'Welcome to JForum',2,'2005-01-04 16:59:54',1,0,0,0,0,1,1,0);
INSERT INTO jforum_posts VALUES (1,1,1,2,'2005-01-04 16:59:54','127.0.0.1',1,0,1,1,'2005-01-04 17:01:45',2,1,0,0);
INSERT INTO jforum_posts_text VALUES (1,'[b]Congratulations![/b]. You have completed the installation of JForum. \r\n\r\nTo start administering the board, login as [i]Admin / <the password you supplied in the installer>[/i] and access the [b]Admin Control Panel[/b] using the link that shows up in the bottom of the page. \r\n\r\nThere you will be able to create Categories, Forums and much more. \r\n\r\nFor more information and support, please access [url]http://www.jforum.net/community.htm[/url] and [url]http://www.jforum.net/help.htm[/url]\r\n\r\nThank you for choosing JForum.\r\n\r\nThe JForum Team.','Welcome to JForum');

#
# View Forum
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 1, 'perm_forum', @GENERAL_GROUP_ID, 0);
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 2, 'perm_forum', @ADMIN_GROUP_ID, 0);

INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 1, '1', 1);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 2, '1', 1);

#
# Anonymous posts
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 3, 'perm_anonymous_post', @GENERAL_GROUP_ID, 0);
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 4, 'perm_anonymous_post', @ADMIN_GROUP_ID, 0);

INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 3, '1', 1);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 4, '1', 1);

#
# View Category
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 5, 'perm_category', @GENERAL_GROUP_ID, 0);
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 6, 'perm_category', @ADMIN_GROUP_ID, 0);

INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 5, '1', 1);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 6, '1', 1);

#
# Sticky / Announcements
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 7, 'perm_create_sticky_announcement_topics', @GENERAL_GROUP_ID, 1);
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 8, 'perm_create_sticky_announcement_topics', @ADMIN_GROUP_ID, 1);

#
# Create / Reply to topics
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 9, 'perm_read_only_forums', @GENERAL_GROUP_ID, 0);
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 10, 'perm_read_only_forums', @ADMIN_GROUP_ID, 0);

INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 9, '1', 1);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 10, '1', 1);

# 
# Enable HTML
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 11, 'perm_html_disabled', @GENERAL_GROUP_ID, 0);
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (@ROLE_ID + 12, 'perm_html_disabled', @ADMIN_GROUP_ID, 0);

INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 11, '1', 1);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 12, '1', 1);

#
# Attachments
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 13, 'perm_attachments_enabled', @GENERAL_GROUP_ID, 0);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 14, 'perm_attachments_enabled', @ADMIN_GROUP_ID, 0);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 15, 'perm_attachments_download', @GENERAL_GROUP_ID, 1);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 16, 'perm_attachments_download', @ADMIN_GROUP_ID, 1);

INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 13, '1', 1);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 14, '1', 1);

#
# Bookmarks
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 17, 'perm_bookmarks_enabled', @GENERAL_GROUP_ID, 1);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 18, 'perm_bookmarks_enabled', @ADMIN_GROUP_ID, 1);

#
# Karma
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 19, 'perm_karma_enabled', @GENERAL_GROUP_ID, 1);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 20, 'perm_karma_enabled', @ADMIN_GROUP_ID, 1);

#
# Reply only
#
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 21, 'perm_reply_only',  @GENERAL_GROUP_ID, 0);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (@ROLE_ID + 22, 'perm_reply_only', @ADMIN_GROUP_ID, 0);

INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 21, '1', 1);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (@ROLE_ID + 22, '1', 1);
