import ch.rasc.embeddedtc.EmbeddedTomcat;

public class Spring4WSStartTomcat {
	public static void main(String[] args) throws Exception {
		EmbeddedTomcat.create().addContextInitializationParameter("org.apache.tomcat.websocket.textBufferSize", "1000000").startAndWait();
	}
}
