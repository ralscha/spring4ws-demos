package ch.rasc.s4ws;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

	@LocalServerPort
	private int port;

	private HttpClient client;

	@BeforeEach
	void setUp() {
		this.client = HttpClient.newBuilder()
			.cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build();
	}

	@Test
	void startsWithoutExternalServicesAndServesEveryPublicDemo() throws Exception {
		for (String demo : new String[] { "", "chat/", "snake/", "tail/", "smoothie/",
				"map/", "bandwidth/", "drawboard/", "tennis/" }) {
			assertThat(get("/" + demo + "index.html").statusCode()).as(demo).isEqualTo(200);
		}
		assertThat(get("/twitter/index.html").statusCode()).isEqualTo(404);
		assertThat(get("/sockjs/info").body()).contains("\"websocket\":true");
	}

	@Test
	void protectsPortfolioAndRejectsLoginWithoutCsrf() throws Exception {
		HttpResponse<String> response = get("/portfolio/index.html");
		assertThat(response.statusCode()).isEqualTo(302);
		assertThat(response.headers().firstValue("location").orElseThrow())
			.endsWith("/portfolio/login.html");
		assertThat(post("/portfolio/login.html", "username=fabrice&password=fab123").statusCode())
			.isEqualTo(403);
	}

	@Test
	void authenticatesBothDemoUsersAndLogsOutWithCsrf() throws Exception {
		for (String credentials : new String[] { "username=fabrice&password=fab123", "username=paulson&password=bond" }) {
			HttpResponse<String> login = post("/portfolio/login.html", credentials + csrfParameter());
			assertThat(login.statusCode()).isEqualTo(302);
			assertThat(login.headers().firstValue("location").orElseThrow())
				.endsWith("/portfolio/index.html");
			assertThat(get("/portfolio/index.html").statusCode()).isEqualTo(200);
			assertThat(post("/portfolio/logout.html", csrfParameter()).statusCode()).isEqualTo(302);
			assertThat(get("/portfolio/index.html").statusCode()).isEqualTo(302);
		}
	}

	@Test
	void rejectsInvalidPassword() throws Exception {
		HttpResponse<String> login = post("/portfolio/login.html",
			"username=fabrice&password=wrong" + csrfParameter());
		assertThat(login.headers().firstValue("location").orElseThrow())
			.endsWith("/portfolio/login.html?error");
	}

	private String csrfParameter() throws Exception {
		JsonNode token = JsonMapper.builder().build().readTree(get("/csrf").body());
		return "&" + token.get("parameterName").asString() + "="
			+ URLEncoder.encode(token.get("token").asString(), StandardCharsets.UTF_8);
	}

	private HttpResponse<String> get(String path) throws Exception {
		return this.client.send(HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> post(String path, String body) throws Exception {
		return this.client.send(HttpRequest.newBuilder(uri(path))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
	}

	private URI uri(String path) {
		return URI.create("http://localhost:" + this.port + path);
	}
}
