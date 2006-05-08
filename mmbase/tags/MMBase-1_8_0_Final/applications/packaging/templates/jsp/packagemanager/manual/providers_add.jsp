<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="85%">
<tr>
	<th colspan="2">
	Manual : Adding providers
	</th>
</tr>
<tr>
	<td colspan="2">
	<p />
	All the bundles/packages your MMBase setup finds are found by a provider. A provider is a source for bundles and packages that allows you to install them indepentant of place and type of medium it is on. The two most used are disk based providers as your import/build dir (see special providers) and http (internet) providers as packages.mmbase.org. More providers can be added to give you access to more (and possibly restricted) bundles/packages on the fly. The following steps explain how to add a provider and check if its working and providing you with packages and bundles.
	<p />
	</td>
</tr>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="85%">
<tr>
	<th align="left">
	Step 1: provider overview
	</th>
</tr>
<tr>
	<td>
	<p/>
	<p/>
	<center><img src="manual/images/providers_add1.jpg" width="95%" /></center>
	<p />
	<p />
	By pressing <b>providers</b> <b>[1]</b> in the navigation bar you will goto the providers overview page. You will be presented with all your current providers and their status (if they are up and providing you with bundles/packages). Mostly these will be a mix between http and disk providers in the default distrobution it starts with 3 import, build and the MMBase foundation package server (that provides the examples). Since we want to add a new provider select the 'add provider' option [2].
	</td>
</tr>
</table>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="85%">
<tr>
	<th align="left">
	Step 2: Providers adding provider page
	</th>
</tr>
<tr>
	<td>
	<p/>
	<p/>
	<center><img src="manual/images/providers_add2.jpg" width="95%" /></center>
	<p />
	<p />
	In this example we want to add a http provider setup by Submarine. You will see that there are several ways (methods) to add a provider this is done to make it easer. Only one method is needed [1][3] or [2][4]. Since the Submarine package server is in the 'hotlist' of partner servers [3][4] can be used. For other providers you need to be given a url by the creators of the provider. We advice people to use the packages.myserver.[com][nl][org] dns naming so it should be easy to guess the package server name once you know who is running it.
	</td>
</tr>
</table>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="85%">
<tr>
	<th align="left">
	Step 3: Adding provider using method one.
	</th>
</tr>
<tr>
	<td>
	<p/>
	<p/>
	<center><img src="manual/images/providers_add3.jpg" width="95%" /></center>
	<p />
	<p />
	In this example we use method 1 to add the provider, So enter the name of the package server [1] and per press add provider [2].
	</td>
</tr>
</table>



<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="85%">
<tr>
	<th align="left">
	Step 4: Provider added, feedback to confirm
	</th>
</tr>
<tr>
	<td>
	<p/>
	<p/>
	<center><img src="manual/images/providers_add4.jpg" width="95%" /></center>
	<p />
	<p />
	MMBase will return to the same screen (so you can add more providers) but will provide feedback if the adding was performed [1].
	</td>
</tr>
</table>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="85%">
<tr>
	<th align="left">
	Step 5: provider overview
	</th>
</tr>
<tr>
	<td>
	<p/>
	<p/>
	<center><img src="manual/images/providers_add5.jpg" width="95%" /></center>
	<p />
	<p />
	Go back to the provider overview when you are done adding providers, It should now show the added provider(s) and there status. You might need to reload a few times (by pressing providers again for example) since it can take 10 to 20 seconds before the provider [1]  will be shown as 'up' in the status area [2]. If all is well you will now find more packages and bundles in the correct pages of the package manager.
	</td>
</tr>
</table>



