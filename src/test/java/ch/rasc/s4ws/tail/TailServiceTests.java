package ch.rasc.s4ws.tail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;

class TailServiceTests {

	@TempDir
	Path directory;

	@Test
	void tailsCombinedLogsAndParsesCurrentBrowsers() throws Exception {
		Path log = Files.createFile(this.directory.resolve("access.log"));
		CityResponse city = new CityResponse(
			new City(List.of("en"), null, null, Map.of("en", "Zurich")), null,
			new Country(List.of("en"), null, null, false, "CH", Map.of("en", "Switzerland")),
			new Location(null, null, 47.37, 8.55, null, "Europe/Zurich"),
			null, null, null, null, List.of(), null);
		SimpMessageSendingOperations messaging = mock(SimpMessageSendingOperations.class);
		TailService service = new TailService("", log.toString(), messaging) {
			@Override
			public CityResponse lookupCity(String ip) {
				return city;
			}
		};
		try {
			Files.writeString(log, """
				203.0.113.1 - - [06/Sep/2026:12:00:00 +0000] "GET /index.html HTTP/1.1" 200 123 "-" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"
				""", StandardOpenOption.APPEND);
			ArgumentCaptor<Access> access = ArgumentCaptor.forClass(Access.class);
			verify(messaging, timeout(10000)).convertAndSend(eq("/topic/tail"), access.capture());
			assertThat(access.getValue().getMessage()).contains("GET /index.html", "Chrome", "Windows");
			assertThat(access.getValue().getCity()).isEqualTo("Zurich");
			assertThat(access.getValue().getLl()).containsExactly(47.37, 8.55);
		}
		finally {
			service.preDestroy();
		}
	}
}
