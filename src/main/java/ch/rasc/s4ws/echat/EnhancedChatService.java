package ch.rasc.s4ws.echat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.rasc.wampspring.EventMessenger;
import ch.rasc.wampspring.annotation.WampCallListener;
import ch.rasc.wampspring.annotation.WampUnsubscribeListener;
import ch.rasc.wampspring.message.CallMessage;
import ch.rasc.wampspring.message.UnsubscribeMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

@Service
public class EnhancedChatService {

	private static final String DATA_IMAGE = "data:image/png;base64,";

	private final static Logger logger = LoggerFactory.getLogger(EnhancedChatService.class);

	private final static ObjectMapper mapper = new ObjectMapper();

	private final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

	private final Map<String, UserConnection> socketIdToUserMap = Maps.newConcurrentMap();
	
	@Autowired
	private EventMessenger eventMessenger;
	
	@WampCallListener
	public Collection<UserConnection> readConnectedUsers() {
		return socketIdToUserMap.values();
	}
	
	@WampCallListener("connect")
	public void connect(CallMessage callMessage, UserConnection newUser) {
		ReadableUserAgent ua = parser.parse(newUser.getBrowser());
		if (ua != null) {
			newUser.setBrowser(ua.getName() + " " + ua.getVersionNumber().getMajor());
		}		
		
		newUser.setSessionId(callMessage.getWebSocketSessionId());
		socketIdToUserMap.put(callMessage.getWebSocketSessionId(), newUser);
		eventMessenger.sendToAll("connected", newUser);
	}
	
	@WampUnsubscribeListener("message") 
	public void unsubscribeClient(UnsubscribeMessage unsubscribeMessage) {
		UserConnection uc = socketIdToUserMap.remove(unsubscribeMessage.getWebSocketSessionId());
		if (uc != null) {
			eventMessenger.sendToAll("disconnected", uc);
		}
	}

	@WampCallListener("hangup")
	public void hangup(String connectedWith) {
		String webSocketSessionId = findUserConnection(connectedWith);
		if (webSocketSessionId != null) {
			eventMessenger.sendTo("hangup", null, Collections.singleton(webSocketSessionId));
		}
	}

	private String findUserConnection(String userName) {
		for (String webSocketSessionId : socketIdToUserMap.keySet()) {
			UserConnection uc = socketIdToUserMap.get(webSocketSessionId);
			if (uc.getUsername().equals(userName)) {
				return webSocketSessionId;
			}
		}
		return null;
	}
	
	
	@WampCallListener("snapshot")
	public void snapshot(CallMessage callMessage, String image) {
		UserConnection uc = socketIdToUserMap.get(callMessage.getWebSocketSessionId());
		if (uc != null && image.startsWith(DATA_IMAGE)) {
			try {
				byte[] imageBytes = DatatypeConverter.parseBase64Binary(image.substring(DATA_IMAGE.length()));
				String resizedImageDataURL = resize(imageBytes);
				uc.setImage(resizedImageDataURL);
				eventMessenger.sendToAll("snapshot", uc);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	private static String resize(byte[] imageData) throws IOException {
	try (ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
			ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
		BufferedImage image = ImageIO.read(bis);

		BufferedImage resizedImage = Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 40,
				Scalr.OP_ANTIALIAS);
		ImageIO.write(resizedImage, "png", bos);
		return DATA_IMAGE + DatatypeConverter.printBase64Binary(bos.toByteArray());
	}

	}
	
	@WampCallListener("sendSdp")
	public void sendSdp(Map<String, Object> offerObject) {
		String toUsername = (String) offerObject.get("toUsername");
		String webSocketSessionId = findUserConnection(toUsername);
		if (webSocketSessionId != null) {
			eventMessenger.sendTo("receiveSdp", offerObject, Collections.singleton(webSocketSessionId));
		}

		if (logger.isDebugEnabled()) {
			try {
				logger.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(offerObject));
			} catch (IOException e) {
				// ignore this
			}
		}
	}
	
	@WampCallListener("sendIceCandidate")
	public void sendIceCandidate(Map<String, Object> candidate) {
		String toUsername = (String) candidate.get("toUsername");
		String webSocketSessionId = findUserConnection(toUsername);
		if (webSocketSessionId != null) {
			eventMessenger.sendTo("receiveIceCandidate", candidate, Collections.singleton(webSocketSessionId));
		}

		if (logger.isDebugEnabled()) {
			try {
				logger.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(candidate));
			} catch (IOException e) {
				// ignore this
			}
		}
	}
}


//
//	@On
//	public void hangup(@Data String toUser) {
//		Socket toUserSocket = usernameToSocketMap.get(toUser);
//		if (toUserSocket != null) {
//			toUserSocket.send("hangup");
//		}
//	}
