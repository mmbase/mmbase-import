#!/usr/local/bin/perl

use strict;

my $mysql    = "/usr/local/algemeen/mysql/bin/mysql";
my $database = "test";
my @reltypes = ("typerel", "insrel", "posrel");

sub mysql {
    my $query = $_[0];
    return `echo '$query' | $mysql $database`;
}

my @tables = mysql("show tables");
shift @tables;

my %prefixes;
my %tables;

my $prefix = $tables[0];

for my $table (@tables) {
    chomp $table;
    print "Adding index to $table \n";
    #mysql("ALTER TABLE $table ADD PRIMARY KEY (number)");
    #mysql("ALTER TABLE $table MODIFY owner varchar(12) NOT NULL");
    my $prefix = $table;
    $prefix =~ s/_.*//;
    chomp $prefix;
    $prefixes{$prefix} = 1;
    $tables{$table}    = 1;
     
}

for my $prefix (keys %prefixes) {
    print "prefix: $prefix\n";
    for my $rel (@reltypes) {   
        print "adding indexes to $prefix"."_$rel\n";
        if ($tables{$prefix."_".$rel}) {
            mysql("ALTER TABLE $prefix"."_$rel MODIFY snumber int(11) NOT NULL");
            mysql("ALTER TABLE $prefix"."_$rel MODIFY dnumber int(11) NOT NULL");
            mysql("ALTER TABLE $prefix"."_$rel MODIFY rnumber int(11) NOT NULL");
            mysql("ALTER TABLE $prefix"."_$rel ADD INDEX (snumber)");
            mysql("ALTER TABLE $prefix"."_$rel ADD INDEX (dnumber)");
            mysql("ALTER TABLE $prefix"."_$rel ADD INDEX (rnumber)");
            unless ($rel =~ "typerel") {
                mysql("ALTER TABLE $prefix"."_$rel MODIFY dir int(11) NOT NULL");
            }
        }
    }
    if (@tables{$prefix."_oalias"}) {
        mysql("ALTER TABLE $prefix"."_oalias ADD INDEX (destination)");
    }
    if ($tables{$prefix."_reldef"}) {
        mysql("ALTER TABLE $prefix"."_reldef MODIFY sname varchar(32) NOT NULL");
        mysql("ALTER TABLE $prefix"."_reldef MODIFY dname varchar(32) NOT NULL");
        mysql("ALTER TABLE $prefix"."_reldef ADD INDEX (sname)");
        mysql("ALTER TABLE $prefix"."_reldef ADD INDEX (dname)");
    }
    if ($tables{$prefix."_icaches"}) {
        mysql("ALTER TABLE $prefix"."_icaches MODIFY ckey mediumblob NOT NULL");
        mysql("ALTER TABLE $prefix"."_icaches MODIFY id int(11) NOT NULL");
        mysql("ALTER TABLE $prefix"."_icaches MODIFY handle mediumblob NOT NULL");
        mysql("ALTER TABLE $prefix"."_icaches ADD INDEX (ckey(10))");
    }
