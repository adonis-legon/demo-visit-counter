package com.example.demovisitcounter;

import java.util.HashMap;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("sql")
public class SqlVisitRepository implements VisitRepository{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SqlVisitRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void incrementCounter(int visitId) {
        interalIncrementCounter(visitId);
    }

    @Transactional
    private void interalIncrementCounter(int visitId) {
        String selectForUpdateSql = "SELECT counter FROM visit WHERE id = :id FOR UPDATE";
        String updateSql = "UPDATE visit SET counter = counter + 1 WHERE id = :id";

        jdbcTemplate.queryForObject(selectForUpdateSql, new HashMap<String, Object>() {
            {
                put("id", visitId);
            }
        }, Integer.class);

        jdbcTemplate.update(updateSql, new HashMap<String, Object>() {
            {
                put("id", visitId);
            }
        });
    }
    
}
