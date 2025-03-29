package org.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Treatment implements Serializable {
    private String doctorID;
    private String patientID;
    private LocalDate startDate;
    private LocalDate endDate;
    private String diagnosis;
}
