package dev.smartconsumer.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import dev.smartconsumer.common.Const;
import dev.smartconsumer.model.dto.StatDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnalysisDAO {

    private final DataSource replicaDs;

    // 읽기 전용 기능이므로 Replica DB만 주입받음
    public AnalysisDAO(DataSource replicaDs) {
        this.replicaDs = replicaDs;
    }

    public List<StatDTO> getMyTopConsumption(String seq) {
        List<StatDTO> all = getMyAllConsumption(seq);
        all.sort(Comparator.comparingLong(StatDTO::getTotalAmount).reversed());
        return all.size() > 3 ? new ArrayList<>(all.subList(0, 3)) : all;
    }

    public List<StatDTO> getMyAllConsumption(String seq) {
        List<StatDTO> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT ");
        for (int i = 0; i < Const.CATEGORIES.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append("SUM(").append(Const.CATEGORIES[i][0]).append(") AS ").append(Const.CATEGORIES[i][0]);
        }
        sb.append(" FROM EDU_DATA_F_2 WHERE SEQ = ?");

        // replicaDs 사용
        try (Connection con = replicaDs.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {

            ps.setString(1, seq);

            try (ResultSet rs = ps.executeQuery()) {
                long grandTotal = 0;
                if (rs.next()) {
                    for (String[] cat : Const.CATEGORIES) {
                        long amount = rs.getLong(cat[0]) * 1000;
                        if (amount > 0) {
                            list.add(StatDTO.builder()
                                    .categoryName(cat[1])
                                    .totalAmount(amount)
                                    .build());
                            grandTotal += amount;
                        }
                    }
                }
                if (grandTotal > 0) {
                    for (StatDTO stat : list) {
                        stat.setPercentage(Math.round(stat.getTotalAmount() * 1000.0 / grandTotal) / 10.0);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting personal consumption for SEQ: {}", seq, e);
        }

        list.sort(Comparator.comparingLong(StatDTO::getTotalAmount).reversed());
        return list;
    }

    public List<StatDTO> getPeerStats(String age, String sexCd) {
        List<StatDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM AVERAGE_DATA_F WHERE TRIM(SEX_CD) = ? AND TRIM(AGE) = ?";

        // replicaDs 사용
        try (Connection con = replicaDs.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sexCd);
            ps.setString(2, age);

            try (ResultSet rs = ps.executeQuery()) {
                long grandTotal = 0;
                if (rs.next()) {
                    for (int i = 0; i < Const.CATEGORIES.length; i++) {
                        long amount = rs.getLong(Const.AVG_COLUMNS[i]) * 1000;
                        if (amount > 0) {
                            list.add(StatDTO.builder()
                                    .categoryName(Const.CATEGORIES[i][1])
                                    .totalAmount(amount)
                                    .build());
                            grandTotal += amount;
                        }
                    }
                }
                if (grandTotal > 0) {
                    for (StatDTO stat : list) {
                        stat.setPercentage(Math.round(stat.getTotalAmount() * 1000.0 / grandTotal) / 10.0);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting peer stats (AGE={}, SEX_CD={})", age, sexCd, e);
        }

        list.sort(Comparator.comparingLong(StatDTO::getTotalAmount).reversed());
        return list.size() > 5 ? new ArrayList<>(list.subList(0, 5)) : list;
    }
}