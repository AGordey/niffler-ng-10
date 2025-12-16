package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "INSERT INTO \"user\" (" +
                        "username, firstname," +
                        " surname, full_name, " +
                        " currency, photo, " +
                        " photo_small) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getFullname());
            ps.setString(5, user.getCurrency().name());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "SELECT * FROM \"user\" u " +
                        "LEFT JOIN friendship f " +
                        "ON u.id = f.requester_id OR u.id = f.addressee_id " +
                        "WHERE id = ?"

        )) {
            ps.setObject(1, id);
            ps.execute();
            UserEntity user = null;
            ArrayList<FriendshipEntity> friendshipAddressees = new ArrayList<FriendshipEntity>();
            ArrayList<FriendshipEntity> friendshipRequests = new ArrayList<FriendshipEntity>();

            try (ResultSet resultSet = ps.getResultSet()) {
                while (resultSet.next()) {
                    UUID userId = resultSet.getObject("id", UUID.class);
                    if (user == null) {
                        user = UserdataUserEntityRowMapper.instance.mapRow(resultSet, 1);
                    }
                    if (resultSet.getObject("addressee_id", UUID.class) != null) {
                        UserEntity requester = new UserEntity();
                        UUID requesterId = resultSet.getObject("requester_id", UUID.class);
                        requester.setId(requesterId);
                        UserEntity addressee = new UserEntity();
                        UUID addresseeId = resultSet.getObject("addressee_id", UUID.class);
                        addressee.setId(addresseeId);
                        FriendshipEntity friendship = new FriendshipEntity();
                        friendship.setRequester(requester);
                        friendship.setAddressee(addressee);
                        friendship.setStatus(FriendshipStatus.valueOf(resultSet.getString("status")));
                        friendship.setCreatedDate(resultSet.getDate("created_date"));

                        if (addresseeId.equals(userId)) {
                            friendshipAddressees.add(friendship);
                        } else {
                            friendshipRequests.add(friendship);
                        }

                    }
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setFriendshipAddressees(friendshipAddressees);
                    user.setFriendshipRequests(friendshipRequests);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "SELECT * FROM \"user\" u " +
                        "LEFT JOIN friendship f " +
                        "ON u.id = f.requester_id OR u.id = f.addressee_id " +
                        "WHERE username = ?"

        )) {
            ps.setString(1, username);
            ps.execute();
            UserEntity user = null;
            ArrayList<FriendshipEntity> friendshipAddressees = new ArrayList<FriendshipEntity>();
            ArrayList<FriendshipEntity> friendshipRequests = new ArrayList<FriendshipEntity>();

            try (ResultSet resultSet = ps.getResultSet()) {
                while (resultSet.next()) {
                    UUID userId = resultSet.getObject("id", UUID.class);
                    if (user == null) {
                        user = UserdataUserEntityRowMapper.instance.mapRow(resultSet, 1);
                    }
                    if (resultSet.getObject("addressee_id", UUID.class) != null) {
                        UserEntity requester = new UserEntity();
                        UUID requesterId = resultSet.getObject("requester_id", UUID.class);
                        requester.setId(requesterId);
                        UserEntity addressee = new UserEntity();
                        UUID addresseeId = resultSet.getObject("addressee_id", UUID.class);
                        addressee.setId(addresseeId);
                        FriendshipEntity friendship = new FriendshipEntity();
                        friendship.setRequester(requester);
                        friendship.setAddressee(addressee);
                        friendship.setStatus(FriendshipStatus.valueOf(resultSet.getString("status")));
                        friendship.setCreatedDate(resultSet.getDate("created_date"));

                        if (addresseeId.equals(userId)) {
                            friendshipAddressees.add(friendship);
                        } else {
                            friendshipRequests.add(friendship);
                        }

                    }
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setFriendshipAddressees(friendshipAddressees);
                    user.setFriendshipRequests(friendshipRequests);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity update(UserEntity user) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "UPDATE \"user\" SET " +
                        "username = ?, " +
                        "firstname = ?, " +
                        "surname = ?, " +
                        "full_name = ?, " +
                        "currency = ?, " +
                        "photo = ?, " +
                        "photo_small = ?) " +
                        "WHERE id= ?"
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getSurname());
            ps.setString(4, user.getFullname());
            ps.setString(5, user.getCurrency().name());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());
            ps.setObject(8, user.getId());
            ps.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(addressee, requester, FriendshipStatus.PENDING);
    }


    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.ACCEPTED);
        createFriendshipWithStatus(addressee, requester, FriendshipStatus.ACCEPTED);
    }

    @Override
    public void remove(UserEntity user) {
        try (PreparedStatement friendshipStatement = holder(URL).connection().prepareStatement(
                "DELETE FROM \"friendship\" " +
                        "WHERE requester_id = ? OR addressee_id = ?"
        );
             PreparedStatement userStatement = holder(URL).connection().prepareStatement(
                     "DELETE FROM \"user\" " +
                             "WHERE id = ?"
             )) {
            friendshipStatement.setObject(1, user.getId());
            friendshipStatement.setObject(2, user.getId());
            friendshipStatement.executeUpdate();
            userStatement.setObject(1, user.getId());
            userStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createFriendshipWithStatus(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status) " +
                        "VALUES (?, ?, ?) "
        )) {
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, status.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
