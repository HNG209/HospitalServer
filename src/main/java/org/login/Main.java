package org.login;

import org.login.dao.DoctorDao;
import org.login.entity.Doctor;
import org.login.utils.AppUtils;
import org.neo4j.driver.Driver;

public class Main {
    public static void main(String[] args) {
        Driver driver = AppUtils.getDriver();
        String dbName = "hng";
        DoctorDao doctorDao = new DoctorDao(driver, dbName);

//        doctorDao.add(Doctor.builder()
//                        .ID("MR-BEAN")
//                        .name("Mr.Bean")
//                        .speciality("Physiology")
//                        .phone("1233-4566-7889")
//                .build());
//        doctorDao.countDoctorBy("Internal Medicine").entrySet().forEach(System.out::println);
        doctorDao.findDoctorBy("Internal").forEach(System.out::println);
//        System.out.println(doctorDao.updateDiagnosis("PT00AA", "DR.007", "not trivial at all:))"));
    }
}