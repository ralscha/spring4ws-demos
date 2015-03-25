package ch.rasc.s4ws;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeaderService {

	// return all request headers
	@RequestMapping("/requestHeaders")
	public Map<String, String> requestHeaders(HttpServletRequest request) {

		Map<String, String> result = new HashMap<>();
		Enumeration<String> e = request.getHeaderNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			result.put(name, request.getHeader(name));
		}

		return result;
	}

}
