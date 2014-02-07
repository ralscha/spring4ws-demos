Ext.define('AB.data.WampStore', {
	extend: 'Ext.data.Store',
	alias: 'store.wamp',

	requires: [ 'AB.data.proxy.WampProxy' ],
	autoSync: false,

	constructor: function() {
		var me = this;
		me.callParent(arguments);

		me.model.getProxy().on('oncreate', function(proxy, obj) {
			var data = me.toArray(obj);
			me.suspendAutoSync();
			for ( var i = 0; i < data.length; i++) {
				var record = me.getById(data[i][me.model.prototype.idProperty]);

				if (record) {
					record.set(data[i]);
					record.commit();
				} else {
					var records = [ new me.model(data[i]) ], options = {
						addRecords: true,
						start: 0
					};
					me.loadRecords(records, options);
				}

				if (!(me.remoteSort || me.buffered)) {
					me.sort();
				}

				me.resumeAutoSync();
			}
		});

		me.model.proxy.on('onupdate', function(proxy, obj) {
			var data = me.toArray(obj);
			me.suspendAutoSync();
			for ( var i = 0; i < data.length; i++) {
				var record = me.getById(data[i][me.model.prototype.idProperty]);
				if (record) {
					record.set(data[i]);
					record.commit();
				}
			}

			if (!(me.remoteSort || me.buffered)) {
				me.sort();
			}

			me.resumeAutoSync();
		});

		me.model.proxy.on('ondestroy', function(proxy, obj) {
			var data = me.toArray(obj);
			me.suspendAutoSync();
			for ( var i = 0; i < data.length; i++) {
				var record = me.getById(data[i][me.model.prototype.idProperty]);
				if (record) {
					me.remove(record);
				}
			}
			me.resumeAutoSync();
		});
	},

	toArray: function(obj) {
		if (Ext.isArray(obj)) {
			return obj;
		} else {
			return [ obj ];
		}
	}
});