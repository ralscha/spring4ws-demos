package ch.rasc.s4ws.grid;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.rasc.s4ws.wamp.EventMessenger;
import ch.rasc.s4ws.wamp.annotation.WampCallListener;
import ch.rasc.s4ws.wamp.message.CallMessage;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Service
public class BookService {

	@Autowired
	private EventMessenger eventMessenger;

	@WampCallListener("http://demo.rasc.ch/spring4ws/book#read")
	public Collection<Book> bookRead(CallMessage callMessage, StoreReadRequest readRequest) throws Throwable {
		System.out.println("bookRead:" + callMessage.getSessionId());

		Collection<Book> list = BookDb.list();
		Ordering<Book> ordering = PropertyOrderingFactory.createOrderingFromSorters(readRequest.getSorters());

		return ordering != null ? ordering.sortedCopy(list) : list;
	}

	@WampCallListener("http://demo.rasc.ch/spring4ws/book#create")
	public List<Book> bookCreate(CallMessage callMessage, List<Book> books) {
		System.out.println("bookCreate:" + callMessage.getSessionId());

		List<Book> result = Lists.newArrayList();
		for (Book book : books) {
			BookDb.create(book);
			result.add(book);
		}

		eventMessenger.send("http://demo.rasc.ch/spring4ws/book#oncreate", result);
		return result;
	}

	@WampCallListener("http://demo.rasc.ch/spring4ws/book#update")
	public List<Book> bookUpdate(CallMessage callMessage, List<Book> books) {
		System.out.println("bookUpdate:" + callMessage.getSessionId());

		List<Book> result = Lists.newArrayList();
		for (Book book : books) {
			BookDb.update(book);
			result.add(book);
		}

		eventMessenger.send("http://demo.rasc.ch/spring4ws/book#onupdate", result,
				Collections.singleton(callMessage.getSessionId()));
		return result;
	}

	@WampCallListener("http://demo.rasc.ch/spring4ws/book#destroy")
	public void bookDestroy(CallMessage callMessage, List<Book> books) throws Throwable {
		System.out.println("bookDestroy:" + callMessage.getSessionId());
		for (Book book : books) {
			BookDb.delete(book);
		}
		eventMessenger.send("http://demo.rasc.ch/spring4ws/book#ondestroy", books);
	}
}
