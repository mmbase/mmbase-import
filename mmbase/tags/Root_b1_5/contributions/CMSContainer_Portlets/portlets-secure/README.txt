DESCRIPTION:
A secure implementation of the content channel portlet and content portlet.
Will update all content elements with a "secure" field.
You will have to update all your non standard content element edit wizards to have a "secure" field,
use the editwizards here for the standard content elements as example.

INSTALL:
When installing on an existing container, run the following sql (also create sql like this for your 
non standard content elements).

ALTER TABLE mm_contentelement ADD COLUMN secure TINYINT(1); 
UPDATE mm_contentelement SET secure = 0;
ALTER TABLE mm_article ADD COLUMN secure TINYINT(1); 
UPDATE mm_article SET secure = 0;
ALTER TABLE mm_faqcategory ADD COLUMN secure TINYINT(1); 
UPDATE mm_faqcategory SET secure = 0;
ALTER TABLE mm_faqitem ADD COLUMN secure TINYINT(1); 
UPDATE mm_faqitem SET secure = 0;
ALTER TABLE mm_link ADD COLUMN secure TINYINT(1); 
UPDATE mm_link SET secure = 0;

ALTER TABLE live_contentelement ADD COLUMN secure TINYINT(1); 
UPDATE live_contentelement SET secure = 0;
ALTER TABLE live_article ADD COLUMN secure TINYINT(1); 
UPDATE live_article SET secure = 0;
ALTER TABLE live_faqcategory ADD COLUMN secure TINYINT(1); 
UPDATE live_faqcategory SET secure = 0;
ALTER TABLE live_faqitem ADD COLUMN secure TINYINT(1); 
UPDATE live_faqitem SET secure = 0;
ALTER TABLE live_link ADD COLUMN secure TINYINT(1); 
UPDATE live_link SET secure = 0;
