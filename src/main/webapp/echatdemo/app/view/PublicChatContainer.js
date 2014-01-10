Ext.define('chat.view.PublicChatContainer', {
	extend: 'Ext.container.Container',

	layout: {
		type: 'border'
	},

	initComponent: function() {
		this.items = [ {
			xtype: 'panel',			
			region: 'center',
			title: 'Public Chat',
			bodyPadding: 5,
			autoScroll: true,
			items: [ new Ext.view.View({
				store : Ext.create('chat.store.ChatMessageStore'),
				itemId: 'chatView',				
				tpl: new Ext.XTemplate('<tpl for=".">', '<p>{username}: {message}</p>', '</tpl>')
			}) ]
		}, {
			xtype: 'container',
			margins: '5 0 0 0',
			region: 'south',
			layout: {
				type: 'hbox'
			},
			items: [ {
				xtype: 'textfield',
				itemId: 'messageTf',
				disabled: true,
				enableKeyEvents: true,
				flex: 1,
				fieldLabel: 'Message',
				labelWidth: 55
			}, {
				xtype: 'button',
				itemId: 'sendButton',
				disabled: true,
				margins: '0 0 0 5',
				width: 60,
				text: 'Send'
			} ]
		} ];

		this.callParent(arguments);
	}
});
