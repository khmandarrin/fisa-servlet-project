<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="ko">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Smart Consumer - 회원가입</title>
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
                background: radial-gradient(circle, #f093fb, transparent);
                top: -100px;
                left: -100px;
                animation: float1 8s ease-in-out infinite;
            }

            body::after {
                width: 350px;
                height: 350px;
                background: radial-gradient(circle, #667eea, transparent);
                bottom: -80px;
                right: -80px;
                animation: float2 10s ease-in-out infinite;
            }

            @keyframes float1 {

                0%,
                100% {
                    transform: translate(0, 0);
                }

                50% {
                    transform: translate(30px, 30px);
                }
            }

            @keyframes float2 {

                0%,
                100% {
                    transform: translate(0, 0);
                }

                50% {
                    transform: translate(-20px, -20px);
                }
            }

            .signup-container {
                position: relative;
                z-index: 1;
                width: 440px;
                padding: 44px 40px;
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
                margin-bottom: 28px;
            }

            .logo-icon {
                font-size: 44px;
                margin-bottom: 6px;
            }

            .logo h1 {
                font-size: 22px;
                font-weight: 700;
                background: linear-gradient(135deg, #f093fb, #667eea);
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
                border-color: #f093fb;
                box-shadow: 0 0 0 3px rgba(240, 147, 251, 0.15);
                background: rgba(255, 255, 255, 0.1);
            }

            .info-box {
                background: rgba(102, 126, 234, 0.1);
                border: 1px solid rgba(102, 126, 234, 0.2);
                border-radius: 12px;
                padding: 14px 16px;
                margin-bottom: 20px;
                font-size: 13px;
                color: rgba(255, 255, 255, 0.6);
                line-height: 1.5;
            }

            .info-box .icon {
                margin-right: 6px;
            }

            .btn-signup {
                width: 100%;
                padding: 14px;
                background: linear-gradient(135deg, #f093fb, #667eea);
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

            .btn-signup:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(240, 147, 251, 0.4);
            }

            .btn-signup:active {
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

            .login-link {
                text-align: center;
                margin-top: 22px;
                font-size: 14px;
                color: rgba(255, 255, 255, 0.5);
            }

            .login-link a {
                color: #c4b5fd;
                text-decoration: none;
                font-weight: 500;
                transition: color 0.2s;
            }

            .login-link a:hover {
                color: #e9d5ff;
            }
        </style>
    </head>

    <body>
        <div class="signup-container">
            <div class="logo">
                <div class="logo-icon">🚀</div>
                <h1>회원가입</h1>
                <p>Smart Consumer에 가입하고 소비 패턴을 분석해보세요</p>
            </div>

            <% if (request.getAttribute("error") !=null) { %>
                <div class="error-msg">
                    <%= request.getAttribute("error") %>
                </div>
                <% } %>

                    <div class="info-box">
                        <span class="icon">💡</span>
                        카드 데이터에 등록된 고객번호로 가입할 수 있습니다.
                    </div>

                    <form action="${pageContext.request.contextPath}/signup" method="post" autocomplete="off">
                        <div class="form-group">
                            <label for="seq">고객번호</label>
                            <input type="text" id="seq" name="seq" placeholder="고객번호를 입력하세요 (예: C001)"
                                value="<%= request.getAttribute(" seq") !=null ? request.getAttribute("seq") : "" %>"
                            required>
                        </div>

                        <div class="form-group">
                            <label for="password">비밀번호</label>
                            <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
                        </div>

                        <div class="form-group">
                            <label for="sexCd">성별</label>
                            <select id="sexCd" name="sexCd" required
                                style="width:100%;padding:14px 16px;background:rgba(255,255,255,0.08);border:1px solid rgba(255,255,255,0.12);border-radius:12px;color:#fff;font-size:15px;font-family:'Inter',sans-serif;outline:none;appearance:none;-webkit-appearance:none;cursor:pointer;">
                                <option value="" disabled <%=request.getAttribute("sexCd")==null ? "selected" : "" %>
                                    >성별을 선택하세요</option>
                                <option value="1" <%="1" .equals(request.getAttribute("sexCd")) ? "selected" : "" %>>남성
                                </option>
                                <option value="2" <%="2" .equals(request.getAttribute("sexCd")) ? "selected" : "" %>>여성
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="age">연령대</label>
                            <select id="age" name="age" required
                                style="width:100%;padding:14px 16px;background:rgba(255,255,255,0.08);border:1px solid rgba(255,255,255,0.12);border-radius:12px;color:#fff;font-size:15px;font-family:'Inter',sans-serif;outline:none;appearance:none;-webkit-appearance:none;cursor:pointer;">
                                <option value="" disabled <%=request.getAttribute("age")==null ? "selected" : "" %>>연령대를
                                    선택하세요</option>
                                <option value="20" <%="20" .equals(request.getAttribute("age")) ? "selected" : "" %>>20대
                                </option>
                                <option value="30" <%="30" .equals(request.getAttribute("age")) ? "selected" : "" %>>30대
                                </option>
                                <option value="40" <%="40" .equals(request.getAttribute("age")) ? "selected" : "" %>>40대
                                </option>
                                <option value="50" <%="50" .equals(request.getAttribute("age")) ? "selected" : "" %>>50대
                                </option>
                            </select>
                        </div>

                        <button type="submit" class="btn-signup">가입하기</button>
                    </form>

                    <div class="login-link">
                        이미 계정이 있으신가요? <a href="${pageContext.request.contextPath}/login">로그인</a>
                    </div>
        </div>
    </body>

    </html>