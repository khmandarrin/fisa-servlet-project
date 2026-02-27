<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="ko">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Smart Consumer - 로그인</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
            rel="stylesheet">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Inter', sans-serif;
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
                color: #e0e0e0;
                overflow: hidden;
            }

            body::before,
            body::after {
                content: '';
                position: fixed;
                border-radius: 50%;
                filter: blur(80px);
                opacity: 0.3;
                z-index: 0;
            }

            body::before {
                width: 400px;
                height: 400px;
                background: radial-gradient(circle, #667eea, transparent);
                top: -100px;
                right: -100px;
                animation: float1 8s ease-in-out infinite;
            }

            body::after {
                width: 350px;
                height: 350px;
                background: radial-gradient(circle, #764ba2, transparent);
                bottom: -80px;
                left: -80px;
                animation: float2 10s ease-in-out infinite;
            }

            @keyframes float1 {

                0%,
                100% {
                    transform: translate(0, 0);
                }

                50% {
                    transform: translate(-30px, 30px);
                }
            }

            @keyframes float2 {

                0%,
                100% {
                    transform: translate(0, 0);
                }

                50% {
                    transform: translate(20px, -20px);
                }
            }

            .login-container {
                position: relative;
                z-index: 1;
                width: 420px;
                padding: 48px 40px;
                background: rgba(255, 255, 255, 0.05);
                backdrop-filter: blur(20px);
                -webkit-backdrop-filter: blur(20px);
                border: 1px solid rgba(255, 255, 255, 0.1);
                border-radius: 24px;
                box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
                animation: slideUp 0.6s ease-out;
            }

            @keyframes slideUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }

                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .logo {
                text-align: center;
                margin-bottom: 32px;
            }

            .logo-icon {
                font-size: 48px;
                margin-bottom: 8px;
            }

            .logo h1 {
                font-size: 24px;
                font-weight: 700;
                background: linear-gradient(135deg, #667eea, #a78bfa);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
            }

            .logo p {
                font-size: 13px;
                color: rgba(255, 255, 255, 0.5);
                margin-top: 4px;
            }

            .form-group {
                margin-bottom: 20px;
            }

            .form-group label {
                display: block;
                font-size: 13px;
                font-weight: 500;
                color: rgba(255, 255, 255, 0.7);
                margin-bottom: 8px;
            }

            .form-group input {
                width: 100%;
                padding: 14px 16px;
                background: rgba(255, 255, 255, 0.08);
                border: 1px solid rgba(255, 255, 255, 0.12);
                border-radius: 12px;
                color: #fff;
                font-size: 15px;
                font-family: 'Inter', sans-serif;
                transition: all 0.3s ease;
                outline: none;
            }

            .form-group input::placeholder {
                color: rgba(255, 255, 255, 0.3);
            }

            .form-group input:focus {
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
                background: rgba(255, 255, 255, 0.1);
            }

            .btn-login {
                width: 100%;
                padding: 14px;
                background: linear-gradient(135deg, #667eea, #764ba2);
                border: none;
                border-radius: 12px;
                color: #fff;
                font-size: 16px;
                font-weight: 600;
                font-family: 'Inter', sans-serif;
                cursor: pointer;
                transition: all 0.3s ease;
                margin-top: 8px;
            }

            .btn-login:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
            }

            .btn-login:active {
                transform: translateY(0);
            }

            .error-msg {
                background: rgba(239, 68, 68, 0.15);
                border: 1px solid rgba(239, 68, 68, 0.3);
                border-radius: 10px;
                padding: 12px 16px;
                margin-bottom: 20px;
                font-size: 13px;
                color: #fca5a5;
                text-align: center;
            }

            .success-msg {
                background: rgba(34, 197, 94, 0.15);
                border: 1px solid rgba(34, 197, 94, 0.3);
                border-radius: 10px;
                padding: 12px 16px;
                margin-bottom: 20px;
                font-size: 13px;
                color: #86efac;
                text-align: center;
            }

            .signup-link {
                text-align: center;
                margin-top: 24px;
                font-size: 14px;
                color: rgba(255, 255, 255, 0.5);
            }

            .signup-link a {
                color: #a78bfa;
                text-decoration: none;
                font-weight: 500;
                transition: color 0.2s;
            }

            .signup-link a:hover {
                color: #c4b5fd;
            }
        </style>
    </head>

    <body>
        <div class="login-container">
            <div class="logo">
                <div class="logo-icon">📊</div>
                <h1>Smart Consumer</h1>
                <p>소비 성향 분석 리포트</p>
            </div>

            <% if (request.getAttribute("error") !=null) { %>
                <div class="error-msg">
                    <%= request.getAttribute("error") %>
                </div>
                <% } %>

                    <% if ("true".equals(request.getParameter("signupSuccess"))) { %>
                        <div class="success-msg">✅ 회원가입이 완료되었습니다. 로그인해 주세요!</div>
                        <% } %>

                            <form action="${pageContext.request.contextPath}/login" method="post" autocomplete="off">
                                <div class="form-group">
                                    <label for="seq">고객번호</label>
                                    <input type="text" id="seq" name="seq" placeholder="고객번호를 입력하세요"
                                        value="<%= request.getAttribute(" seq") !=null ? request.getAttribute("seq")
                                        : "" %>" required>
                                </div>

                                <div class="form-group">
                                    <label for="password">비밀번호</label>
                                    <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요"
                                        required>
                                </div>

                                <button type="submit" class="btn-login">로그인</button>
                            </form>

                            <div class="signup-link">
                                계정이 없으신가요? <a href="${pageContext.request.contextPath}/signup">회원가입</a>
                            </div>
        </div>
    </body>

    </html>