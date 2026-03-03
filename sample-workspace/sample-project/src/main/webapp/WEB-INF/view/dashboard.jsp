<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="ko">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Smart Consumer - Dashboard</title>
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap"
                    rel="stylesheet">
                <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: 'Inter', sans-serif;
                        min-height: 100vh;
                        background: #0a0a1a;
                        color: #e0e0e0;
                    }

                    /* ── Navbar ── */
                    .navbar {
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        padding: 16px 40px;
                        background: rgba(255, 255, 255, 0.03);
                        border-bottom: 1px solid rgba(255, 255, 255, 0.06);
                        backdrop-filter: blur(12px);
                        position: sticky;
                        top: 0;
                        z-index: 100;
                    }

                    .nav-brand {
                        display: flex;
                        align-items: center;
                        gap: 10px;
                    }

                    .nav-brand span.icon {
                        font-size: 28px;
                    }

                    .nav-brand h1 {
                        font-size: 20px;
                        font-weight: 700;
                        background: linear-gradient(135deg, #667eea, #a78bfa);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                    }

                    .nav-user {
                        display: flex;
                        align-items: center;
                        gap: 16px;
                    }

                    .nav-user .user-info {
                        text-align: right;
                        font-size: 13px;
                    }

                    .nav-user .user-name {
                        font-weight: 600;
                        color: #fff;
                        font-size: 15px;
                    }

                    .nav-user .user-tag {
                        color: rgba(255, 255, 255, 0.4);
                    }

                    .btn-logout {
                        padding: 8px 20px;
                        background: rgba(239, 68, 68, 0.15);
                        border: 1px solid rgba(239, 68, 68, 0.3);
                        border-radius: 10px;
                        color: #fca5a5;
                        font-size: 13px;
                        font-weight: 500;
                        text-decoration: none;
                        transition: all 0.3s ease;
                    }

                    .btn-logout:hover {
                        background: rgba(239, 68, 68, 0.25);
                        transform: translateY(-1px);
                    }

                    /* ── Main Container ── */
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 32px 40px;
                    }

                    /* ── Hero Section ── */
                    .hero {
                        position: relative;
                        padding: 40px 44px;
                        background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(167, 139, 250, 0.1));
                        border: 1px solid rgba(102, 126, 234, 0.2);
                        border-radius: 24px;
                        margin-bottom: 32px;
                        overflow: hidden;
                    }

                    .hero::before {
                        content: '';
                        position: absolute;
                        width: 200px;
                        height: 200px;
                        background: radial-gradient(circle, rgba(102, 126, 234, 0.2), transparent);
                        top: -60px;
                        right: -40px;
                        border-radius: 50%;
                        filter: blur(40px);
                    }

                    .hero h2 {
                        font-size: 28px;
                        font-weight: 800;
                        margin-bottom: 8px;
                    }

                    .hero h2 .highlight {
                        background: linear-gradient(135deg, #667eea, #a78bfa);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        background-clip: text;
                    }

                    .hero p {
                        font-size: 15px;
                        color: rgba(255, 255, 255, 0.5);
                        line-height: 1.6;
                    }

                    /* ── Grid ── */
                    .grid {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 24px;
                    }

                    /* ── Card ── */
                    .card {
                        background: rgba(255, 255, 255, 0.04);
                        border: 1px solid rgba(255, 255, 255, 0.08);
                        border-radius: 20px;
                        padding: 28px;
                        backdrop-filter: blur(8px);
                        transition: all 0.3s ease;
                    }

                    .card:hover {
                        border-color: rgba(255, 255, 255, 0.15);
                        transform: translateY(-2px);
                        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
                    }

                    .card-header {
                        display: flex;
                        align-items: center;
                        gap: 10px;
                        margin-bottom: 20px;
                    }

                    .card-header .emoji {
                        font-size: 24px;
                    }

                    .card-header h3 {
                        font-size: 17px;
                        font-weight: 600;
                        color: #fff;
                    }

                    .card-header .badge {
                        margin-left: auto;
                        padding: 4px 12px;
                        border-radius: 20px;
                        font-size: 11px;
                        font-weight: 600;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }

                    .badge-personal {
                        background: rgba(102, 126, 234, 0.2);
                        color: #93a8f8;
                    }

                    .badge-peer {
                        background: rgba(240, 147, 251, 0.15);
                        color: #f0a0fb;
                    }

                    .chart-container {
                        position: relative;
                        width: 100%;
                        height: 280px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }

                    /* ── Stat list ── */
                    .stat-list {
                        list-style: none;
                        margin-top: 16px;
                    }

                    .stat-list li {
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        padding: 10px 14px;
                        border-radius: 10px;
                        margin-bottom: 6px;
                        background: rgba(255, 255, 255, 0.03);
                        transition: background 0.2s;
                    }

                    .stat-list li:hover {
                        background: rgba(255, 255, 255, 0.06);
                    }

                    .stat-category {
                        display: flex;
                        align-items: center;
                        gap: 10px;
                        font-size: 14px;
                        font-weight: 500;
                    }

                    .stat-dot {
                        width: 10px;
                        height: 10px;
                        border-radius: 50%;
                        flex-shrink: 0;
                    }

                    .stat-amount {
                        font-size: 14px;
                        font-weight: 600;
                        color: #a78bfa;
                    }

                    .stat-pct {
                        font-size: 12px;
                        color: rgba(255, 255, 255, 0.4);
                        margin-left: 8px;
                    }

                    /* ── Full Width Card ── */
                    .card-full {
                        grid-column: 1 / -1;
                    }

                    .comparison-chart-container {
                        position: relative;
                        width: 100%;
                        height: 320px;
                    }

                    /* ── No Data ── */
                    .no-data {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        height: 200px;
                        color: rgba(255, 255, 255, 0.3);
                        font-size: 14px;
                    }

                    .no-data .icon {
                        font-size: 48px;
                        margin-bottom: 12px;
                    }

                    /* ── Footer ── */
                    .footer {
                        text-align: center;
                        padding: 32px;
                        color: rgba(255, 255, 255, 0.2);
                        font-size: 12px;
                    }

                    /* ── Responsive ── */
                    @media (max-width: 768px) {
                        .grid {
                            grid-template-columns: 1fr;
                        }

                        .container {
                            padding: 20px;
                        }

                        .navbar {
                            padding: 14px 20px;
                        }

                        .hero {
                            padding: 28px;
                        }

                        .hero h2 {
                            font-size: 22px;
                        }
                    }
                </style>
            </head>

            <body>
                <!-- Navbar -->
                <nav class="navbar">
                    <div class="nav-brand">
                        <span class="icon">📊</span>
                        <h1>Smart Consumer</h1>
                    </div>
                    <div class="nav-user">
                        <div class="user-info">
                            <div class="user-name">고객 ${sessionScope.LOGIN_USER.seq}</div>
                            <div class="user-tag">${sessionScope.LOGIN_USER.age}대 · ${sessionScope.LOGIN_USER.sexCd ==
                                '1' ? '남성' : '여성'}</div>
                        </div>
                        <a href="${pageContext.request.contextPath}/logout" class="btn-logout">로그아웃</a>
                    </div>
                </nav>

                <div class="container">
                    <!-- Hero -->
                    <div class="hero">
                        <h2>
                            👋 <span class="highlight">고객 ${sessionScope.LOGIN_USER.seq}</span>님의 소비 리포트
                        </h2>
                        <p>
                            <strong>${sessionScope.LOGIN_USER.age}대 ${sessionScope.LOGIN_USER.sexCd == '1' ? '남성' :
                                '여성'}</strong> 고객님,
                            아래에서 나의 소비 패턴과 또래의 소비 트렌드를 비교해보세요.
                        </p>
                    </div>

                    <div class="grid">
                        <!-- ─── Card 1: 나의 소비 Top 3 (Doughnut Chart) ─── -->
                        <div class="card">
                            <div class="card-header">
                                <span class="emoji">🏆</span>
                                <h3>나의 소비 TOP 3</h3>
                                <span class="badge badge-personal">Personal</span>
                            </div>
                            <c:choose>
                                <c:when test="${not empty myStats}">
                                    <div class="chart-container">
                                        <canvas id="myTopChart"></canvas>
                                    </div>
                                    <ul class="stat-list" id="myStatList">
                                        <c:forEach var="stat" items="${myStats}" varStatus="loop">
                                            <li>
                                                <span class="stat-category">
                                                    <span class="stat-dot" id="dot-my-${loop.index}"></span>
                                                    ${stat.categoryName}
                                                </span>
                                                <span>
                                                    <span class="stat-amount">
                                                        <fmt:formatNumber value="${stat.totalAmount}" type="number" />원
                                                    </span>
                                                    <c:if test="${stat.percentage > 0}">
                                                        <span class="stat-pct">${stat.percentage}%</span>
                                                    </c:if>
                                                </span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-data">
                                        <span class="icon">📭</span>
                                        <p>아직 소비 데이터가 없습니다</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- ─── Card 2: 또래 소비 트렌드 (Doughnut Chart) ─── -->
                        <div class="card">
                            <div class="card-header">
                                <span class="emoji">👥</span>
                                <h3>${sessionScope.LOGIN_USER.age}대 ${sessionScope.LOGIN_USER.sexCd == '1' ? '남성' :
                                    '여성'} 소비 트렌드</h3>
                                <span class="badge badge-peer">Peers</span>
                            </div>
                            <c:choose>
                                <c:when test="${not empty peerStats}">
                                    <div class="chart-container">
                                        <canvas id="peerChart"></canvas>
                                    </div>
                                    <ul class="stat-list" id="peerStatList">
                                        <c:forEach var="stat" items="${peerStats}" varStatus="loop">
                                            <li>
                                                <span class="stat-category">
                                                    <span class="stat-dot" id="dot-peer-${loop.index}"></span>
                                                    ${stat.categoryName}
                                                </span>
                                                <span>
                                                    <span class="stat-amount">
                                                        <fmt:formatNumber value="${stat.totalAmount}" type="number" />원
                                                    </span>
                                                    <c:if test="${stat.percentage > 0}">
                                                        <span class="stat-pct">${stat.percentage}%</span>
                                                    </c:if>
                                                </span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-data">
                                        <span class="icon">📭</span>
                                        <p>또래 소비 데이터가 없습니다</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- ─── Card 3: 나 vs 또래 비교 (Bar Chart) - Full Width ─── -->
                        <div class="card card-full">
                            <div class="card-header">
                                <span class="emoji">⚡</span>
                                <h3>나 vs ${sessionScope.LOGIN_USER.age}대 ${sessionScope.LOGIN_USER.sexCd == '1' ? '남성' :
                                    '여성'} 소비 비교</h3>
                            </div>
                            <c:choose>
                                <c:when test="${not empty myStats || not empty peerStats}">
                                    <div class="comparison-chart-container">
                                        <canvas id="comparisonChart"></canvas>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="no-data">
                                        <span class="icon">📊</span>
                                        <p>비교할 데이터가 없습니다</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="footer">
                        © 2026 Smart Consumer · 연령/성별 기반 소비 성향 분석 서비스
                    </div>
                </div>

                <script>
                    // ── Color Palette ──
                    const COLORS = [
                        '#667eea', '#a78bfa', '#f093fb',
                        '#4ade80', '#fbbf24', '#fb923c',
                        '#38bdf8', '#f472b6', '#34d399'
                    ];
                    const COLORS_50 = [
                        'rgba(102,126,234,0.5)', 'rgba(167,139,250,0.5)', 'rgba(240,147,251,0.5)',
                        'rgba(74,222,128,0.5)', 'rgba(251,191,36,0.5)', 'rgba(251,146,60,0.5)',
                        'rgba(56,189,248,0.5)', 'rgba(244,114,182,0.5)', 'rgba(52,211,153,0.5)'
                    ];

                    // ── Chart Default Config ──
                    Chart.defaults.color = 'rgba(255,255,255,0.6)';
                    Chart.defaults.font.family = "'Inter', sans-serif";

                    // ── Prepare Data from JSP (Server-Side Rendering) ──
                    const myLabels = [];
                    const myData = [];
                    <c:forEach var="stat" items="${myStats}" varStatus="loop">
                        myLabels.push('${stat.categoryName}');
                        myData.push(${stat.totalAmount});
                    </c:forEach>

                    const peerLabels = [];
                    const peerData = [];
                    <c:forEach var="stat" items="${peerStats}" varStatus="loop">
                        peerLabels.push('${stat.categoryName}');
                        peerData.push(${stat.totalAmount});
                    </c:forEach>

                    // ── 1. My Top 3 Doughnut Chart ──
                    if (myLabels.length > 0) {
                        const myCtx = document.getElementById('myTopChart').getContext('2d');
                        new Chart(myCtx, {
                            type: 'doughnut',
                            data: {
                                labels: myLabels,
                                datasets: [{
                                    data: myData,
                                    backgroundColor: COLORS.slice(0, myLabels.length),
                                    borderColor: 'rgba(10,10,26,0.8)',
                                    borderWidth: 3,
                                    hoverBorderWidth: 0,
                                    hoverOffset: 8
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                cutout: '65%',
                                plugins: {
                                    legend: { display: false },
                                    tooltip: {
                                        backgroundColor: 'rgba(20,20,40,0.95)',
                                        titleColor: '#fff',
                                        bodyColor: 'rgba(255,255,255,0.8)',
                                        borderColor: 'rgba(255,255,255,0.1)',
                                        borderWidth: 1,
                                        cornerRadius: 10,
                                        padding: 12,
                                        callbacks: {
                                            label: function (ctx) {
                                                const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
                                                const pct = ((ctx.parsed / total) * 100).toFixed(1);
                                                return ctx.label + ': ' + ctx.parsed.toLocaleString() + '원 (' + pct + '%)';
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        // Set dot colors
                        myLabels.forEach((_, i) => {
                            const dot = document.getElementById('dot-my-' + i);
                            if (dot) dot.style.backgroundColor = COLORS[i];
                        });
                    }

                    // ── 2. Peer Stats Doughnut Chart ──
                    if (peerLabels.length > 0) {
                        const peerCtx = document.getElementById('peerChart').getContext('2d');
                        new Chart(peerCtx, {
                            type: 'doughnut',
                            data: {
                                labels: peerLabels,
                                datasets: [{
                                    data: peerData,
                                    backgroundColor: COLORS.slice(0, peerLabels.length),
                                    borderColor: 'rgba(10,10,26,0.8)',
                                    borderWidth: 3,
                                    hoverBorderWidth: 0,
                                    hoverOffset: 8
                                }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                cutout: '65%',
                                plugins: {
                                    legend: { display: false },
                                    tooltip: {
                                        backgroundColor: 'rgba(20,20,40,0.95)',
                                        titleColor: '#fff',
                                        bodyColor: 'rgba(255,255,255,0.8)',
                                        borderColor: 'rgba(255,255,255,0.1)',
                                        borderWidth: 1,
                                        cornerRadius: 10,
                                        padding: 12,
                                        callbacks: {
                                            label: function (ctx) {
                                                const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
                                                const pct = ((ctx.parsed / total) * 100).toFixed(1);
                                                return ctx.label + ': ' + ctx.parsed.toLocaleString() + '원 (' + pct + '%)';
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        // Set dot colors
                        peerLabels.forEach((_, i) => {
                            const dot = document.getElementById('dot-peer-' + i);
                            if (dot) dot.style.backgroundColor = COLORS[i];
                        });
                    }

                    // ── 3. Comparison Bar Chart (My Top vs Peer) ──
                    if (myLabels.length > 0 || peerLabels.length > 0) {
                        // Merge all unique categories
                        const allCategories = [...new Set([...myLabels, ...peerLabels])];

                        const myMap = {};
                        myLabels.forEach((l, i) => myMap[l] = myData[i]);
                        const peerMap = {};
                        peerLabels.forEach((l, i) => peerMap[l] = peerData[i]);

                        const myBarData = allCategories.map(c => myMap[c] || 0);
                        const peerBarData = allCategories.map(c => peerMap[c] || 0);

                        const compCtx = document.getElementById('comparisonChart').getContext('2d');
                        new Chart(compCtx, {
                            type: 'bar',
                            data: {
                                labels: allCategories,
                                datasets: [
                                    {
                                        label: '나의 소비',
                                        data: myBarData,
                                        backgroundColor: 'rgba(102,126,234,0.7)',
                                        borderColor: '#667eea',
                                        borderWidth: 1,
                                        borderRadius: 6,
                                        borderSkipped: false
                                    },
                                    {
                                        label: '또래 평균',
                                        data: peerBarData,
                                        backgroundColor: 'rgba(240,147,251,0.5)',
                                        borderColor: '#f093fb',
                                        borderWidth: 1,
                                        borderRadius: 6,
                                        borderSkipped: false
                                    }
                                ]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                indexAxis: 'y',
                                plugins: {
                                    legend: {
                                        position: 'top',
                                        align: 'end',
                                        labels: {
                                            usePointStyle: true,
                                            pointStyle: 'circle',
                                            padding: 20,
                                            font: { size: 12 }
                                        }
                                    },
                                    tooltip: {
                                        backgroundColor: 'rgba(20,20,40,0.95)',
                                        titleColor: '#fff',
                                        bodyColor: 'rgba(255,255,255,0.8)',
                                        borderColor: 'rgba(255,255,255,0.1)',
                                        borderWidth: 1,
                                        cornerRadius: 10,
                                        padding: 12,
                                        callbacks: {
                                            label: function (ctx) {
                                                return ctx.dataset.label + ': ' + ctx.parsed.x.toLocaleString() + '원';
                                            }
                                        }
                                    }
                                },
                                scales: {
                                    x: {
                                        grid: {
                                            color: 'rgba(255,255,255,0.05)',
                                            drawBorder: false
                                        },
                                        ticks: {
                                            callback: function (val) {
                                                if (val >= 1000000) return (val / 1000000).toFixed(1) + 'M';
                                                if (val >= 1000) return (val / 1000).toFixed(0) + 'K';
                                                return val;
                                            }
                                        }
                                    },
                                    y: {
                                        grid: { display: false }
                                    }
                                }
                            }
                        });
                    }
                </script>
            </body>

            </html>