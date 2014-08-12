package ch.rasc.s4ws.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.rasc.wampspring.EventMessenger;
import ch.rasc.wampspring.annotation.WampCallListener;
import ch.rasc.wampspring.message.CallMessage;

import com.google.common.collect.Ordering;

@Service
public class BookService {

	@Autowired
	private EventMessenger eventMessenger;

	@WampCallListener("grid:read")
	public Collection<Book> bookRead(CallMessage callMessage, StoreReadRequest readRequest)
			throws Throwable {
		System.out.println("bookRead:" + callMessage.getWebSocketSessionId());

		Collection<Book> list = BookDb.list();
		Ordering<Book> ordering = PropertyOrderingFactory
				.createOrderingFromSorters(readRequest.getSort());

		return ordering != null ? ordering.sortedCopy(list) : list;
	}

	@WampCallListener("grid:create")
	public List<Book> bookCreate(CallMessage callMessage, List<Book> books) {
		System.out.println("bookCreate:" + callMessage.getWebSocketSessionId());

		List<Book> result = new ArrayList<>();
		for (Book book : books) {
			BookDb.create(book);
			result.add(book);
		}

		eventMessenger.sendToAllExcept("grid:oncreate", result,
				callMessage.getWebSocketSessionId());
		return result;
	}

	@WampCallListener("grid:update")
	public List<Book> bookUpdate(CallMessage callMessage, List<Book> books) {
		System.out.println("bookUpdate:" + callMessage.getWebSocketSessionId());

		List<Book> result = new ArrayList<>();
		for (Book book : books) {
			BookDb.update(book);
			result.add(book);
		}

		eventMessenger.sendToAllExcept("grid:onupdate", result,
				callMessage.getWebSocketSessionId());
		return result;
	}

	@WampCallListener("grid:destroy")
	public void bookDestroy(CallMessage callMessage, List<Book> books) throws Throwable {
		System.out.println("bookDestroy:" + callMessage.getWebSocketSessionId());
		for (Book book : books) {
			BookDb.delete(book);
		}
		eventMessenger.sendToAll("grid:ondestroy", books);
	}
}
