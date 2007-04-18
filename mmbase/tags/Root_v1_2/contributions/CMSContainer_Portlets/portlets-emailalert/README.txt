DESCRIPTION:
Email alerts implementation.

The following sql commands need to be executed:

alter table mm_article add column alert tinyint(1) default 0;
alter table live_article add column alert tinyint(1) default 0;