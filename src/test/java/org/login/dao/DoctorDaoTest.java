package org.login.dao;

import org.junit.jupiter.api.Test;
import org.login.entity.Doctor;
import org.login.utils.AppUtils;

import javax.print.Doc;

import static org.junit.jupiter.api.Assertions.*;

class DoctorDaoTest {
    DoctorDao doctorDao = new DoctorDao(AppUtils.getDriver(), "hng");

    @Test
    void add() {
        boolean result = doctorDao.add(Doctor.builder()
                        .ID("22671951")
                        .name("TPH")
                        .speciality("IT")
                        .phone("0355227249")
                .build());

        assertTrue(result);
    }

    @Test
    void countDoctorBy() {
        int size = doctorDao.countDoctorBy("Internal Medicine").entrySet().size();

        assertEquals(5, size);
    }

    @Test
    void findDoctorBy() {
        int size = doctorDao.findDoctorBy("Internal").size();

        assertEquals(2, size);
    }

    @Test
    void updateDiagnosis() {
        boolean result = doctorDao.updateDiagnosis("PT007", "DR.007", "I'm good");

        assertTrue(result);
    }
}