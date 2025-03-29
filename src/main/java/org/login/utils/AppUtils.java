package org.login.utils;

import org.login.entity.Doctor;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.types.Node;

public class AppUtils {
    public static Driver getDriver() {
        String uri = "neo4j://localhost:7687";
        String username = "neo4j";
        String password = "29092004";
        AuthToken authToken = AuthTokens.basic(username, password);

        return GraphDatabase.driver(uri, authToken);
    }

    public static Doctor toDoctor(Node node) {
        return Doctor.builder()
                .ID(node.get("doctorID").asString())
                .name(node.get("name").asString())
                .speciality(node.get("speciality").asString())
                .phone(node.get("phone").asString())
                .build();
    }
}
