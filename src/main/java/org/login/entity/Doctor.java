package org.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor implements Serializable {
    private String ID;
    private String name;
    private String phone;
    private String speciality;
    private String departmentID;
}
