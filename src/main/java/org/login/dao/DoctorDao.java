package org.login.dao;

import org.login.entity.Doctor;
import org.login.utils.AppUtils;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.types.Node;

import javax.print.Doc;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DoctorDao {
    private final Driver driver;
    private final SessionConfig sessionConfig;

    public DoctorDao(Driver driver, String name) {
        this.driver = driver;
        this.sessionConfig = SessionConfig.builder()
                .withDatabase(name)
                .build();
    }

    public boolean add(Doctor doctor) {
        String query = "CREATE (d:Doctor{doctorID: $id, name: $name, phone: $phone, speciality: $spec});";
        Map<String, Object> params = Map.of(
                "id", doctor.getID(),
                "name", doctor.getName(),
                "phone", doctor.getPhone(),
                "spec", doctor.getSpeciality()
        );

        try(Session session = driver.session(sessionConfig)) {
            return session.executeWrite(tx -> {
                ResultSummary summary = tx.run(query, params).consume();
                return summary.counters().nodesCreated() > 0;
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Integer> countDoctorBy(String deptName) {
        String query = "MATCH (d:Doctor)-[:BELONG_TO]->(de:Department)\n" +
                "WHERE de.name = $dept\n" +
                "WITH COUNT(d) AS soluong, d.speciality AS spec\n" +
                "RETURN spec, soluong";

        Map<String, Object> params = Map.of(
                "dept", deptName
        );

        try(Session session = driver.session(sessionConfig)) {
            return session.executeRead(tx -> {
                return tx.run(query, params)
                        .stream()
                        .collect(Collectors.toMap(
                            record -> record.get("spec").asString(), record -> record.get("soluong").asInt()
                        ));
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public List<Doctor> findDoctorBy(String spec) {
        String query = "CALL db.index.fulltext.queryNodes('spec_index', $spec) YIELD node, score\n" +
                "RETURN node, score";

        Map<String, Object> params = Map.of(
                "spec", spec
        );

        try(Session session = driver.session(sessionConfig)) {
            return session.executeRead(tx -> {
                return tx.run(query, params)
                        .stream()
                        .map(i -> {
                            Node node = i.get("node").asNode();
                            return AppUtils.toDoctor(node);
                        }).toList();
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean updateDiagnosis(String patientID, String doctorID, String newDiagnosis) {
        String query = "MATCH (p:Patient{patientID: $patientID})-[r:BE_TREATED]->(d:Doctor{doctorID: $doctorID})\n" +
                "SET r.diagnosis = $diagnosis\n";

        Map<String, Object> params = Map.of(
                "patientID", patientID,
                "doctorID", doctorID,
                "diagnosis", newDiagnosis
        );

        try(Session session = driver.session(sessionConfig)) {
            return session.executeWrite(tx -> {
                ResultSummary summary = tx.run(query, params).consume();
                return summary.counters().containsUpdates();
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
