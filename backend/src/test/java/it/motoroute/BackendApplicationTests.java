package it.motoroute;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class BackendApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void flywayCreatesRoutesTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM information_schema.tables
            WHERE table_schema = 'public'
              AND table_name = 'routes'
            """,
            Integer.class
        );

        assertThat(tableCount).isEqualTo(1);
    }

    @Test
    void flywayAppliesFirstMigrationSuccessfully() {
        Boolean migrationSuccessful = jdbcTemplate.queryForObject(
            """
            SELECT success
            FROM flyway_schema_history
            WHERE version = '1'
            """,
            Boolean.class
        );

        assertThat(migrationSuccessful).isTrue();
    }
}
