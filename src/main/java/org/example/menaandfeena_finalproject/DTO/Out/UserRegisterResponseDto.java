package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserRegisterResponseDto {
    private Integer id;
    private String fullName;
    private String email;
    private String detectedNeighborhoodName; // اسم الحي الذي تم لقطه وتخزينه تلقائياً
    private String city;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
