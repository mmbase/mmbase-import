#!/usr/bin/perl
$module_dir = shift @ARGV;
if ($module_dir ne "CVSROOT") {
    print "You cannot check in in CVS any more. We migrated to Subversion. Please use https://scm.mmbase.org.\n";
    exit(-1);
} else {
    exit(0);
}
