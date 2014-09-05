Ext.define('Ext.ux.ws.wamp.Store', {
	extend: 'Ext.data.Store',
	alias: 'store.wamp',

	requires: [ 'Ext.ux.ws.wamp.Proxy' ],
	autoSync: false,

	constructor: function() {
		var me = this;
		me.callParent(arguments);
		var proxy = me.model.getProxy();
		me.mon(proxy,{
			scope: this,
			oncreate: proxy.api.oncreate ? me.onCreate : Ext.emptyFn,
			onupdate: proxy.api.onupdate ? me.onUpdate : Ext.emptyFn,
			ondestroy: proxy.api.ondestroy ? me.onDestroy : Ext.emptyFn
		});
	},
	/**
	 * @param proxy wamp proxy
	 * @param obj
	 */
	onCreate: function(proxy, obj) {
		var me = this, data = me.toArray(obj);
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
		}
		me.resumeAutoSync();
	},
	/**
	 * @param proxy wamp proxy
	 * @param obj
	 */
	onUpdate: function(proxy, obj) {
		var me = this, data = me.toArray(obj);
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
	},
	/**
	 * @param proxy wamp proxy
	 * @param obj
	 */
	onDestroy: function(proxy, obj) {
		var me = this, data = me.toArray(obj);
		me.suspendAutoSync();
		for ( var i = 0; i < data.length; i++) {
			var record = me.getById(data[i][me.model.prototype.idProperty]);
			if (record) {
				me.remove(record);
			}
		}
		me.resumeAutoSync();
	},
	toArray: function(obj) {
		if (Ext.isArray(obj)) {
			return obj;
		}
		return [ obj ];
	}
});