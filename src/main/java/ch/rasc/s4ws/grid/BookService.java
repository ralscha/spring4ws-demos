package ch.rasc.s4ws.grid;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.rasc.wampspring.EventMessenger;
import ch.rasc.wampspring.annotation.WampCallListener;
import ch.rasc.wampspring.message.CallMessage;
import ch.rasc.wampspring.message.WampMessageHeader;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Service
public class BookService {

	@Autowired
	private EventMessenger eventMessenger;

	@WampCallListener("http://demo.rasc.ch/spring4ws/book#read")
	public Collection<Book> bookRead(CallMessage callMessage, StoreReadRequest readRequest) throws Throwable {
		System.out.println("bookRead:" + callMessage.getHeader(WampMessageHeader.WEBSOCKET_SESSION_ID));

		Collection<Book> list = BookDb.list();
		Ordering<Book> ordering = PropertyOrderingFactory.createOrderingFromSorters(readRequest.getSort());

		return ordering != null ? ordering.sortedCopy(list) : list;
	}

	@WampCallListener("http://demo.rasc.ch/spring4ws/book#create")
	public List<Book> bookCreate(CallMessage callMessage, List<Book> books) {
		System.out.println("bookCreate:" + callMessage.getHeader(WampMessageHeader.WEBSOCKET_SESSION_ID));

		List<Book> result = Lists.newArrayList();
		for (Book book : books) {
			BookDb.create(book);
			result.add(book);
		}

		eventMessenger.sendToAllExcept("http://demo.rasc.ch/spring4ws/book#oncreate", result, callMessage.getWebSocketSessionId());
		return result;
	}

	@WampCallListener("http://demo.rasc.ch/spring4ws/book#update")
	public List<Book> bookUpdate(CallMessage callMessage, List<Book> books) {
		System.out.println("bookUpdate:" + callMessage.getHeader(WampMessageHeader.WEBSOCKET_SESSION_ID));

		List<Book> result = Lists.newArrayList();
		for (Book book : books) {
			BookDb.update(book);
			result.add(book);
		}

		eventMessenger.sendToAllExcept("http://demo.rasc.ch/spring4ws/book#onupdate", result, callMessage.getWebSocketSessionId());
		return result;
	}

	@WampCallListener("http://demo.rasc.ch/spring4ws/book#destroy")
	public void bookDestroy(CallMessage callMessage, List<Book> books) throws Throwable {
		System.out.println("bookDestroy:" + callMessage.getHeader(WampMessageHeader.WEBSOCKET_SESSION_ID));
		for (Book book : books) {
			BookDb.delete(book);
		}
		eventMessenger.sendToAll("http://demo.rasc.ch/spring4ws/book#ondestroy", books);
	}
}
