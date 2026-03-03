package dev.smartconsumer.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String seq;    // 고객번호 (PK)
    private String password;
    private String sexCd;  // "1" or "2"
    private String age;    // 연령대 코드 ("20", "30" 등)
}
