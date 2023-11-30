package ru.alexnv.apps.wallet.in.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import ru.alexnv.apps.wallet.in.servlets.ServletsUtility;

/**
 * Фильтр входящих запросов. Пропускает только входящий JSON.
 */
public class JsonRequestFilter extends HttpFilter implements Filter {
       
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2379993274829435135L;
    
	/** Вспомогательный класс для HTTP сервлетов. */
	private ServletsUtility servletsUtil;

	/**
	 * Instantiates a new json request filter.
	 *
	 * @see HttpFilter#HttpFilter()
	 */
    public JsonRequestFilter() {
        super();
        servletsUtil = new ServletsUtility();
    }

	/**
	 * Фильтрация всех входящих запросов запросов, кроме GET.
	 * Проверяется поле запроса contentType, если не содержит application/json, то доступ к ресурсу запрещён.
	 * Иначе - доступ к ресурсу разрешён.
	 *
	 * @param request the request
	 * @param response the response
	 * @param chain the chain
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		if (!"GET".equals(httpRequest.getMethod())) {
			String contentType = httpRequest.getContentType();
			if (contentType == null || !contentType.equals(MediaType.APPLICATION_JSON)) {
				servletsUtil.respondWithError(httpResponse, HttpServletResponse.SC_BAD_REQUEST, "Тип запроса не JSON.");
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

}
