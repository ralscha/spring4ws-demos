Ext.Loader.setConfig({
	enabled: true,
	disableCaching: true
});

// Limit the resolution, to avoid putting too much data on the wire.
Sch.preset.Manager.getPreset('hourAndDay').timeResolution.increment = 15;
var absession;

Ext.application({
	name: 'App',
	appFolder: 'app',

	viewport: {
		layout: {
			type: 'vbox',
			align: 'stretch'
		}
	},

	launch: function() {
		var path = window.location.pathname.split('/');
		var me = this;

		ab._construct = function(url, protocols) {
			return new SockJS(url);
		};

		ab.connect('/' + path[1] + '/scheduler', function(session) {
			absession = session;
			console.log("Connected to ", absession);
			me.guiLaunch();
		}, function(code, reason) {
			absession = null;
			console.log("Connection lost (" + reason + ")");
		}, {
			skipSubprotocolCheck: true
		});
	},

	// Initialize application
	guiLaunch: function() {
		var field = new Ext.form.Text({
			fieldLabel: 'User ',
			height: 30,
			label: 'User ',
			value: 'John Doe',
			labelWidth: 115,
			listeners: {
				change: function(field, value) {
					scheduler.userName = value;
				}
			}
		});

		var scheduler = Ext.create('App.view.SchedulerGrid', {
			title: 'Springframework 4/SockJS/AutobahnJS/ExtScheduler (Bryntum) example',
			startDate: new Date(2013, 2, 1, 7, 0),
			endDate: new Date(2013, 2, 1, 23, 30),
			flex: 1,
			userName: field.getValue()
		});

		var vp;

		if (Ext.versions.touch) {
			vp = Ext.Viewport;
		} else {
			// Ext JS
			vp = new Ext.Viewport(this.viewport);

			// Uncomment this to see what's happening in the EventStore
			// Ext.util.Observable.capture(scheduler.eventStore, function() {
			// console.log(arguments); });

			scheduler.on('eventcontextmenu', this.onEventContextMenu, this);
			Ext.QuickTips.init();
		}

		vp.add([ new Ext.form.Panel({
			region: 'north',
			hidden: Ext.os && Ext.os.is.phone,
			padding: 5,
			border: false,
			height: 55,
			items: field
		}), scheduler ]);
	},

	onEventContextMenu: function(scheduler, rec, e) {
		e.stopEvent();

		if (!scheduler.gCtx) {
			scheduler.gCtx = new Ext.menu.Menu({
				items: [ {
					text: 'Delete event',
					iconCls: 'icon-delete',
					handler: function() {
						scheduler.eventStore.remove(scheduler.gCtx.rec);
					}
				} ]
			});
		}
		scheduler.gCtx.rec = rec;
		scheduler.gCtx.showAt(e.getXY());
	}
});
