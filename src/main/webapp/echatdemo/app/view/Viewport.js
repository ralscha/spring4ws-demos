Ext.define('chat.view.Viewport', {
	extend: 'Ext.container.Viewport',
	controller: 'chat.controller.ChatController',
	
	renderTo: Ext.getBody(),

	padding: 10,

	layout: {
		type: 'border'
	},

	initComponent: function() {

		this.items = [ {
			xtype: 'container',
			flex: 1,
			region: 'north',
			split: true,
			layout: {
				align: 'stretch',
				type: 'hbox'
			},
			items: [ Ext.create('chat.view.UsersContainer', {
				flex: 1,
				margins: '0 5 0 0'
			}), Ext.create('chat.view.PublicChatContainer', {
				flex: 3
			}) ],
		}, {
			xtype: 'container',
			flex: 1,
			margins: '10 0 0 0',
			region: 'center',
			layout: {
				align: 'stretch',
				type: 'hbox'
			},
			items: [ {
				xtype: 'panel',
				itemId: 'localVideo',
				margins: '0 5 0 0',
				flex: 1,
				layout: 'fit',
				title: 'Local'
			}, {
				xtype: 'panel',
				itemId: 'remoteVideo',
				flex: 1,
				layout: 'fit',
				title: 'Remote'
			} ]
		} ];

		this.callParent(arguments);
	}

});