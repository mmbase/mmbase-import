<mm:notpresent referid="imageonly"
	><td class="bottom" background="<mm:related path="posrel,images" constraints="posrel.pos=1"
					><mm:node element="images"
						><mm:image template="s(100x50)" 
					/></mm:node
				></mm:related>" style="background-position:left bottom;background-repeat:no-repeat;">
	<table cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td style="width:100%;height:50px;">
				<mm:field name="type"
					><mm:isnotempty
						><mm:compare value="standaard" inverse="true"
						><mm:compare value="niet_te_koop" inverse="true"
						><img style="float:right;" src="media/<mm:write />.gif"></mm:compare
					   ></mm:compare
					></mm:isnotempty
				></mm:field
				><a href="<mm:url page="<%= shop_itemHref %>" />" style="display:block;width:auto;height:50px;" ><img src="media/spacer.gif" border="0" alt=""></a>
			</td>
			<td class="bottom" style="padding-bottom:10px;padding-right:4px;">
				<span class="subtitle">Meer</span>&nbsp;<a href="<mm:url page="<%= shop_itemHref %>" />" class="subtitle"><span style="font-weight:normal;">informatie</span></a>
			</td>
			<td class="bottom" style="padding-bottom:10px;padding-right:2px;">
				<a href="<mm:url page="<%= shop_itemHref %>" />"><img src="media/pijl_oranje_op_wit.gif" border="0" alt=""></a><br>
			</td>
		</tr>
	</table>
	</td>
</mm:notpresent
><mm:present referid="imageonly"
	><td class="bottom" background="<mm:related path="posrel,images" constraints="posrel.pos=1"
						><mm:node element="images"
							><mm:image template="s(195x80)" 
						/></mm:node
					></mm:related>" style="background-position:center bottom;background-repeat:no-repeat;">
	<mm:field name="type"
		><mm:isnotempty
			><mm:compare value="standaard" inverse="true"
			><mm:compare value="niet_te_koop" inverse="true"
		      ><img style="float:right;margin-right:30px;margin-top:5px;" src="media/<mm:write />.gif"></mm:compare
		   ></mm:compare
		></mm:isnotempty
	></mm:field
	><a style="display:block;width:auto;height:80px;" href="<mm:url page="<%= shop_itemHref %>" />" ><img src="media/spacer.gif" border="0" alt=""></a></td>
</mm:present>
