package ch.rasc.s4ws.scheduler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;

public class CustomEventDb {
	private final static Map<Integer, CustomEvent> db = new ConcurrentHashMap<>();

	private final static AtomicInteger lastId = new AtomicInteger();

	static {
		db.put(1, new CustomEvent(1, 2, "Chase turkey", new DateTime(2013, 3, 1, 8, 0, 0), new DateTime(2013, 3, 1, 10,
				0, 0), true));
		db.put(2, new CustomEvent(2, 1, "Stuff turkey", new DateTime(2013, 3, 1, 10, 0, 0), new DateTime(2013, 3, 1,
				12, 0, 0), true));
		db.put(3, new CustomEvent(3, 3, "Cook turkey", new DateTime(2013, 3, 1, 12, 0, 0), new DateTime(2013, 3, 1, 15,
				0, 0), true));
		db.put(4, new CustomEvent(4, 5, "Set table", new DateTime(2013, 3, 1, 14, 0, 0), new DateTime(2013, 3, 1, 16,
				0, 0), false));
		db.put(5, new CustomEvent(5, 4, "Serve dinner", new DateTime(2013, 3, 1, 16, 0, 0), new DateTime(2013, 3, 1,
				19, 0, 0), false));
		db.put(6, new CustomEvent(6, 6, "Hack on Java/Portal", new DateTime(2013, 3, 1, 16, 0, 0), new DateTime(2013,
				3, 1, 18, 30, 0), false));
		db.put(7, new CustomEvent(7, 7, "Clean up", new DateTime(2013, 3, 1, 19, 0, 0), new DateTime(2013, 3, 1, 20,
				30, 0), false));
		db.put(78, new CustomEvent(8, 8, "Do laundry", new DateTime(2013, 3, 1, 17, 0, 0), new DateTime(2013, 3, 1, 19,
				0, 0), false));

		lastId.set(8);
	}

	public static Collection<CustomEvent> list() {
		return Collections.unmodifiableCollection(db.values());
	}

	public static void create(CustomEvent newEvent) {
		newEvent.setId(lastId.incrementAndGet());
		db.put(newEvent.getId(), newEvent);
	}

	public static CustomEvent read(int id) {
		return db.get(id);
	}

	public static void update(CustomEvent updatedEvent) {
		db.put(updatedEvent.getId(), updatedEvent);
	}

	public static void delete(List<Integer> ids) {
		for (Integer id : ids) {
			db.remove(id);
		}
	}
}
