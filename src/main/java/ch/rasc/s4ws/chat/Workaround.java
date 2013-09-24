package ch.rasc.s4ws.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class Workaround {

	@MessageMapping(value = "/queue/chatmessage")
	@SendTo("/queue/chatmessage")
	public ChatMessage workaround(ChatMessage msg) {
		return msg;
	}
}
