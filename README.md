# Spring WebSocket demos

Nine sample applications using Spring Boot 4, Spring Framework 7, WebSocket,
SockJS and STOMP: chat, multiplayer snake, an access-log map, Smoothie charts,
a live car map, a simulated stock portfolio, bandwidth monitoring, drawboard,
and tennis scores. The Twitter demo and its server/client dependencies have
been removed.

## Build and run

Requires **JDK 26**. The Maven wrapper downloads Maven 3.9.16 when needed.

```sh
./mvnw clean verify
java -jar target/spring4ws-demos.jar
```

On Windows use `./mvnw.cmd clean verify`. Open <http://localhost:8080>.
The application starts without credentials, a database, or external services.
Portfolio demo accounts are `fabrice / fab123` and `paulson / bond`; trades and
prices are simulated and held in memory. These public credentials are for
demonstration only.

To use a different port or servlet context:

```sh
java -jar target/spring4ws-demos.jar --server.port=8081 --server.servlet.context-path=/demos
```

## Browser dependencies

Browser assets are served locally from `src/main/resources/static/vendor` and
committed so Java-only builds need no Node installation or CDN access. Their
exact npm versions and integrity hashes are recorded in `package.json` and
`package-lock.json`. To regenerate them, use Node.js 24 or later:

```sh
npm ci --ignore-scripts
npm run build
./mvnw clean verify
```

Commit the manifest, lockfile and regenerated vendor directory together when
updating browser libraries. `scripts/vendor.mjs` copies the pinned distributions,
source maps and license notices. `vendor/versions.json` records the generated
versions. OpenStreetMap tiles still require an internet connection.

## Verification

`./mvnw verify` runs server integration tests against embedded Tomcat, including
public pages, portfolio authentication and CSRF protection. The browser tests
exercise real WebSocket messages, trades, reconnection and SockJS HTTP fallback:

```sh
npm ci --ignore-scripts
npx playwright install chromium
npm test
npm run test:context
```

Build the jar before running `npm test`. Playwright starts it on port 18080 and
stops it afterwards. `npm run test:context` repeats the browser suite with the
application deployed under `/demos` on port 18081. To test an already running deployment instead, set
`DEMO_BASE_URL` (including any context path). Browser tests stub external map
tiles; car coordinates and all other messages come from the real application.

## Optional configuration

| Environment variable | Purpose | Default |
| --- | --- | --- |
| `GEOIP2_CITYFILE` | Path to a GeoLite2/GeoIP2 City `.mmdb` database | Empty |
| `ACCESS_LOGS` | Comma-separated paths to Apache/nginx combined access logs | Empty |
| `BANDWIDTH_NETWORK_INTERFACE` | Linux interface under `/sys/class/net` | `eth0` |

Set both log-map properties to display geolocated log entries. Acquire the City
database from [MaxMind](https://dev.maxmind.com/geoip/geolite2-free-geolocation-data/).
With no logs configured, the map waits for data and no tailer threads are started.
Bandwidth counters come from Linux network statistics; other platforms use
simulated counters. The car map uses Leaflet/OpenStreetMap and needs no Google
Maps API key.

HTTP login and logout use CSRF tokens from `/csrf`. STOMP clients fetch a token
for each connection; only SockJS transport URLs are exempt from HTTP CSRF checks.
WebSocket origins are restricted to the application's own origin. Portfolio
messages require authentication, and clients may only publish to the demo's
explicitly allowed destinations. A simple in-memory broker handles `/topic` and
`/queue`; `/app` routes to server handlers.

## Upgrade baseline

Stable releases verified on 2026-09-06; milestone/beta releases are excluded.

| Component | Version |
| --- | --- |
| Spring Boot | 4.1.1 (manages Spring, Security, Tomcat and Jackson 3) |
| Java / Maven wrapper distribution | 26 / 3.9.16 |
| Commons IO / GeoIP2 / Yauaa | 2.22.0 / 5.2.0 / 8.2.0 |
| Error Prone / Maven compiler plugin | 2.50.0 / 3.16.0 |
| STOMP.js / SockJS client | 7.3.0 / 1.6.1 |
| Bootstrap / jQuery / Knockout | 5.3.8 / 4.0.0 / 3.5.3 |
| Leaflet / Raphael / Smoothie | 1.9.4 / 2.3.0 / 1.36.1 |
| Playwright | 1.63.0 |

Migration references: [Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html),
[Boot 4 migration guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide),
[Spring WebSocket security](https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html),
[STOMP.js client API](https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html),
[GeoIP2 Java API](https://maxmind.github.io/GeoIP2-java/),
and [Yauaa](https://yauaa.basjes.nl/using/index.html).
