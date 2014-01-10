Ext.define('chat.view.UsersContainer', {
	extend: 'Ext.container.Container',

	layout: {
		type: 'border'
	},

	initComponent: function() {
		this.items = [ {
			xtype: 'container',
			region: 'north',
			margins: '0 0 5 0',
			layout: {
				type: 'hbox'
			},
			items: [ {
				xtype: 'textfield',
				itemId: 'usernameTf',
				fieldLabel: 'Username',
				labelWidth: 60,
				enableKeyEvents: true,
				size: 255
			}, {
				xtype: 'button',
				itemId: 'connectButton',
				margins: '0 0 0 5',
				disabled: true,
				flex: 1,
				text: 'Connect'
			} ]
		}, {
			xtype: 'gridpanel',
			region: 'center',
			itemId: 'connectedUsersGrid',
			title: 'Connected Users',
			store: Ext.create('chat.store.UserStore'),
			columns: [ {
				dataIndex: 'image',
				width: 45,
				text: '',
				sortable: false,
				renderer: function(val) {
					return '<img src="' + val + '">';
				}
			}, {
				dataIndex: 'username',
				flex: 1,
				text: 'User'
			}, {
				width: 140,
				dataIndex: 'browser',
				flex: 1,
				text: 'Browser'
			} ]
		}, {
			xtype: 'container',
			margins: '5 0 0 0',
			region: 'south',
			layout: {
				type: 'hbox'
			},
			items: [ {
				xtype: 'button',
				itemId: 'startPeerConnectionButton',
				disabled: true,
				text: 'Start Peer-to-Peer Connection'
			} ]
		} ];

		this.callParent(arguments);
	}
});
