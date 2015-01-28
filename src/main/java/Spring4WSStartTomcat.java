import ch.rasc.embeddedtc.EmbeddedTomcat;

public class Spring4WSStartTomcat {
	public static void main(String[] args) throws Exception {
		
		// -Dtwitter4j.debug=false
		// -Dtwitter4j.oauth.consumerKey=<consumerKey>
		// -Dtwitter4j.oauth.consumerSecret=<secretKey>
		// -Dtwitter4j.oauth.accessToken=<accessToken>
		// -Dtwitter4j.oauth.accessTokenSecret=<accessTokenSecret>
		// -DTAIL_GEOCITY_DAT=<path_to_GeoLiteCity.dat>   download it from here: http://dev.maxmind.com/geoip/legacy/geolite/
		// -DTAIL_ACCESS_LOG=<path_to_http_server_access_log>
		// -Dbandwidth.network.interface=<network_interface>
		
		EmbeddedTomcat.create().startAndWait();
	}
}
