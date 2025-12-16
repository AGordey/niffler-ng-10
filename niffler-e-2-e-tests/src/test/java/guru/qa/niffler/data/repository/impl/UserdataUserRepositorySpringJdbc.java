package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(template.query(
                "SELECT * FROM \"user\" u " +
                        "LEFT JOIN friendship f " +
                        "ON u.id = f.requester_id OR u.id = f.addressee_id " +
                        "WHERE id = ?",
                UserdataUserEntityResultSetExtractor.instance,
                id
        ));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(template.query(
                "SELECT * FROM \"user\" u " +
                        "LEFT JOIN friendship f " +
                        "ON u.id = f.requester_id OR u.id = f.addressee_id " +
                        "WHERE username = ?",
                UserdataUserEntityResultSetExtractor.instance,
                username
        ));
    }

    @Override
    public UserEntity update(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE \"user\" SET " +
                            "username = ?, currency = ?, " +
                            "firstname = ?, surname = ?, photo = ?, " +
                            "photo_small = ?, full_name = ? " +
                            "WHERE id = ?"
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            ps.setObject(8, user.getId());
            return ps;
        });
        return user;
    }


    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.PENDING);
    }


    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.ACCEPTED);
        createFriendshipWithStatus(addressee, requester, FriendshipStatus.ACCEPTED);
    }

    @Override
    public void remove(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        template.update("DELETE FROM friendship WHERE requester_id = ? or addressee_id = ?", user.getId(), user.getId());
        template.update("DELETE FROM \"user\" WHERE id = ?", user.getId());

    }

    private void createFriendshipWithStatus(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    " INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?) "
            );
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, status.name());
            return statement;
        });
    }
}