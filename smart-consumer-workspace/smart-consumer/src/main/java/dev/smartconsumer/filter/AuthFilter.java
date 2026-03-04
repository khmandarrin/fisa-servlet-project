package dev.smartconsumer.filter;

import dev.smartconsumer.common.Const;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = {"/dashboard", "/api/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info(">>> AuthFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        HttpSession session = httpReq.getSession(false);

        if (session == null || session.getAttribute(Const.SESSION_USER) == null) {
            log.warn("AuthFilter: Unauthorized access to {} → redirecting to /login", httpReq.getRequestURI());

            // API 요청이면 JSON 에러 반환
            if (httpReq.getRequestURI().contains("/api/")) {
                httpResp.setContentType("application/json");
                httpResp.setCharacterEncoding("UTF-8");
                httpResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResp.getWriter().write("{\"error\":\"Unauthorized. Please login.\"}");
                return;
            }

            // 일반 요청이면 로그인 페이지로 리다이렉트
            httpResp.sendRedirect(httpReq.getContextPath() + "/login");
            return;
        }

        // 인증된 사용자 → 브라우저 캐시 방지 후 다음 필터/서블릿으로 진행
        httpResp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResp.setHeader("Pragma", "no-cache");
        httpResp.setDateHeader("Expires", 0);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info(">>> AuthFilter destroyed.");
    }
}
