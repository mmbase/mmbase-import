-- Table structure for table 'jforum_attach_quota'
DROP SEQUENCE jforum_attach_quota_seq;
DROP TABLE jforum_attach_quota;

-- Table structure for table 'jforum_attach_desc'
DROP SEQUENCE jforum_attach_desc_seq;
DROP TABLE jforum_attach_desc;

-- Table structure for table 'jforum_attach'
DROP SEQUENCE jforum_attach_seq;
DROP TABLE jforum_attach;

-- Table structure for table 'jforum_extensions'
DROP SEQUENCE jforum_extensions_seq;
DROP TABLE jforum_extensions;

-- Table structure for table 'jforum_extension_groups'
DROP SEQUENCE jforum_extension_groups_seq;
DROP TABLE jforum_extension_groups;

-- Table structure for table 'jforum_quota_limit'
DROP SEQUENCE jforum_quota_limit_seq;
DROP TABLE jforum_quota_limit;

-- Table structure for table 'jforum_bookmark'
DROP SEQUENCE jforum_bookmarks_seq;
DROP TABLE jforum_bookmarks;

-- Table structure for table 'jforum_karma'
DROP SEQUENCE jforum_karma_seq;
DROP TABLE jforum_karma;

-- Table structure for table 'jforum_search_results'
DROP TABLE jforum_search_results;
DROP TABLE jforum_search_topics;

-- Table structure for table 'jforum_search_wordmatch'
DROP TABLE jforum_search_wordmatch;

-- Table structure for table 'jforum_search_words'
DROP SEQUENCE jforum_search_words_seq;
DROP TABLE jforum_search_words;

-- Table structure for table 'jforum_words'
DROP SEQUENCE jforum_words_seq;
DROP TABLE jforum_words;

-- Table structure for table 'jforum_vote_voters'
DROP TABLE jforum_vote_voters;

-- Table structure for table 'jforum_vote_results'
DROP TABLE jforum_vote_results;

-- Table structure for table 'jforum_vote_desc'
DROP SEQUENCE jforum_vote_desc_seq;
DROP TABLE jforum_vote_desc;

-- Table structure for table 'jforum_users'
DROP SEQUENCE jforum_users_seq;
DROP TABLE jforum_users;

-- Table structure for table 'jforum_topics_watch'
DROP TABLE jforum_topics_watch;

-- Table structure for table 'jforum_topics'
DROP SEQUENCE jforum_topics_seq;
DROP TABLE jforum_topics;

-- Table structure for table 'jforum_themes'
DROP SEQUENCE jforum_themes_seq;
DROP TABLE jforum_themes;

-- Table structure for table 'jforum_smilies'
DROP SEQUENCE jforum_smilies_seq;
DROP TABLE jforum_smilies;

-- Table structure for table 'jforum_sessions'
DROP TABLE jforum_sessions;

-- Table structure for table 'jforum_ranks'
DROP SEQUENCE jforum_ranks_seq;
DROP TABLE jforum_ranks;

-- Table structure for table 'jforum_privmsgs'
DROP SEQUENCE jforum_privmsgs_seq;
DROP TABLE jforum_privmsgs;
DROP TABLE jforum_privmsgs_text;

-- Table structure for table 'jforum_posts_text'
DROP TABLE jforum_posts_text;

-- Table structure for table 'jforum_posts'
DROP SEQUENCE jforum_posts_seq;
DROP TABLE jforum_posts;

-- Table structure for table 'jforum_role_values'
DROP TABLE jforum_role_values;
	
-- Table structure for table 'jforum_roles'
DROP SEQUENCE jforum_roles_seq;
DROP TABLE jforum_roles;

-- Table structure for table 'jforum_groups'
DROP SEQUENCE jforum_groups_seq;
DROP TABLE jforum_groups;
DROP TABLE jforum_user_groups;

-- Table structure for table 'jforum_forums'
DROP SEQUENCE jforum_forums_seq;
DROP TABLE jforum_forums;
DROP TABLE jforum_forums_watch;

-- Table structure for table 'jforum_config'
DROP SEQUENCE jforum_config_seq;
DROP TABLE jforum_config;

-- Table structure for table 'jforum_categories'
DROP SEQUENCE jforum_categories_seq;
DROP TABLE jforum_categories;

-- jforum_banlist
DROP SEQUENCE jforum_banlist_seq;
DROP TABLE jforum_banlist;

-- jforum_banner
DROP SEQUENCE jforum_banner_seq;
DROP TABLE jforum_banner;

DROP INDEX idx_banlist_user;
DROP INDEX idx_forums_categories_id;
DROP INDEX idx_ug_group;
DROP INDEX idx_ug_user;
DROP INDEX idx_roles_group;
DROP INDEX idx_roles_name;
DROP INDEX idx_posts_user;
DROP INDEX idx_posts_topic;
DROP INDEX idx_posts_forum;
DROP INDEX idx_pm_text_id;
DROP INDEX idx_topics_forum;
DROP INDEX idx_topics_user;
DROP INDEX idx_topics_fp;
DROP INDEX idx_topics_lp;
DROP INDEX idx_tw_topic;
DROP INDEX idx_tw_user;
DROP INDEX idx_sw_word;
DROP INDEX idx_sw_hash;
DROP INDEX idx_swm_post;
DROP INDEX idx_swm_word;
DROP INDEX idx_swm_title;
DROP INDEX idx_sr_topic;
DROP INDEX idx_st_topic;
DROP INDEX idx_st_forum;
DROP INDEX idx_st_user;
DROP INDEX idx_st_fp;
DROP INDEX idx_st_lp;
DROP INDEX idx_krm_post;
DROP INDEX idx_krm_topic;
DROP INDEX idx_krm_user;
DROP INDEX idx_krm_from;
DROP INDEX idx_bok_user;
DROP INDEX idx_bok_rel;
DROP INDEX idx_ext_group;
DROP INDEX idx_ext_ext;
DROP INDEX idx_att_post;
DROP INDEX idx_att_priv;
DROP INDEX idx_att_user;
DROP INDEX idx_att_d_att;
DROP INDEX idx_att_d_ext;
DROP INDEX idx_aq_group;
DROP INDEX idx_aq_ql;