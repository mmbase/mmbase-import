<%
Vector fields;

fields = new Vector(); fields.add(new Integer(200)); fields.add("artikel.titel"); fields.add("artikel.intro");
nodePaths.put("artikel", fields);

fields = new Vector(); fields.add(new Integer(300)); fields.add("paragraaf.titel"); fields.add("paragraaf.tekst");
nodePaths.put("artikel,posrel,paragraaf", fields);

fields = new Vector(); fields.add(new Integer(200)); fields.add("teaser.titel"); fields.add("teaser.omschrijving");
nodePaths.put("teaser", fields);

fields = new Vector(); fields.add(new Integer(300)); fields.add("producttypes.title");
nodePaths.put("teaser,posrel,producttypes", fields);

fields = new Vector(); fields.add(new Integer(300)); fields.add("products.titel"); fields.add("products.omschrijving");
nodePaths.put("teaser,posrel,producttypes,posrel,products", fields);

fields = new Vector(); fields.add(new Integer(200)); fields.add("producttypes.title");
nodePaths.put("producttypes", fields);

fields = new Vector(); fields.add(new Integer(300)); fields.add("products.titel"); fields.add("products.omschrijving");
nodePaths.put("producttypes,posrel,products", fields);

fields = new Vector(); fields.add(new Integer(200)); fields.add("items.titel"); fields.add("items.intro"); fields.add("items.body");
nodePaths.put("items", fields);

fields = new Vector(); fields.add(new Integer(200)); fields.add("documents.filename");
nodePaths.put("documents", fields);

fields = new Vector(); fields.add(new Integer(300)); 
fields.add("vacature.titel");
fields.add("vacature.functienaam"); 
fields.add("vacature.omschrijving");		
fields.add("vacature.functieinhoud"); 
fields.add("vacature.functieomvang"); 
fields.add("vacature.duur"); 
fields.add("vacature.afdeling"); 
fields.add("vacature.functieeisen"); 
fields.add("vacature.opleidingseisen"); 
fields.add("vacature.competenties"); 
fields.add("vacature.salarisschaal");
fields.add("vacature.metatags");
nodePaths.put("vacature", fields);
%>