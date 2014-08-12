package ch.rasc.s4ws.tail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentFamily;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

@Service
public class TailService {

	private final Pattern accessLogPattern = Pattern.compile(getAccessLogRegex(),
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private final UserAgentStringParser parser = UADetectorServiceFactory
			.getResourceModuleParser();

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@Autowired
	private Environment environment;

	public ExecutorService executor;

	private LookupService lookupService;

	private List<Tailer> tailers;

	@PostConstruct
	public void postConstruct() throws IOException {
		String property = environment.getRequiredProperty("TAIL_GEOCITY_DAT");
		lookupService = new LookupService(property, LookupService.GEOIP_INDEX_CACHE);
		tailers = new ArrayList<>();

		String logFiles = environment.getRequiredProperty("TAIL_ACCESS_LOG");
		for (String logFile : Splitter.on(",").trimResults().split(logFiles)) {
			Path p = Paths.get(logFile);
			tailers.add(new Tailer(p.toFile(), new ListenerAdapter()));
		}

		executor = Executors.newFixedThreadPool(tailers.size());
		for (Tailer tailer : tailers) {
			executor.execute(tailer);
		}

	}

	@PreDestroy
	public void preDestroy() {
		if (tailers != null) {
			for (Tailer tailer : tailers) {
				tailer.stop();
			}
		}

		if (executor != null) {
			executor.shutdown();
		}
	}

	private class ListenerAdapter extends TailerListenerAdapter {
		@Override
		public void handle(String line) {
			Matcher matcher = accessLogPattern.matcher(line);

			if (!matcher.matches()) {
				// System.out.println(line);
				return;
			}

			String ip = matcher.group(1);
			if (!"-".equals(ip) && !"127.0.0.1".equals(ip)) {
				Location l = lookupService.getLocation(ip);
				if (l != null) {
					Access access = new Access();
					access.setIp(ip);
					access.setDate(DateTime.now().getMillis());
					access.setCity(l.city);
					access.setCountry(l.countryName);

					String userAgent = matcher.group(9);
					ReadableUserAgent ua = parser.parse(userAgent);
					if (ua != null && ua.getFamily() != UserAgentFamily.UNKNOWN) {
						String uaString = ua.getName() + " "
								+ ua.getVersionNumber().toVersionString();
						uaString += "; " + ua.getOperatingSystem().getName();
						uaString += "; " + ua.getFamily();
						uaString += "; " + ua.getTypeName();
						uaString += "; " + ua.getProducer();

						access.setMessage(matcher.group(4) + "; " + uaString);
					}
					else {
						access.setMessage(null);
					}
					access.setLl(new float[] { l.latitude, l.longitude });

					messagingTemplate.convertAndSend("/queue/geoip", access);
				}
			}
		}
	}

	private static String getAccessLogRegex() {
		String regex1 = "^([\\d.-]+)"; // Client IP
		String regex2 = " (\\S+)"; // -
		String regex3 = " (\\S+)"; // -
		String regex4 = " \\[([\\w:/]+\\s[+\\-]\\d{4})\\]"; // Date
		String regex5 = " \"(.*?)\""; // request method and url
		String regex6 = " (\\d{3})"; // HTTP code
		String regex7 = " (\\d+|.+?)"; // Number of bytes
		String regex8 = " \"([^\"]+|.+?)\""; // Referer
		String regex9 = " \"([^\"]+|.+?)\""; // Agent

		return regex1 + regex2 + regex3 + regex4 + regex5 + regex6 + regex7 + regex8
				+ regex9;
	}

}