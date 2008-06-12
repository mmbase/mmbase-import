#!/usr/bin/perl
$module_dir = shift @ARGV;

foreach $file (@ARGV) {
    if ($file =~ m/\.(xml|jspx|xsl|xslt|tagx|tld)$/) {
	if (open FIL, "<$file") {
	    close FIL;
	    if ($file =~ m/buildbase.xml$/) {
		print "Ignoring buildbase.xml, because it is known to be invalid xml\n";
	    } else {
		$files .= " $file";
	    }
	} else {
	    print "Ignoring file '$file', cannot open it\n";
	}
    }
}

if ($files ne "") {
    print "Testing for XML compliancy:$files\n";

    #@output = `SGML_CATALOG_FILES=/usr/share/sgml/docbook/xmlcatalog:/usr/share/sgml/docbook/dtd/xml/4.1.2/catalog.xml /usr/bin/xmllint --valid --noout $files 2>&1`;

    @output = `/usr/bin/xmllint --noout $files 2>&1 | grep -v '^warning:'`;

    for $line (@output) {
	print STDERR $line;
    }
    if ($? != 0) {
	exit(-1);
    }
    if ($#output > 0) {
	exit(-1);
    }
    exit(0);
}
