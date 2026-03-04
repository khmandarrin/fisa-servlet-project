package dev.smartconsumer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDTO {
    private String categoryName; // 카테고리명 (대분류)
    private long totalAmount;    // 총 소비 금액
    private double percentage;   // 전체 대비 비율 (%)
}