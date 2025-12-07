package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.mapper.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    @Override
    public AuthUserEntity create(AuthUserEntity authUserEntity) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO \"user\" (username, password, " +
                            "enabled, account_non_expired, " +
                            "account_non_locked, credentials_non_expired) " +
                            "VALUES (?,?,?,?,?,?) ",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, authUserEntity.getUsername());
            statement.setString(2, passwordEncoder.encode(authUserEntity.getPassword()));
            statement.setBoolean(3, authUserEntity.getEnabled());
            statement.setBoolean(4, authUserEntity.getCredentialsNonExpired());
            statement.setBoolean(5, authUserEntity.getAccountNonLocked());
            statement.setBoolean(6, authUserEntity.getAccountNonExpired());
            return statement;
        }, keyHolder);

        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        authUserEntity.setId(generatedKey);
        AuthAuthorityEntity[] authority = authUserEntity.getAuthorities().toArray(AuthAuthorityEntity[]::new);

        template.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, generatedKey);
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
        return authUserEntity;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(template.query(
                """
                        SELECT user_id as id,
                            u.username,
                            u.password,
                            u.enabled,
                            u.account_non_expired,
                            u.account_non_locked,
                            u.credentials_non_expired,
                            a.id as authority_id,
                            authority
                        FROM "user" u
                        JOIN authority a ON u.id = a.user_id
                        WHERE u.id = ?
                        """,
                AuthUserEntityResultSetExtractor.instance,
                id
        ));
    }
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(template.query(
                """
                        SELECT user_id as id,
                            u.username,
                            u.password,
                            u.enabled,
                            u.account_non_expired,
                            u.account_non_locked,
                            u.credentials_non_expired,
                            a.id as authority_id,
                            authority
                        FROM "user" u
                        JOIN authority a ON u.id = a.user_id
                        WHERE u.username = ?
                        """,
                AuthUserEntityResultSetExtractor.instance,
                username
        ));
    }


}
