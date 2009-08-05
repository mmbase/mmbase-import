#!/usr/bin/perl -w
#
# This script parse a mmbase-sql log file and produces some statistics
#
#count the number of unique queries
#cat mmbase-sql.log | sed "s,.*: ,,g" | sort | uniq -c | sort -r -n > mmbase-sql.log.unique.sorted
# Author: Kees Jongenburger
#use strict;

#array where the index is the amount of tables in the query
#the value is the total time spent running all found queries with that length
my @stats;

#array where the index is the amount of tables in the query
#the value is the amount of queries found with that lenght
my @count;

for (my $x = 0 ; $x < 15 ; $x++){
  $stats[$x] =0;
  $count[$x] =0;
}
   
my %list_of_table;
my %list_of_table_time;
my %list_of_paths;
my %list_of_paths_time;

while(<STDIN>){
   my $line =$_;
   chop $line;

   #
   # Here we match for a line that contains 2 interesting part of data
   # The first is the time spent running the query
   # the second part we are interested is the part between FROM and WHERE (containig the tables)
   if ($line =~ m/.* ([0-9]+) ms.*FROM (.*) [WHERE]{1}.*/g){
       my $time = $1;
       my $from = $2;
       my @tables = split(",",$from);
       my $tablecount = scalar(@tables);
       #print "$time tables " . scalar(@tables) . "\n";
       $stats[$tablecount] = $stats[$tablecount] +$time;
       $count[$tablecount] = $count[$tablecount] +1;

       my $path ="";
       #table stats
       foreach (@tables){
          my @ar= split(" ",$_);
          my $table = $ar[0] ;
          $table =~ s/install_//g;
          $path .= $table . " " ;

           if (exists $list_of_table{$table}){
             $list_of_table{$table} = $list_of_table{$table} +1;
             $list_of_table_time{$table} = $list_of_table_time{$table} + $time;
           } else {
             $list_of_table{$table} = 1;
             $list_of_table_time{$table} =  $time;
           }
       }
       #path stats
       if (exists $list_of_path{$path}){
         $list_of_path{$path} = $list_of_path{$path} +1;
         $list_of_path_time{$path} = $list_of_path_time{$path} + $time;
       } else {
         $list_of_path{$path} = 1;
         $list_of_path_time{$path} =  $time;
       }
       #if ($tablecount == 5){ print $line ."\n"; }
       #print scalar(@tables) . ":" . $time;
   } else {
       if ($line =~ m/[^0-9]*([0-9]+).*FROM (.*) [WHERE]{1}.*/g){
         print "hit  $line\n";
       } else {
         if ($line =~ m/UPDATE/) {
           next;
         }
         if ($line =~ m/null/) {
           next;
         }
         #print "miss  $line\n";
       }
   }
}

#sort -n -k 3 -t " " mmbase-sql.lo
#head -n 500 mmbase-sql.log| grep -v UPDATE | grep -v null | sed "s,.* \([0-9]*\) ms.*FROM ,\1:,g" | sed "s,WHERE.*,,g" 


print "queries   |levels | total time | average time\n";
print "----------------------------------------------\n";

format QUERY_STAT_FORMAT =
@#########|    @##| @######### | @#####.##
$queries,$tablecount,$total_time,$average_time
.

$~ = 'QUERY_STAT_FORMAT';
for (my $x = 1 ; $x < 10 ; $x++){
 if ( $count[$x] != 0){
      $queries    = $count[$x];
      $tablecount = $x;
      $total_time = $stats[$x];
      $average_time = $stats[$x]/$count[$x];
      write();
 }
}

print "\n\n";
print "used tables | total time    | count    | average time\n";
print "-----------------------------------------------------\n";
format TABLELIST_FORMAT =
@<<<<<<<<<< |  @########### | @<<<<<<< | @######.##
$name,$total_time,$count,$average
.
$~ = 'TABLELIST_FORMAT';
foreach ( sort { $list_of_table_time{$b} <=> $list_of_table_time{$a}}  keys %list_of_table){
  $name = $_;
  $count = $list_of_table{$_};
  $total_time = $list_of_table_time{$_};
  $average = ($list_of_table_time{$_}  / $list_of_table{$_} );
  write();
}

print "\n\n";
print "table count| total time | count    | average  | path  \n";
print "------------------------------------------------------------------------------------------------------------------------------------------------------\n";
format PATHLIST_FORMAT =
@<<<<<<<<< | @######### | @####### | @####.## | ^<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
$tcount,$total,$count,$average,$path
.
$~ = 'PATHLIST_FORMAT';
foreach ( sort { $list_of_path_time{$b} <=> $list_of_path_time{$a} } (keys %list_of_path)){
  $path = $_;
  my @tables = split(" ",$path);
  $tcount = scalar( @tables );
  $count = $list_of_path{$_};
  $total = $list_of_path_time{$_};
  $average = ($list_of_path_time{$_}  / $list_of_path{$_} );
  write();
}

