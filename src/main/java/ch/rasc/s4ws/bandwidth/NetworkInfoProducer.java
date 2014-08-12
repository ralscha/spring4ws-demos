package ch.rasc.s4ws.bandwidth;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableMap;

@Service
public class NetworkInfoProducer {

	private final Random rand = new Random();

	private long tx = 0;

	private long rx = 0;

	private final boolean isLinux;

	@Value("#{environment['bandwidth.network.interface']}")
	private String networkInterface;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	public NetworkInfoProducer() {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory
				.getOperatingSystemMXBean();
		String os = operatingSystemMXBean.getName().toLowerCase();
		isLinux = os.indexOf("linux") != -1;
	}

	@Scheduled(initialDelay = 2000, fixedRate = 1000)
	public void sendNetworkInfo() {

		if (isLinux) {
			try {
				ProcessBuilder pb = new ProcessBuilder("cat", "/sys/class/net/"
						+ networkInterface + "/statistics/rx_bytes");
				Process p = pb.start();
				p.waitFor();
				rx = Long.parseLong(StringUtils.trimAllWhitespace(IOUtils.toString(p
						.getInputStream())));

				pb = new ProcessBuilder("cat", "/sys/class/net/" + networkInterface
						+ "/statistics/tx_bytes");
				p = pb.start();
				p.waitFor();
				tx = Long.parseLong(StringUtils.trimAllWhitespace(IOUtils.toString(p
						.getInputStream())));
			}
			catch (NumberFormatException | IOException | InterruptedException e) {
				rx = 0;
				tx = 0;
			}
		}
		else {
			rx += rand.nextInt(512 * 1024);
			tx += rand.nextInt(512 * 1024);
		}

		Map<String, Long> info = ImmutableMap.of("rec", rx, "snd", tx);
		messagingTemplate.convertAndSend("/queue/networkinfo", info);
	}

}
