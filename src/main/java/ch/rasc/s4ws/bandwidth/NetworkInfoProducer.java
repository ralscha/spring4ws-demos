package ch.rasc.s4ws.bandwidth;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

@Service
public class NetworkInfoProducer {

	private final Random rand = new Random();

	private long tx = 0;

	private long rx = 0;

	private final boolean isLinux;

	@Value("${bandwidth.network.interface}")
	private String networkInterface;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	public NetworkInfoProducer() {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory
				.getOperatingSystemMXBean();
		String os = operatingSystemMXBean.getName().toLowerCase();
		this.isLinux = os.indexOf("linux") != -1;
	}

	@Scheduled(initialDelay = 2000, fixedRate = 2000)
	public void sendNetworkInfo() {

		if (this.isLinux) {
			try {
				ProcessBuilder pb = new ProcessBuilder("cat", "/sys/class/net/"
						+ this.networkInterface + "/statistics/rx_bytes");
				Process p = pb.start();
				p.waitFor();
				this.rx = Long.parseLong(StringUtils.trimAllWhitespace(StreamUtils
						.copyToString(p.getInputStream(), StandardCharsets.UTF_8)));

				pb = new ProcessBuilder("cat", "/sys/class/net/" + this.networkInterface
						+ "/statistics/tx_bytes");
				p = pb.start();
				p.waitFor();
				this.tx = Long.parseLong(StringUtils.trimAllWhitespace(StreamUtils
						.copyToString(p.getInputStream(), StandardCharsets.UTF_8)));
			}
			catch (NumberFormatException | IOException | InterruptedException e) {
				this.rx = 0;
				this.tx = 0;
			}
		}
		else {
			this.rx += this.rand.nextInt(512 * 1024);
			this.tx += this.rand.nextInt(512 * 1024);
		}

		Map<String, Long> info = new HashMap<>();
		info.put("rec", this.rx);
		info.put("snd", this.tx);
		this.messagingTemplate.convertAndSend("/topic/networkinfo", info);
	}

}
