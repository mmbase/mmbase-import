create table users
(userid int not null primary key auto_increment,
username varchar(255) not null,
password varchar(255) not null,
email varchar(255),
emaildisp varchar(255),
firstname varchar(255),
middleinit varchar(2),
lastname varchar(255),
signature varchar(255),
homepage varchar(255),
posts int,
picture varchar(255),
occupation varchar(255),
hobbies varchar(255),
bio varchar(255),
icq varchar(255),
microsoftim varchar(255),
netscapeim varchar(255),
yahooim varchar(255),
userdate date,
usertime time,
birthdate date,
location varchar(255),
country varchar(255),
language varchar(255),
timezone varchar(255),
coppa int,
access int,
rank varchar(255),
customrank int,
uploadsize int,
numrowsthreads int,
numrowsmessages int,
numrowsnews int,
numrowsuserlist int,
emailprivatemessage int,
emailmessage int,
privatemailtosent int,
privatemailorder varchar(255),
profanityfilter int,
blockaccess int,
blockavatar int, 
fontsize int,
fontfamily varchar(255),
lastlogindate date,
lastlogintime time)
;


create table forumcategory
(forumcategoryid int not null primary key auto_increment,
forumcategoryname varchar(255),
forumcategorydescription varchar(255),
groupid int)
;


create table forums
(forumid int not null primary key auto_increment,
forumcategoryid int,
forumname varchar(255),
forumdescription varchar(255),
threads int,
posts int,
forumdate date,
forumtime time,
lastmessageid int,
groupid int,
allowattachments int)
;


create table moderators
(moderatorid int not null primary key auto_increment,
forumid int,
userid int)
;


create table threads
(threadid int not null primary key auto_increment,
subject varchar(255),
userid int,
replies int,
views int,
threaddate date,
threadtime time,
forumid int,
newsitem int,
locked int,
lastmessageid int)
;


create table messages
(messageid int not null primary key auto_increment,
userid int,
replied int,
threadid int,
messagedate date,
messagetime time,
messageinthread int,
messagedepth int,
message text,
subject varchar(255),
ipaddress varchar(255),
sendemail int)
;


create table ranks
(rankid int not null primary key auto_increment,
rankname varchar(255),
minposts int)
;


create table privatemail
(mailid int not null primary key auto_increment,
userid int,
sender int,
subject varchar(255),
folder varchar(255),
message text,
maildate date,
mailtime time,
newmessage int,
sendemail int) 
;

create table privatemailfolders
(privatemailfoldersid int not null primary key auto_increment,
userid int,
foldername varchar(255),
folderdescription varchar(255))
;

create table buddylist
(buddylistid int not null primary key auto_increment,
userid int,
buddy int)
;

create table newsitem
(newsid int not null primary key auto_increment,
userid int,
subject varchar(255),
newscategoryid int,
newspicurl varchar(255),
message text,
newsarticle text,
threadid int,
archiveasarticle int,
newsdate date,
newstime time,
groupid int)
;
  
create table newscategory
(newscategoryid int not null primary key auto_increment,
categoryname varchar(255),
categorydescription varchar(255),
categoryurl varchar(255),
groupid int)
;

create table texttranslation
(texttranslateid int not null primary key auto_increment,
textin varchar(255),
textout varchar(255),
smiley int)
;

create table profanity
(profanityid int not null primary key auto_increment,
textin varchar(255),
textout varchar(255))
;

create table security
(securityid int not null primary key auto_increment,
iprange varchar(255),
emaildomain varchar(255))
;

create table links
(linkid int not null primary key auto_increment,
linkname varchar(255),
linkurl varchar(255),
linkdescription varchar(255),
linkcategoryid varchar(255))
;

create table linkscategory
(linkcategoryid int not null primary key auto_increment,
linkcategoryname varchar(255),
linkcategorydescription varchar(255),
groupid int)
;

create table gallery
(galleryid int not null primary key auto_increment,
gallerycategoryid int,
galleryurl varchar(255))
;

create table gallerycategory
(gallerycategoryid int not null primary key auto_increment,
gallerycategoryname varchar(255),
gallerycategorydescription varchar(255),
groupid int)
;

create table advertise
(adid int not null primary key auto_increment,
adname varchar(255),
adurl varchar(255),
adpicurl varchar(255),
userid int,
addate date,
adtime time,
hits int)
;

create table adhit
(adhitid int not null primary key auto_increment,
adid int,
ipaddress varchar(255),
adhitdate date,
adhittime time)
;

create table programgroup
(programgroupid int not null primary key auto_increment,
programgroupname varchar(255),
programgroupdescription varchar(255),
programgroupinfo text,
license text,
groupid int)
;

create table program
(programid int not null primary key auto_increment,
programgroupid int,
programname varchar(255),
programurl varchar(255),
programdescription varchar(255))
;

create table groups
(groupid int not null primary key auto_increment,
groupname varchar(255),
groupdescription varchar(255))
;

create table groupusers
(groupusersid int not null primary key auto_increment,
groupid int,
userid int)
;

create table avatar
(avatarid int not null primary key auto_increment,
avatarurl varchar(255))
;


## Populate the tables

# Ranks can be modifyied to suit your needs

insert into ranks (rankname, minposts) values
('novice', '0')
;
insert into ranks (rankname, minposts) values
('intermediate', '50')
;
insert into ranks (rankname, minposts) values
('expert', '100')
;
insert into ranks (rankname, minposts) values
('veteran', '200')
;


# Do not edit these entries
# These are manditory!

insert into users (userid, username) values
('1', 'deleted_user')
;


# translation entries

insert into texttranslation (textin, textout)
values ('>', '&gt;')
;
insert into texttranslation (textin, textout)
values ('<', '&lt;')
;
insert into texttranslation (textin, textout)
values ('[b]', '<b>')
;
insert into texttranslation (textin, textout)
values ('[/b]', '</b>')
;
insert into texttranslation (textin, textout)
values ('[i]', '<i>')
;
insert into texttranslation (textin, textout)
values ('[/i]', '</i>')
;
insert into texttranslation (textin, textout)
values ('[url=', '<a href=')
;
insert into texttranslation (textin, textout)
values ('[/url]', '</a>')
;
insert into texttranslation (textin, textout)
values ('[img]', '<img src="')
;
insert into texttranslation (textin, textout)
values ('[/img]', '">')
;
insert into texttranslation (textin, textout)
values ('\n', '<br>')
;
insert into texttranslation (textin, textout)
values ('[pre]', '<pre>')
;
insert into texttranslation (textin, textout)
values ('[/pre]', '</pre>')
;
insert into texttranslation (textin, textout)
values ('[hr]', '<hr>')
;
insert into texttranslation (textin, textout)
values ('[p]', '<p>')
;
insert into texttranslation (textin, textout)
values ('[olA]', '<ol type=A>')
;
insert into texttranslation (textin, textout)
values ('[ola]', '<ol type=a>')
;
insert into texttranslation (textin, textout)
values ('[olI]', '<ol type=I>')
;
insert into texttranslation (textin, textout)
values ('[oli]', '<ol type=i>')
;
insert into texttranslation (textin, textout)
values ('[ol]', '<ol>')
;
insert into texttranslation (textin, textout)
values ('[li]', '<li>')
;
insert into texttranslation (textin, textout)
values ('[/ol]', '</ol>')
;
insert into texttranslation (textin, textout)
values ('[ul]', '<ul>')
;
insert into texttranslation (textin, textout)
values ('[ulc]', '<ul type=circle>')
;
insert into texttranslation (textin, textout)
values ('[uls]', '<ul type=square>')
;
insert into texttranslation (textin, textout)
values ('[/ul]', '</ul>')
;
insert into texttranslation (textin, textout)
values ('[cite]', '<cite>')
;
insert into texttranslation (textin, textout)
values ('[/cite]', '</cite>')
;
insert into texttranslation (textin, textout)
values ('[strike]', '<strike>')
;
insert into texttranslation (textin, textout)
values ('[/strike]', '</strike>')
;
insert into texttranslation (textin, textout)
values ('[u]', '<u>')
;
insert into texttranslation (textin, textout)
values ('[/u]', '</u>')
;
insert into texttranslation (textin, textout)
values ('[blink]', '</blink>')
;
insert into texttranslation (textin, textout)
values ('[font=red]', '<font color=red>')
;
insert into texttranslation (textin, textout)
values ('[font=green]', '<font color=green>')
;
insert into texttranslation (textin, textout)
values ('[font=blue]', '<font color=blue>')
;
insert into texttranslation (textin, textout)
values ('[font=orange]', '<font color=orange>')
;
insert into texttranslation (textin, textout)
values ('[font=black]', '<font color=black>')
;
insert into texttranslation (textin, textout)
values ('[font=white]', '<font color=white>')
;
insert into texttranslation (textin, textout)
values ('[font=yellow]', '<font color=yellow>')
;
insert into texttranslation (textin, textout)
values ('[font=purple]', '<font color=purple>')
;
insert into texttranslation (textin, textout)
values ('[font=pink]', '<font color=pink>')
;
insert into texttranslation (textin, textout)
values ('[font=silver]', '<font color=silver>')
;
insert into texttranslation (textin, textout)
values ('[font=small]', '<font size=1>')
;
insert into texttranslation (textin, textout)
values ('[font=medium]', '<font size=3>')
;
insert into texttranslation (textin, textout)
values ('[font=large]', '<font size=5>')
;
insert into texttranslation (textin, textout)
values ('[font=arial]', '<font face=Arial>')
;
insert into texttranslation (textin, textout)
values ('[font=century]', '<font face=Century Gothic>')
;
insert into texttranslation (textin, textout)
values ('[font=courier]', '<font face=Courier New>')
;
insert into texttranslation (textin, textout)
values ('[font=georgia]', '<font face=Georgia>')
;
insert into texttranslation (textin, textout)
values ('[font=impact]', '<font face=Impact>')
;
insert into texttranslation (textin, textout)
values ('[font=tahoma]', '<font face=Tahoma>')
;
insert into texttranslation (textin, textout)
values ('[font=times]', '<font face=Times New Roman>')
;
insert into texttranslation (textin, textout)
values ('[font=verdana]', '<font face=Verdana>')
;
insert into texttranslation (textin, textout)
values ('[/font]', '</font>')
;
insert into texttranslation (textin, textout)
values ('[blockquote]', '<blockquote>')
;
insert into texttranslation (textin, textout)
values ('[/blockquote]', '</blockquote>')
;

## Smileys

insert into texttranslation (textin, textout, smiley)
values ('[:)]', '<img src=$#context#/images/smiley/smile.gif border=0>', '1')
;
insert into texttranslation (textin, textout, smiley)
values ('[:(]', '<img src=$#context#/images/smiley/sad.gif border=0>', '1')
;
insert into texttranslation (textin, textout, smiley)
values ('[:x]', '<img src=$#context#/images/smiley/sick.gif border=0>', '1')
;
insert into texttranslation (textin, textout, smiley)
values ('[big grin]', '<img src=$#context#/images/smiley/biggrin.gif border=0>', '1')
;
insert into texttranslation (textin, textout, smiley)
values ('[;)]', '<img src=$#context#/images/smiley/wink.gif border=0>', '1')
;
insert into texttranslation (textin, textout, smiley)
values ('[question]', '<img src=$#context#/images/smiley/question.gif border=0>', '1')
;
insert into texttranslation (textin, textout, smiley)
values ('[:P]', '<img src=$#context#/images/smiley/tongue.gif border=0>', '1')
;
