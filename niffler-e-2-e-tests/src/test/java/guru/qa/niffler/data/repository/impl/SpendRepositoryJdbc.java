package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.spendJdbcUrl();
    private static final SpendDao spendDao = new SpendDaoJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoJdbc();

    @Override
    public SpendEntity create(SpendEntity spend) {
        if (spend.getCategory().getId() == null || categoryDao.findById(spend.getCategory().getId()).isEmpty()) {
            spend.setCategory(
                    categoryDao.create(spend.getCategory())
            );
        }
        return spendDao.create(spend);
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement spendPs = holder(URL).connection().prepareStatement(
                "UPDATE \"spend\" SET " +
                        "username = ?, spend_date = ?, " +
                        "currency = ?, amount = ?, " +
                        "description = ?, category_id = ?" +
                        "WHERE id = ?"
        );
             PreparedStatement categoryPs = holder(URL).connection().prepareStatement(
                     "UPDATE \"category\" SET name = ?,username = ?, archived = ? " +
                             "WHERE id = ?"
             )) {
            spendPs.setString(1, spend.getUsername());
            spendPs.setDate(2, new Date(spend.getSpendDate().getTime()));
            spendPs.setString(3, spend.getCurrency().name());
            spendPs.setDouble(4, spend.getAmount());
            spendPs.setString(5, spend.getDescription());
            spendPs.setObject(5, spend.getId());
            spendPs.setObject(6, spend.getCategory().getId());
            spendPs.executeUpdate();

            categoryPs.setString(1, spend.getCategory().getName());
            categoryPs.setString(2, spend.getCategory().getUsername());
            categoryPs.setBoolean(3, spend.getCategory().isArchived());
            categoryPs.setObject(4, spend.getCategory().getId());
            categoryPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spend;
    }


    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        return categoryDao.update(category);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name) {
        return categoryDao.findByUsernameAndCategoryName(username, name);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return spendDao.findById(id);
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "SELECT * FROM \"spend\" WHERE username = ? and description = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, description);
            ps.execute(); // Делаем execute тк делаем SELECT a не INSERT
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity();
                    se.setId(rs.getObject("id", UUID.class));
                    se.setUsername(rs.getString("username"));
                    se.setSpendDate(rs.getDate("spend_date"));
                    se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    se.setAmount(rs.getDouble("amount"));
                    se.setDescription(rs.getString("description"));
                    CategoryEntity category = new CategoryEntity();
                    category.setId(rs.getObject("category_id", UUID.class));
                    se.setCategory(category);
                    return Optional.of(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDao.delete(spend);

    }

    @Override
    public void removeCategory(CategoryEntity spend) {
        categoryDao.delete(spend);
    }
}
