package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO \"spend\" (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKey);
        return spend;

    }

    @NotNull
    @Override
    public SpendEntity update(SpendEntity spend) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            UPDATE spend
                            SET username = ?,
                                spend_date = ?,
                                currency = ?,
                                amount = ?,
                                description = ?,
                                category_id = ?
                            WHERE id = ?
                            """);
            statement.setString(1, spend.getUsername());
            statement.setDate(2, new Date(spend.getSpendDate().getTime()));
            statement.setString(3, spend.getCurrency().name());
            statement.setDouble(4, spend.getAmount());
            statement.setString(5, spend.getDescription());
            statement.setObject(6, spend.getCategory().getId());
            statement.setObject(7, spend.getId());
            return statement;
        });
        return spend;
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                        "SELECT * FROM \"spend\" WHERE id = ?",
                        SpendEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"spend\" WHERE username = ?",
                SpendEntityRowMapper.instance,
                username
        );
    }


    @Override
    public List<SpendEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"spend\" ",
                SpendEntityRowMapper.instance
        );
    }

    @Override
    public void delete(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM \"spend\" WHERE id = ?"
            );
            ps.setObject(1, spend.getId());
            return ps;
        });
    }
}

