DESCRIPTION:
Email alert implementation. The visitors of the website can subscribe to changes on articles on a page. The editors have editwizards by which they can configure the texts of the emails.

The following sql commands need to be executed:

alter table mm_article add column alert tinyint(1) default 0;
alter table live_article add column alert tinyint(1) default 0;