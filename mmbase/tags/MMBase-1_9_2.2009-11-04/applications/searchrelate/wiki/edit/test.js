
Ext.onReady(function(){
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

    var viewport = new Ext.Viewport({
        layout:'border',
        items:[
            {
                region: 'north',
                contentEl: 'title',
                autoHeight: true
            },
            {
                region: 'center',
                contentEl: 'main'
            },
            {
                region: 'east',
		title: 'Relations',
		split:true,
		collapsable: true,
                contentEl: 'relations'
            }
	]
    });
});

