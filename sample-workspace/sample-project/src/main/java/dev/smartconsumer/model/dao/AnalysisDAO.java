package dev.smartconsumer.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;

import dev.smartconsumer.common.Const;
import dev.smartconsumer.common.DBUtil;
import dev.smartconsumer.model.dto.StatDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnalysisDAO {

    /**
     * [개인 분석] 나의 소비 Top 3 조회
     * EDU_DATA_F_2에서 10개 카테고리 컬럼을 SUM하고 상위 3개 반환
     */
    public List<StatDTO> getMyTopConsumption(ServletContext ctx, String seq) {
        List<StatDTO> all = getMyAllConsumption(ctx, seq);
        // 금액 기준 내림차순 정렬 후 Top 3
        all.sort(Comparator.comparingLong(StatDTO::getTotalAmount).reversed());
        return all.size() > 3 ? new ArrayList<>(all.subList(0, 3)) : all;
    }

    /**
     * [개인 분석] 전체 카테고리별 소비 (원형 차트용)
     * EDU_DATA_F_2의 10개 카테고리 컬럼을 SUM → 각 카테고리별 총액 + 비율
     */
    public List<StatDTO> getMyAllConsumption(ServletContext ctx, String seq) {
        List<StatDTO> list = new ArrayList<>();

        // 10개 카테고리 컬럼을 모두 SUM
        StringBuilder sb = new StringBuilder("SELECT ");
        for (int i = 0; i < Const.CATEGORIES.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append("SUM(").append(Const.CATEGORIES[i][0]).append(") AS ").append(Const.CATEGORIES[i][0]);
        }
        sb.append(" FROM EDU_DATA_F_2 WHERE SEQ = ?");

        try (Connection con = DBUtil.getConnection(ctx, DBUtil.DBType.REPLICA);
             PreparedStatement ps = con.prepareStatement(sb.toString())) {

            ps.setString(1, seq);

            try (ResultSet rs = ps.executeQuery()) {
                long grandTotal = 0;
                if (rs.next()) {
                    for (String[] cat : Const.CATEGORIES) {
                        long amount = rs.getLong(cat[0]);
                        if (amount > 0) {
                            list.add(StatDTO.builder()
                                    .categoryName(cat[1]) // 한글명
                                    .totalAmount(amount)
                                    .build());
                            grandTotal += amount;
                        }
                    }
                }
                // 비율 계산
                if (grandTotal > 0) {
                    for (StatDTO stat : list) {
                        stat.setPercentage(Math.round(stat.getTotalAmount() * 1000.0 / grandTotal) / 10.0);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting personal consumption for SEQ: {}", seq, e);
        }

        // 금액 기준 내림차순 정렬
        list.sort(Comparator.comparingLong(StatDTO::getTotalAmount).reversed());
        return list;
    }

    /**
     * [또래 분석] 동연령대/동성별 평균 소비
     * AVERAGE_DATA_F에서 SEX_CD + AGE로 조회, 10개 _MEAN 컬럼을 매핑
     */
    public List<StatDTO> getPeerStats(ServletContext ctx, String age, String sexCd) {
        List<StatDTO> list = new ArrayList<>();

        String sql = "SELECT * FROM AVERAGE_DATA_F WHERE TRIM(SEX_CD) = ? AND TRIM(AGE) = ?";

        try (Connection con = DBUtil.getConnection(ctx, DBUtil.DBType.REPLICA);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sexCd);
            ps.setString(2, age);

            try (ResultSet rs = ps.executeQuery()) {
                long grandTotal = 0;
                if (rs.next()) {
                    for (int i = 0; i < Const.CATEGORIES.length; i++) {
                        long amount = rs.getLong(Const.AVG_COLUMNS[i]);
                        if (amount > 0) {
                            list.add(StatDTO.builder()
                                    .categoryName(Const.CATEGORIES[i][1]) // 한글명
                                    .totalAmount(amount)
                                    .build());
                            grandTotal += amount;
                        }
                    }
                }
                // 비율 계산
                if (grandTotal > 0) {
                    for (StatDTO stat : list) {
                        stat.setPercentage(Math.round(stat.getTotalAmount() * 1000.0 / grandTotal) / 10.0);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting peer stats (AGE={}, SEX_CD={})", age, sexCd, e);
        }

        // 금액 기준 내림차순 정렬 후 Top 5
        list.sort(Comparator.comparingLong(StatDTO::getTotalAmount).reversed());
        return list.size() > 5 ? new ArrayList<>(list.subList(0, 5)) : list;
    }
}