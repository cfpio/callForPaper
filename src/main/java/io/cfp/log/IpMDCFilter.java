package io.cfp.log;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Filter which save the user ip into the MDC
 */
@Component
public class IpMDCFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			MDC.put(MDCKey.IP, getIp(request));
			chain.doFilter(request, response);

		} finally {
			MDC.remove(MDCKey.IP);
		}
	}

	private String getIp(ServletRequest request) {

		if (request instanceof HttpServletRequest) {
			String forwarded = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
			if (forwarded != null) {
				return forwarded;
			}
		}

		return request.getRemoteHost();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
