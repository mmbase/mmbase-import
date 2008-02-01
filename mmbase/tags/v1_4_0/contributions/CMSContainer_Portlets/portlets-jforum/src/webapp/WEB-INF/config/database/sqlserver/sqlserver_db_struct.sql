IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_banlist]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_banlist]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_categories]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_categories]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_config]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_config]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_forums]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_forums]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_groups]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_groups]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_posts]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_posts]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_posts_text]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_posts_text]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_privmsgs]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_privmsgs]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_privmsgs_text]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_privmsgs_text]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_ranks]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_ranks]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_role_values]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_role_values]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_roles]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_roles]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_search_results]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_search_results]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_search_topics]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_search_topics]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_search_wordmatch]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_search_wordmatch]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_search_words]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_search_words]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_sessions]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_sessions]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_smilies]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_smilies]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_themes]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_themes]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_topics]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_topics]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_topics_watch]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_topics_watch]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_user_groups]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_user_groups]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_users]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_users]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_vote_desc]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_vote_desc]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_vote_results]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_vote_results]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_vote_voters]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_vote_voters]


IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_words]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_words]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_karma]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_karma]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_bookmarks]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_bookmarks]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_quota_limit]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_quota_limit]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_extension_groups]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_extension_groups]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_extensions]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_extensions]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_attach]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_attach]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_attach_desc]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_attach_desc]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_attach_quota]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_attach_quota]

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'[jforum_banner]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [jforum_banner]

CREATE TABLE [jforum_banlist] (
	[banlist_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[user_id] [bigint] DEFAULT (0) NOT NULL ,
	[banlist_ip] [varchar] (8) NOT NULL ,
	[banlist_email] [varchar] (255) NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_categories] (
	[categories_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[title] [varchar] (100) NOT NULL ,
	[display_order] [bigint] DEFAULT (0) NOT NULL,
	[moderated] [tinyint] DEFAULT (0) 
) ON [PRIMARY]


CREATE TABLE [jforum_config] (
	[config_name] [varchar] (255) NOT NULL ,
	[config_value] [varchar] (255) NOT NULL ,
	[config_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_forums] (
	[forum_id] [int] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[categories_id] [bigint] DEFAULT (1) NOT NULL ,
	[forum_name] [varchar] (150) NOT NULL ,
	[forum_desc] [varchar] (255) NULL ,
	[forum_order] [bigint] DEFAULT (1) NULL ,
	[forum_topics] [bigint] DEFAULT (0) NOT NULL ,
	[forum_last_post_id] [bigint] DEFAULT (0) NOT NULL ,
	[moderated] [int] DEFAULT (0) NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_groups] (
	[group_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[group_name] [varchar] (40) NOT NULL ,
	[group_description] [varchar] (255) NULL ,
	[parent_id] [bigint] DEFAULT (0) NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_posts] (
	[post_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[topic_id] [bigint] DEFAULT (0) NOT NULL ,
	[forum_id] [int] DEFAULT (0) NOT NULL ,
	[user_id] [bigint] DEFAULT (0) NOT NULL ,
	[post_time] [datetime] NULL ,
	[poster_ip] [varchar] (15) NULL ,
	[enable_bbcode] [tinyint] DEFAULT (1) NOT NULL ,
	[enable_html] [tinyint] DEFAULT (1)  NOT NULL ,
	[enable_smilies] [tinyint] DEFAULT (1) NOT NULL ,
	[enable_sig] [tinyint] DEFAULT (1) NOT NULL ,
	[post_edit_time] [datetime] NULL ,
	[post_edit_count] [int] DEFAULT (0) NOT NULL ,
	[status] [tinyint] DEFAULT (1) NULL, 
	[attach] [tinyint] DEFAULT (0) not null,
	[need_moderate] [tinyint] DEFAULT (0)
) ON [PRIMARY]


CREATE TABLE [jforum_posts_text] (
	[post_id] [bigint] DEFAULT (0) PRIMARY KEY CLUSTERED NOT NULL ,
	[post_text] [text] NULL ,
	[post_subject] [varchar] (100) NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]


CREATE TABLE [jforum_privmsgs] (
	[privmsgs_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[privmsgs_type] [tinyint] DEFAULT (0) NOT NULL ,
	[privmsgs_subject] [varchar] (255) NOT NULL ,
	[privmsgs_from_userid] [bigint] DEFAULT (0) NOT NULL ,
	[privmsgs_to_userid] [bigint] DEFAULT (0) NOT NULL ,
	[privmsgs_date] [datetime] NULL ,
	[privmsgs_ip] [varchar] (8) DEFAULT('') NOT NULL , 
	[privmsgs_enable_bbcode] [tinyint] DEFAULT (1) NOT NULL ,
	[privmsgs_enable_html] [tinyint] DEFAULT (0) NOT NULL ,
	[privmsgs_enable_smilies] [tinyint] DEFAULT (1) NOT NULL ,
	[privmsgs_attach_sig] [tinyint] DEFAULT (1) NOT NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_privmsgs_text] (
	[privmsgs_id] [bigint] DEFAULT (0) PRIMARY KEY CLUSTERED NOT NULL ,
	[privmsgs_text] [text] NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]


CREATE TABLE [jforum_ranks] (
	[rank_id] [int] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[rank_title] [varchar] (50) NOT NULL ,
	[rank_min] [bigint] DEFAULT (0) NOT NULL ,
	[rank_special] [tinyint] NULL ,
	[rank_image] [varchar] (255) NULL 
) ON [PRIMARY]

	
CREATE TABLE [jforum_role_values] (
	[role_id] [bigint] DEFAULT (0) NOT NULL ,
	[role_value] [varchar] (255) NULL ,
	[role_type] [tinyint] DEFAULT (1) NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_roles] (
	[role_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[group_id] [bigint] DEFAULT (0) NULL ,
	[name] [varchar] (255) NOT NULL ,
	[role_type] [tinyint] DEFAULT (1) NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_search_results] (
	[topic_id] [bigint] DEFAULT (0) NOT NULL ,
	[session_id] [varchar] (50) NULL ,
	[search_time] [datetime] NULL 
) ON [PRIMARY]

CREATE TABLE [jforum_search_topics] (
	[topic_id] [bigint] DEFAULT (0) NOT NULL ,
	[forum_id] [int] DEFAULT (0) NOT NULL ,
	[topic_title] [varchar] (100) NOT NULL ,
	[user_id] [bigint] DEFAULT (0) NOT NULL ,
	[topic_time] [datetime] NULL ,
	[topic_views] [bigint] DEFAULT (0) NULL ,
	[topic_replies] [bigint] DEFAULT (0) NULL ,
	[topic_status] [tinyint] DEFAULT (0) NULL ,
	[topic_vote_id] [int] DEFAULT (0) NULL ,
	[topic_type] [tinyint] DEFAULT (0) NULL ,
	[topic_first_post_id] [bigint] DEFAULT (0) NULL ,
	[topic_last_post_id] [bigint] DEFAULT (0) NOT NULL ,
	[moderated] [int] DEFAULT (0) NULL ,
	[session_id] [varchar] (50) NULL ,
	[search_time] [datetime] NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_search_wordmatch] (
	[post_id] [bigint] DEFAULT (0) NOT NULL ,
	[word_id] [bigint] DEFAULT (0) NOT NULL ,
	[title_match] [tinyint] DEFAULT (0) NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_search_words] (
	[word_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[word] [varchar] (100) NOT NULL ,
	[word_hash] [bigint] NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_sessions] (
	[session_id] [varchar] (50) NOT NULL ,
	[session_user_id] [bigint] DEFAULT (0) NOT NULL ,
	[session_start] [datetime] NULL ,
	[session_time] [bigint] DEFAULT (0) NULL ,
	[session_ip] [varchar] (8) DEFAULT '' NOT NULL  ,
	[session_page] [bigint] DEFAULT (0) NOT NULL ,
	[session_logged_int] [tinyint] NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_smilies] (
	[smilie_id] [int] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[code] [varchar] (50) NOT NULL ,
	[url] [varchar] (100) NULL ,
	[disk_name] [varchar] (255) NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_themes] (
	[themes_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[template_name] [varchar] (30) NOT NULL ,
	[style_name] [varchar] (30) NOT NULL 
) ON [PRIMARY]

CREATE TABLE [jforum_topics] (
	[topic_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[forum_id] [int] DEFAULT (0) NOT NULL ,
	[topic_title] [varchar] (100) NOT NULL ,
	[user_id] [bigint] DEFAULT (0) NOT NULL ,
	[topic_time] [datetime] NULL ,
	[topic_views] [bigint] DEFAULT (1) NULL ,
	[topic_replies] [bigint] DEFAULT (0) NULL ,
	[topic_status] [tinyint] DEFAULT (0) NULL ,
	[topic_vote_id] [int] DEFAULT (0) NULL ,
	[topic_type] [tinyint] DEFAULT (0) NULL ,
	[topic_first_post_id] [bigint] DEFAULT (0) NULL ,
	[topic_last_post_id] [bigint] DEFAULT (0) NOT NULL ,
	[moderated] [int] DEFAULT (0) NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_topics_watch] (
	[topic_id] [bigint] DEFAULT (0) NOT NULL ,
	[user_id] [bigint] DEFAULT (0) NOT NULL ,
	[is_read] [tinyint] DEFAULT (0) NOT NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_user_groups] (
	[group_id] [bigint] DEFAULT (0) NOT NULL ,
	[user_id] [bigint] DEFAULT (0) NOT NULL 
) ON [PRIMARY]

CREATE TABLE [jforum_users] (
	[user_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[user_active] [tinyint] NULL ,
	[username] [varchar] (50) NOT NULL ,
	[user_password] [varchar] (32) NOT NULL ,
	[user_session_time] [bigint] DEFAULT (0) NULL ,
	[user_session_page] [int] DEFAULT (0) NOT NULL ,
	[user_lastvisit] [datetime] NULL ,
	[user_regdate] [datetime] NULL ,
	[user_level] [tinyint] NULL ,
	[user_posts] [bigint] DEFAULT (0) NOT NULL ,
	[user_timezone] [varchar] (5) DEFAULT('') NOT NULL , 
	[user_style] [tinyint] NULL ,
	[user_lang] [varchar] (255) DEFAULT('') NOT NULL ,
	[user_dateformat] [varchar] (20) DEFAULT ('%d/%M/%Y %H:%i') NOT NULL ,
	[user_new_privmsg] [int] DEFAULT (0) NOT NULL ,
	[user_unread_privmsg] [int] DEFAULT (0) NOT NULL ,
	[user_last_privmsg] [datetime] NULL ,
	[user_emailtime] [datetime] NULL ,
	[user_viewemail] [tinyint] DEFAULT (0) NULL ,
	[user_attachsig] [tinyint] DEFAULT (1) NULL ,
	[user_allowhtml] [tinyint] DEFAULT (0) NULL ,
	[user_allowbbcode] [tinyint] DEFAULT (1) NULL ,
	[user_allowsmilies] [tinyint] DEFAULT (1) NULL ,
	[user_allowavatar] [tinyint] DEFAULT (1) NULL ,
	[user_allow_pm] [tinyint] DEFAULT (1) NULL ,
	[user_allow_viewonline] [tinyint] DEFAULT (1) NULL ,
	[user_notify] [tinyint] DEFAULT (1) NULL ,
	[user_notify_pm] [tinyint] DEFAULT (1) NULL ,
	[user_popup_pm] [tinyint] DEFAULT (1) NULL ,
	[rank_id] [int] DEFAULT (1) NULL ,
	[user_avatar] [varchar] (100)  NULL ,
	[user_avatar_type] [tinyint] DEFAULT (0) NOT NULL ,
	[user_email] [varchar] (255)  DEFAULT('') NOT NULL ,
	[user_icq] [varchar] (15) NULL ,
	[user_website] [varchar] (100) NULL ,
	[user_from] [varchar] (100) NULL ,
	[user_sig] [text] NULL ,
	[user_sig_bbcode_uid] [varchar] (10) NULL ,
	[user_aim] [varchar] (255) NULL ,
	[user_yim] [varchar] (255) NULL ,
	[user_msnm] [varchar] (255) NULL ,
	[user_occ] [varchar] (100) NULL ,
	[user_interests] [varchar] (255) NULL ,
	[user_biography] [text] NULL ,
	[user_actkey] [varchar] (32) NULL ,
	[gender] [char] (1) NULL ,
	[themes_id] [bigint] NULL ,
	[deleted] [tinyint] NULL ,
	[user_viewonline] [tinyint] DEFAULT(1) NULL ,
	[security_hash] [varchar] (32) NULL, 
	[user_karma] [decimal] (10,2)
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]


CREATE TABLE [jforum_vote_desc] (
	[vote_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[topic_id] [bigint] DEFAULT (0) NOT NULL ,
	[vote_text] [varchar] (255) NOT NULL ,
	[vote_start] [datetime] NOT NULL ,
	[vote_length] [bigint] DEFAULT (0) NOT NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]


CREATE TABLE [jforum_vote_results] (
	[vote_id] [bigint] DEFAULT (0) NOT NULL ,
	[vote_option_id] [tinyint] DEFAULT (0) NOT NULL ,
	[vote_option_text] [varchar] (255) NOT NULL ,
	[vote_result] [bigint] DEFAULT (0) NOT NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_vote_voters] (
	[vote_id] [bigint] DEFAULT (0) NOT NULL ,
	[vote_user_id] [bigint] DEFAULT (0) NOT NULL ,
	[vote_user_ip] [varchar] (15) NOT NULL 
) ON [PRIMARY]


CREATE TABLE [jforum_words] (
	[word_id] [bigint] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[word] [varchar] (100) NOT NULL ,
	[replacement] [varchar] (100) NOT NULL 
) ON [PRIMARY]

CREATE TABLE [jforum_karma] (
	[karma_id] [BIGINT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL ,
	[post_id] [INT] NOT NULL,
	[topic_id] [INT] NOT NULL,
	[post_user_id] [INT] NOT NULL,
	[from_user_id] [INT] NOT NULL,
	[points] [INT] NOT NULL,
	[rate_date] [datetime] NULL
) ON [PRIMARY]

CREATE TABLE [jforum_bookmarks] (
	[bookmark_id] [INT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL,
	[user_id] [INT] NOT NULL,
	[relation_id] [INT] NOT NULL,
	[relation_type] [INT] NOT NULL,
	[public_visible] [INT] DEFAULT (1),
	[title] varchar(255),
	[description] [varchar] (255)
) ON [PRIMARY]

CREATE TABLE [jforum_quota_limit] (
	[quota_limit_id] [INT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL,
	[quota_desc] [VARCHAR](50) NOT NULL,
	[quota_limit] [INT] NOT NULL,
	[quota_type] [TINYINT] DEFAULT (1)
) ON [PRIMARY]

CREATE TABLE [jforum_extension_groups] (
	[extension_group_id] [INT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL,
	[name] [VARCHAR](100) NOT NULL,
	[allow] [TINYINT] DEFAULT (1), 
	[upload_icon] [VARCHAR](100),
	[download_mode] [TINYINT] DEFAULT (1)
) ON [PRIMARY]

CREATE TABLE [jforum_extensions] (
	[extension_id] [INT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL,
	[extension_group_id] [INT] NOT NULL,
	[description][VARCHAR](100),
	[upload_icon] [VARCHAR](100),
	[extension] [VARCHAR](10),
	[allow] [TINYINT] DEFAULT (1)
) ON [PRIMARY]

CREATE TABLE [jforum_attach] (
	[attach_id] [INT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL,
	[post_id] [INT],
	[privmsgs_id] [INT],
	[user_id] [INT] NOT NULL
) ON [PRIMARY]

CREATE TABLE [jforum_attach_desc] (
	[attach_desc_id] [INT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL,
	[attach_id] [INT] NOT NULL,
	[physical_filename] [VARCHAR](255) NOT NULL,
	[real_filename] [VARCHAR](255) NOT NULL,
	[download_count] [INT],
	[description ][VARCHAR](255),
	[mimetype] [VARCHAR](50),
	[filesize] [INT],
	[upload_time] [DATETIME],
	[thumb] [TINYINT] DEFAULT (0),
	[extension_id] [INT]
) ON [PRIMARY]

CREATE TABLE [jforum_attach_quota] (
	[attach_quota_id] [INT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL,
	[group_id] [INT] NOT NULL,
	[quota_limit_id] [INT] NOT NULL
) ON [PRIMARY]

CREATE TABLE [jforum_banner] (
	[banner_id] [INT] IDENTITY (1, 1) PRIMARY KEY CLUSTERED NOT NULL,
	[banner_name] [VARCHAR](90),
	[banner_placement] [TINYINT] DEFAULT (0) NOT NULL,
	[banner_description] [VARCHAR](250),
	[banner_clicks] [INT] DEFAULT (0) NOT NULL,
	[banner_views] [INT] DEFAULT (0) NOT NULL,
	[banner_url] [VARCHAR](250),
	[banner_weight] [INT] DEFAULT (50) NOT NULL,
	[banner_active] [TINYINT] DEFAULT (0) NOT NULL,
	[banner_comment] [VARCHAR](250),
	[banner_type] [INT] DEFAULT (0) NOT NULL,
	[banner_width] [INT] DEFAULT (0) NOT NULL,
	[banner_height] [INT] DEFAULT (0) NOT NULL,
) ON [PRIMARY]

 CREATE  INDEX [forum_id] ON [jforum_posts]([forum_id]) ON [PRIMARY]
 CREATE  INDEX [idx_role] ON [jforum_role_values]([role_id]) ON [PRIMARY]
 CREATE  INDEX [idx_group] ON [jforum_roles]([group_id]) ON [PRIMARY]
 CREATE  INDEX [idx_name] ON [jforum_roles]([name]) ON [PRIMARY]
 CREATE  INDEX [topic_id] ON [jforum_search_results]([topic_id]) ON [PRIMARY]
 CREATE  INDEX [topic_id] ON [jforum_search_topics]([topic_id]) ON [PRIMARY]
 CREATE  INDEX [forum_id] ON [jforum_search_topics]([forum_id]) ON [PRIMARY]
 CREATE  INDEX [user_id] ON [jforum_search_topics]([user_id]) ON [PRIMARY]
 CREATE  INDEX [topic_first_post_id] ON [jforum_search_topics]([topic_first_post_id]) ON [PRIMARY]
 CREATE  INDEX [topic_last_post_id] ON [jforum_search_topics]([topic_last_post_id]) ON [PRIMARY]
 CREATE  INDEX [post_id] ON [jforum_search_wordmatch]([post_id]) ON [PRIMARY]
 CREATE  INDEX [word_id] ON [jforum_search_wordmatch]([word_id]) ON [PRIMARY]
 CREATE  INDEX [title_match] ON [jforum_search_wordmatch]([title_match]) ON [PRIMARY]
 CREATE  INDEX [word] ON [jforum_search_words]([word]) ON [PRIMARY]
 CREATE  INDEX [word_hash] ON [jforum_search_words]([word_hash]) ON [PRIMARY]
 CREATE  INDEX [forum_id] ON [jforum_topics]([forum_id]) ON [PRIMARY]
 CREATE  INDEX [user_id] ON [jforum_topics]([user_id]) ON [PRIMARY]
 CREATE  INDEX [topic_first_post_id] ON [jforum_topics]([topic_first_post_id]) ON [PRIMARY]
 CREATE  INDEX [topic_last_post_id] ON [jforum_topics]([topic_last_post_id]) ON [PRIMARY]
 CREATE  INDEX [idx_topic] ON [jforum_topics_watch]([topic_id]) ON [PRIMARY]
 CREATE  INDEX [idx_user] ON [jforum_topics_watch]([user_id]) ON [PRIMARY]
 CREATE  INDEX [idx_group] ON [jforum_user_groups]([group_id]) ON [PRIMARY]
 CREATE  INDEX [idx_user] ON [jforum_user_groups]([user_id]) ON [PRIMARY]
 CREATE  INDEX [idx_user] ON [jforum_banlist]([user_id]) ON [PRIMARY] 
 CREATE  INDEX [categories_id] ON [jforum_forums]([categories_id]) ON [PRIMARY]
 CREATE  INDEX [user_id] ON [jforum_posts]([user_id]) ON [PRIMARY]
 CREATE  INDEX [topic_id] ON [jforum_posts]([topic_id]) ON [PRIMARY]
 CREATE  INDEX [karma_id] ON [jforum_karma]([karma_id]) ON [PRIMARY]
 CREATE  INDEX [bookmarks_relation_id] ON [jforum_bookmarks]([relation_id]) ON [PRIMARY]

 CREATE  INDEX [idx_att_post] ON [jforum_attach]([post_id]) ON [PRIMARY]
 CREATE  INDEX [idx_att_priv] ON [jforum_attach]([privmsgs_id]) ON [PRIMARY]
 CREATE  INDEX [idx_att_user] ON [jforum_attach]([user_id]) ON [PRIMARY]
 CREATE  INDEX [idx_att_d_att] ON [jforum_attach_desc]([attach_id]) ON [PRIMARY]
 CREATE  INDEX [idx_att_d_ext] ON [jforum_attach_desc]([extension_id]) ON [PRIMARY]
