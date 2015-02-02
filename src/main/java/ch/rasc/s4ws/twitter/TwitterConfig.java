package ch.rasc.s4ws.twitter;

import java.util.Collections;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

@Configuration
public class TwitterConfig {

	@Bean
	public ITopic<Tweet> hazelcastTopic() {
		Config config = new Config();
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig()
				.setMembers(Collections.singletonList("127.0.0.1")).setEnabled(true);

		HazelcastInstance hc = Hazelcast.newHazelcastInstance(config);
		return hc.getTopic("tweets");
	}

	@PreDestroy
	public void destroy() {
		Hazelcast.shutdownAll();
	}

}
