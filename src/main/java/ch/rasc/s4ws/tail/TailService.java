package ch.rasc.s4ws.tail;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;

@Service
public class TailService {

	private final Pattern accessLogPattern = Pattern.compile(getAccessLogRegex(),
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private final SimpMessageSendingOperations messagingTemplate;

	public ExecutorService executor;

	private final List<Tailer> tailers;

	private DatabaseReader reader = null;

	@Autowired
	public TailService(@Value("${geoip2.cityfile}") String cityFile,
			@Value("${access.logs}") String accessLogs,
			SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;

		String databaseFile = cityFile;
		if (databaseFile != null) {
			Path database = Paths.get(databaseFile);
			if (Files.exists(database)) {
				try {
					this.reader = new DatabaseReader.Builder(database.toFile()).build();
				}
				catch (IOException e) {
					LoggerFactory.getLogger(getClass()).error("GeoIPCityService init", e);
				}
			}
		}

		this.tailers = new ArrayList<>();

		for (String logFile : accessLogs.split(",")) {
			Path p = Paths.get(logFile.trim());
			this.tailers.add(new Tailer(p.toFile(), new ListenerAdapter()));
		}

		this.executor = Executors.newFixedThreadPool(this.tailers.size());
		for (Tailer tailer : this.tailers) {
			this.executor.execute(tailer);
		}
	}

	@PreDestroy
	public void preDestroy() {
		if (this.tailers != null) {
			for (Tailer tailer : this.tailers) {
				tailer.stop();
			}
		}

		if (this.executor != null) {
			this.executor.shutdown();
		}
	}

	private class ListenerAdapter extends TailerListenerAdapter {
		@Override
		public void handle(String line) {
			Matcher matcher = TailService.this.accessLogPattern.matcher(line);

			if (!matcher.matches()) {
				// System.out.println(line);
				return;
			}

			String ip = matcher.group(1);
			if (!"-".equals(ip) && !"127.0.0.1".equals(ip)) {
				CityResponse cr = lookupCity(ip);
				if (cr != null) {
					Access access = new Access();
					access.setIp(ip);
					access.setDate(Instant.now().toEpochMilli());
					access.setCity(cr.getCity().getName());
					access.setCountry(cr.getCountry().getName());

					String userAgent = matcher.group(9);

					UserAgent ua = UserAgent.parseUserAgentString(userAgent);

					if (ua != null && ua.getBrowser() != Browser.UNKNOWN) {
						String uaString = ua.getBrowser().getName() + "; "
								+ ua.getOperatingSystem().toString();

						access.setMessage(matcher.group(4) + "; " + uaString);
					}
					else {
						access.setMessage(null);
					}
					access.setLl(new Double[] { cr.getLocation().getLatitude(),
							cr.getLocation().getLongitude() });

					TailService.this.messagingTemplate.convertAndSend("/topic/tail",
							access);
				}
			}
		}
	}

	public CityResponse lookupCity(String ip) {
		if (this.reader != null) {
			CityResponse response;
			try {
				try {
					response = this.reader.city(InetAddress.getByName(ip));
					return response;
				}
				catch (AddressNotFoundException e) {
					return null;
				}
			}
			catch (IOException | GeoIp2Exception e) {
				LoggerFactory.getLogger(getClass()).error("lookupCity", e);
			}
		}

		return null;
	}

	private static String getAccessLogRegex() {
		String regex1 = "^([\\d.:a-z]+)"; // Client IP
		String regex2 = " (\\S+)"; // -
		String regex3 = " (\\S+)"; // -
		String regex4 = " \\[([\\w:/]+\\s[+\\-]\\d{4})\\]"; // Date
		String regex5 = " \"(.*?)\""; // request method and url
		String regex6 = " (\\d{3})"; // HTTP code
		String regex7 = " (\\d+|.+?)"; // Number of bytes
		String regex8 = " \"([^\"]+|.+?)\""; // Referer
		String regex9 = " \"([^\"]*|.*?)\""; // Agent

		return regex1 + regex2 + regex3 + regex4 + regex5 + regex6 + regex7 + regex8
				+ regex9;
	}

}