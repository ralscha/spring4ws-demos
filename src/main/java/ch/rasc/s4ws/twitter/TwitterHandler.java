package ch.rasc.s4ws.twitter;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MinMaxPriorityQueue;
import com.hazelcast.core.ITopic;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;

@Controller
public class TwitterHandler {

	@Autowired
	private Environment environment;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@Autowired
	private ITopic<Tweet> hazelcastTopic;

	private final Queue<Tweet> lastTweets = MinMaxPriorityQueue.maximumSize(10).create();

	private BasicClient client;

	private ExecutorService executorService;

	private Twitter4jStatusClient t4jClient;

	@SubscribeMapping("/queue/tweets")
	public Queue<Tweet> subscribe() {
		return this.lastTweets;
	}

	@PostConstruct
	public void init() {
		BlockingQueue<String> queue = new LinkedBlockingQueue<>(100);
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

		endpoint.trackTerms(ImmutableList.of("ExtJS", "#extjs", "#SenchaTouch", "Sencha",
				"#java", "java7", "java8", "#websocket", "#SpringFramework", "html5",
				"javascript"));
		endpoint.languages(ImmutableList.of("en", "de"));

		String consumerKey = this.environment.getProperty("twitter4j.oauth.consumerKey");
		String consumerSecret = this.environment
				.getProperty("twitter4j.oauth.consumerSecret");
		String accessToken = this.environment.getProperty("twitter4j.oauth.accessToken");
		String accessTokenSecret = this.environment
				.getProperty("twitter4j.oauth.accessTokenSecret");

		Authentication auth = new OAuth1(consumerKey, consumerSecret, accessToken,
				accessTokenSecret);

		this.client = new ClientBuilder().hosts(Constants.STREAM_HOST).endpoint(endpoint)
				.authentication(auth).processor(new StringDelimitedProcessor(queue))
				.build();

		this.executorService = Executors.newSingleThreadExecutor();

		TwitterStatusListener statusListener = new TwitterStatusListener(
				this.messagingTemplate, this.hazelcastTopic, this.lastTweets);
		this.t4jClient = new Twitter4jStatusClient(this.client, queue,
				ImmutableList.of(statusListener), this.executorService);

		this.t4jClient.connect();
		this.t4jClient.process();
	}

	@PreDestroy
	public void destroy() {
		this.t4jClient.stop();
		this.client.stop();
		this.executorService.shutdown();
	}

}
