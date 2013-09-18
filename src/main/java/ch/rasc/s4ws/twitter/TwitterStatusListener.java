package ch.rasc.s4ws.twitter;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Collections;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

import com.hazelcast.core.ITopic;
import com.twitter.hbc.twitter4j.handler.StatusStreamHandler;
import com.twitter.hbc.twitter4j.message.DisconnectMessage;

public class TwitterStatusListener implements StatusStreamHandler {

	private final static Pattern URL_PATTERN = Pattern
			.compile("(((http[s]?:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)");

	private final SimpMessageSendingOperations messagingTemplate;

	private final Queue<Tweet> lastTweets;

	private final ITopic<Tweet> hazelcastTopic;

	public TwitterStatusListener(SimpMessageSendingOperations messagingTemplate, ITopic<Tweet> hazelcastTopic,
			Queue<Tweet> lastTweets) {
		this.messagingTemplate = messagingTemplate;
		this.lastTweets = lastTweets;
		this.hazelcastTopic = hazelcastTopic;
	}

	@Override
	public void onStatus(Status status) {

		String text = status.getText();
		text = text.replace("<", "&lt;");

		Matcher matcher = URL_PATTERN.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String unshortenedURL = unshorten(matcher.group());
			if (unshortenedURL != null) {
				matcher.appendReplacement(sb, "<a target=\"_blank\" href=\"" + unshortenedURL + "\">" + unshortenedURL
						+ "</a>");
			} else {
				matcher.appendReplacement(sb, "$0");
			}
		}
		matcher.appendTail(sb);

		Tweet tweet = new Tweet();
		tweet.setCreatedAt(status.getCreatedAt().getTime());
		tweet.setFromUser(status.getUser().getName());
		tweet.setId(status.getId());
		tweet.setProfileImageUrl(status.getUser().getProfileImageURL().toString());
		tweet.setText(sb.toString());

		lastTweets.offer(tweet);

		messagingTemplate.convertAndSend("/queue/tweets", Collections.singletonList(tweet));

		hazelcastTopic.publish(tweet);
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// nothing here
	}

	@Override
	public void onTrackLimitationNotice(int limit) {
		// nothing here
	}

	@Override
	public void onScrubGeo(long user, long upToStatus) {
		// nothing here
	}

	@Override
	public void onException(Exception e) {
		// nothing here
	}

	@Override
	public void onDisconnectMessage(DisconnectMessage message) {
		// nothing here
	}

	@Override
	public void onUnknownMessageType(String s) {
		// nothing here
	}

	private String unshorten(String url) {
		try {
			HttpHead head = new HttpHead(url);
			HttpParams params = new BasicHttpParams();
			HttpClientParams.setRedirecting(params, false);
			head.setParams(params);
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			HttpResponse response = defaultHttpClient.execute(head);

			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_MOVED_PERMANENTLY || status == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				if (locationHeader != null) {
					String value = locationHeader.getValue();
					if (!value.startsWith("http") && value.startsWith("/")) {
						value = "http:/" + value;
					}
					return unshorten(value);
				}
			} else if (status >= 400 && status != HttpStatus.SC_METHOD_NOT_ALLOWED && status != HttpStatus.SC_FORBIDDEN) {
				return null;
			}

		} catch (IllegalStateException | IOException e) {
			if (!(e instanceof SSLException || e instanceof ConnectException)) {
				// ignore this
			}
		}
		return url;
	}
}
