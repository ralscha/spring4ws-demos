Ext.define('App.store.mixin.WampIO', {

	initSocket: function() {

		var me = this;
		
		absession.subscribe('http://demo.rasc.ch/spring4ws/schdemo#serverDoInitialLoad', function(topic, event) {
			me.onInitialLoad(event);
		});
		absession.subscribe('http://demo.rasc.ch/spring4ws/schdemo#serverDoUpdate', function(topic, event) {
			me.onRemoteUpdate(event);
		});
		absession.subscribe('http://demo.rasc.ch/spring4ws/schdemo#serverDoAdd', function(topic, event) {
			me.onRemoteAdd(event);
		});
		absession.subscribe('http://demo.rasc.ch/spring4ws/schdemo#serverSyncId', function(topic, event) {
			me.onRemoteSyncId(event);
		});
		absession.subscribe('http://demo.rasc.ch/spring4ws/schdemo#serverDoRemove', function(topic, event) {
			me.onRemoteRemove(event);
		});

		this.myListeners = {
			add: this.onLocalAdd,
			update: this.onLocalUpdate,
			remove: this.onLocalRemove,
			addrecords: this.onLocalAdd,
			updaterecord: this.onLocalUpdate,
			removerecords: this.onLocalRemove
		};

		this.addMyListeners();

		// Load initial data to Store from Server
		this.doInitialLoad();
	},

	addMyListeners: function() {
		// Add event listeners to store operations
		this.on(this.myListeners);
	},

	removeMyListeners: function() {
		// Add event listeners to store operations
		this.un(this.myListeners);
	},

	/**
	 * Send event to server in order to receive initial data for store from the
	 * DB.
	 */
	doInitialLoad: function() {
		absession.publish('http://demo.rasc.ch/spring4ws/schdemo#clientDoInitialLoad');
	},

	/* BEGIN REMOTE LISTENER METHODS */

	/**
	 * New records were added remotely, add them to our local client store
	 */
	onRemoteAdd: function(data) {
		var records = data.records, current, model = this.getModel();

		this.removeMyListeners();

		for ( var i = 0, l = records.length; i < l; i += 1) {
			current = records[i];

			// change dates from JSON form to Date
			current.startDate = new Date(current.StartDate);
			current.endDate = new Date(current.EndDate);

			this.add(new model(current, current.Id));
		}

		this.addMyListeners();
	},

	onRemoteSyncId: function(data) {
        var records = data.records,
            model = this.getModel();

		this.removeMyListeners();

        Ext.Array.each(records, function(updatedRecord) {
            var internalId = updatedRecord.internalId;

			this.each(function(rec, idx) {
				if (rec.internalId == internalId) {
                    this.remove(rec);
                    this.add(new model(updatedRecord.record, updatedRecord.record.Id));
					return false;
				}
			}, this);
		}, this);

		this.addMyListeners();
	},

	/**
	 * Records were updated remotely, update them in our local client store
	 */
	onRemoteUpdate: function(data) {
		var localRecord;

		this.removeMyListeners();

		localRecord = this.getById(data.Id);
		if (localRecord) {
			data.StartDate && (data.StartDate = new Date(data.StartDate));
			data.EndDate && (data.EndDate = new Date(data.EndDate));

			localRecord.set(data);
		}

		this.addMyListeners();
	},

	/**
	 * Records were removed remotely, remove them from our local client store
	 */
	onRemoteRemove: function(data) {
		var ids = data.ids, record, current;

		this.removeMyListeners();

		for ( var i = 0, l = ids.length; i < l; i += 1) {
			current = ids[i];
			record = this.getById(current);

			this.remove(record);
		}

		this.addMyListeners();
	},

	/**
	 * Initial data loaded from server.
	 */
	onInitialLoad: function(data) {
		(this.loadData || this.setData).call(this, data.data);
	},

	/* EOF REMOTE LISTENER METHODS */


	/* BEGIN LOCAL STORE LISTENER METHODS */

	/**
	 * On adding records to client store, send event to server and add items to
	 * DB.
	 */
	onLocalAdd: function(store, records, index, opts) {
		var recordsData = [];
		records = records.length ? records : [ records ];

		for ( var i = 0, l = records.length; i < l; i += 1) {
			records[i].data.Name = 'New Assignment';

			var d = {
				data: records[i].data,
				internalId: records[i].internalId
			};

			if (Ext.versions.touch) {
				delete d.data.Id;
			}

			recordsData.push(d);
		}

		absession.publish('http://demo.rasc.ch/spring4ws/schdemo#clientDoAdd', recordsData);
	},

	/**
	 * On updating records in client store, send event to server and update
	 * items in DB.
	 */
	onLocalUpdate: function(store, record) {
		absession.publish('http://demo.rasc.ch/spring4ws/schdemo#clientDoUpdate', record.data);
	},

	/**
	 * On adding removing records from client store, send event to server and
	 * remove items from DB.
	 */
	onLocalRemove: function(store, records, index, opts) {
		records = records.length ? records : [ records ];
		var ids = Ext.Array.map(records, function(rec) {
			return rec.get('Id');
		});

		absession.publish('http://demo.rasc.ch/spring4ws/schdemo#clientDoRemove', ids);
	}

/* EOF LOCAL STORE LISTENER METHODS */
});