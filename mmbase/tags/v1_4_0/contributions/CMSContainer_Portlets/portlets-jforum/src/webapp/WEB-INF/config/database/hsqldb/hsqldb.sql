# ##########
# UserModel
# ##########
UserModel.lastUserRegistered = SELECT TOP 1 user_id, username FROM jforum_users ORDER BY user_regdate DESC 

UserModel.selectAllByLimit = SELECT LIMIT ? ? user_email, user_id, user_posts, user_regdate, username, deleted, user_karma, \
	user_from, user_website, user_viewemail FROM jforum_users ORDER BY user_id 

UserModel.lastGeneratedUserId = SELECT MAX(user_id) from jforum_users

UserModel.selectAllByGroup = SELECT LIMIT ? ? user_email, u.user_id, user_posts, user_regdate, username, deleted, user_karma, \
	user_from, user_website, user_viewemail \
	FROM jforum_users u, jforum_user_groups ug \
	WHERE u.user_id = ug.user_id \
	AND ug.group_id = ? \
	ORDER BY user_id

UserModel.selectById = SELECT u.*, \
	(SELECT COUNT(1) FROM jforum_privmsgs pm \
	WHERE pm.privmsgs_to_userid = u.user_id \
	AND pm.privmsgs_type = 1) AS private_messages \
	FROM jforum_users u \
	WHERE u.user_id = ?

UserModel.isUsernameRegistered = SELECT COUNT(1) as registered FROM jforum_users WHERE LCASE(username) = LCASE(?)
UserModel.login = SELECT user_id FROM jforum_users WHERE LCASE(username) = LCASE(?) AND user_password = ?

# #############
# PostModel
# #############
PostModel.lastGeneratedPostId = SELECT max(post_id) from jforum_posts

PostModel.selectAllByTopicByLimit = SELECT LIMIT ? ? p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, p.attach, \
	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username, p.need_moderate \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND topic_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 0 \
	ORDER BY post_time ASC 

PostModel.selectByUserByLimit = SELECT LIMIT ? ? p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, p.attach, \
	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username, p.need_moderate \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND p.user_id = u.user_id \
	AND p.user_id = ? \
	AND p.need_moderate = 0 \
	ORDER BY post_time DESC

# ##########
# PollModel
# ##########
PollModel.lastGeneratedPollId = SELECT MAX(vote_id) FROM jforum_vote_desc

# #############
# ForumModel
# #############
ForumModel.lastGeneratedForumId = SELECT MAX(forum_id) from jforum_forums

# #############
# TopicModel
# #############
TopicModel.selectAllByForumByLimit = SELECT LIMIT ? ? t.*, p.user_id AS last_user_id, p.post_time, 0 AS attach \
	FROM jforum_topics t, jforum_posts p \
	WHERE t.forum_id = ? \
	AND p.post_id = t.topic_last_post_id \
	AND p.need_moderate = 0 \
	ORDER BY t.topic_type DESC, t.topic_last_post_id DESC

TopicModel.selectRecentTopicsByLimit = SELECT LIMIT 0 ? t.*, p.user_id AS last_user_id, p.post_time, 0 AS attach \
	FROM jforum_topics t, jforum_posts p \
	WHERE p.post_id = t.topic_last_post_id \
	AND t.user_id = ? \
	AND p.need_moderate = 0 \
	ORDER BY t.topic_last_post_id DESC \
	
TopicModel.lastGeneratedTopicId = SELECT MAX(topic_id) from jforum_topics

TopicModel.selectByUserByLimit = SELECT LIMIT ? ? t.*, p.user_id AS last_user_id, p.post_time, 0 AS attach \
	FROM jforum_topics t, jforum_posts p \
	WHERE p.post_id = t.topic_last_post_id \
	AND t.user_id = ? \
	AND p.need_moderate = 0 \
	ORDER BY t.topic_last_post_id DESC

# #####################
# PrivateMessagesModel
# #####################
PrivateMessagesModel.lastGeneratedPmId = SELECT max(privmsgs_id) from jforum_privmsgs

# ############
# SearchModel
# ############
SearchModel.cleanSearchResults = DELETE FROM jforum_search_results WHERE session_id = ? OR datediff('hh',search_time,current_timestamp) < 1 
SearchModel.cleanSearchTopics = DELETE FROM jforum_search_topics WHERE session_id = ? OR datediff('hh',search_time,current_timestamp) < 1 

SearchModel.searchByWord = SELECT post_id FROM jforum_search_wordmatch wm, jforum_search_words w \
	WHERE wm.word_id = w.word_id \
	AND LCASE(w.word) = LCASE(?)
	
SearchModel.getPostsToIndex = SELECT LIMIT ? ? p.post_id, pt.post_text, pt.post_subject \
	FROM jforum_posts p, jforum_posts_text pt \
	WHERE p.post_id = pt.post_id \
	AND p.post_id BETWEEN ? AND ?

# #############
# SmiliesModel
# #############
SmiliesModel.lastGeneratedSmilieId = SELECT max(smilie_id) from jforum_smilies

# ##################
# PermissionControl
# ##################
PermissionControl.lastGeneratedRoleId = SELECT max(role_id) from jforum_roles

# ##############
# CategoryModel
# ##############
CategoryModel.lastGeneratedCategoryId = SELECT max(categories_id) from jforum_categories

# ################
# AttachmentModel
# ################
AttachmentModel.lastGeneratedAttachmentId = SELECT MAX(attach_id) FROM jforum_attach
