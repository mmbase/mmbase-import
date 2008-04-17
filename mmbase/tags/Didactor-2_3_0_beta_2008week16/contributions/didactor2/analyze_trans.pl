#!/usr/bin/perl
# This tool will analyze all translation files and all JSP files, 
# and print warnings if there are problems with the translations.
# The following problems are detected:
# - translations that are defined multiple times in the same file
# - translations that are never used in JSP
# - translations that are referred to in JSP, but are not defined in
#   property files
#
# This tool is written rather quick-and-dirty and will onl y work on unix.
#
# Author: Johannes Verelst <johannes.verelst@eo.nl>

use strict;
my @transfiles = `cd src; find . -name "*.properties" | grep "config/translations"`;
my $line;
my $file;
my %translations = ();
my %usage = ();

# If more than the two translations 'nl' and 'default' are specified, 
# define them here.
$translations{"nl"} = ();
$translations{"default"} = ();

# Step 1: read all translation files and warn about duplicate keys
foreach $line (@transfiles) {
  chomp $line;
  my ($dot, $component, $config, $translations, $filename) = split('/', $line);
  my @f = split('\.', $filename);
  my $lang;
  if ($#f == 2) {
    $lang = $f[1]
  } else {
    $lang = "default";
  }
  open FOPEN, "<$line";
  while ($line = <FOPEN>) {
    if ($line =~ m/^$/) {
      next;
    }
    my ($key, $value) = split('=', $line);
    $key = $component . "." . $key;
    if (exists $translations{$lang}{$key}) {
      print "WARNING1: Key '" . $key . "' already defined in language $lang!\n";
    }
    $translations{$lang}{$key} = $value;
  }
  close FOPEN;
}

# Step 2: find 'di:translate' tags in all JSP files, and warn about 
# non-existant translation keys.
my @jspfiles = `cd src; find . -name *.jsp`;
foreach $file (@jspfiles) {
  chomp $file;
  open FOPEN, "<$file";
  while ($line = <FOPEN>) {
    chomp $line;
    while ($line =~ m/.*?<di:translate\s*key="(.*?)"\s*\/>(.*)/) {
      my $key = $1;
      $line = $2;
      if ($key =~ m/<%/) {
        next;
      }
      my $lang;
      foreach $lang (keys %translations) {
        if (!exists $translations{$lang}{$key}) {
          print "WARNING2: Key '$key' used in file '$file' not defined in language '$lang'\n";
        }
      }
      $usage{$key} = "1";
    }
  }
}

# Step 3: warn about non-used translations. 
# The following translations are ignored here:
# - mmbob related
# - education question related, because they are used in a dynamic way
my $lang;
foreach $lang (keys %translations) {
  my $key;
  foreach $key (keys %{$translations{$lang}}) {
    if ($key =~ m/mmbob\./) {
      next;
    }
    if ($key =~ m/education.createnew.*question/) {
      next;
    }
    if (!exists $usage{$key}) {
      print "WARNING3: Key '$key' language '$lang' is never used\n";
    }
  }
}
