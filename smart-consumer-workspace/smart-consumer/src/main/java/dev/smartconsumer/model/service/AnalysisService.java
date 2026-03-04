package dev.smartconsumer.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.smartconsumer.model.dao.AnalysisDAO;
import dev.smartconsumer.model.dto.StatDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 소비 분석 관련 비즈니스 로직을 담당하는 서비스 계층.
 * Spring 컨테이너에 의해 빈으로 관리되며, AnalysisDAO를 생성자 주입받음.
 */
@Slf4j
@Service
public class AnalysisService {

    private final AnalysisDAO analysisDAO;

    // 생성자 주입 (단일 생성자이므로 @Autowired 생략 가능)
    public AnalysisService(AnalysisDAO analysisDAO) {
        this.analysisDAO = analysisDAO;
        log.info(">>> AnalysisService initialized with AnalysisDAO");
    }

    /**
     * [개인 분석] 나의 소비 Top 3 조회
     */
    public List<StatDTO> getMyTopConsumption(String seq) {
        return analysisDAO.getMyTopConsumption(seq);
    }

    /**
     * [개인 분석] 전체 카테고리별 소비 (원형 차트용)
     */
    public List<StatDTO> getMyAllConsumption(String seq) {
        return analysisDAO.getMyAllConsumption(seq);
    }

    /**
     * [또래 분석] 동연령대/동성별 평균 소비
     */
    public List<StatDTO> getPeerStats(String age, String sexCd) {
        return analysisDAO.getPeerStats(age, sexCd);
    }

    /**
     * 대시보드용: 전체 소비 + Top 3 추출 (DB 재조회 없음)
     */
    public List<StatDTO> getMyTop3FromAll(List<StatDTO> allStats) {
        return allStats.size() > 3 ? new ArrayList<>(allStats.subList(0, 3)) : allStats;
    }
}
