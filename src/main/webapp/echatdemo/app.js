Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'chat': 'app'
	}
});

Ext.onReady(function() {
	Ext.create('chat.view.Viewport');
});
