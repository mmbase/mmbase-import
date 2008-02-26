
Ext.onReady(function(){
    var validator = new MMBaseValidator();
    validator.validateHook = function(valid) {
        document.getElementById('submit').disabled = ! valid;
    }
    validator.prefetchNodeManager("xmlnews");
    validator.setup(window);

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
                contentEl: 'main',
		autoScroll: true
            },
            {
                region: 'east',
		title: 'Relations',
		split:true,
		collapsible: true,
                contentEl: 'relations'
            },
            {
                region: 'south',
		collapsible: true,
                contentEl: 'commit'
            }
	]
    });

});
