package org.login.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Patient implements Serializable {
    private String ID;
    private String name;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String address;
}
