package dev.smartconsumer.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 모든 요청의 Character Encoding을 UTF-8로 설정하는 필터.
 * web.xml에서 /* 패턴으로 매핑됨.
 */
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { }
}
