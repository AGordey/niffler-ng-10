package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface AuthAuthorityDao {

    void create(AuthAuthorityEntity... authAuthorityEntity);

    @Nonnull
    List<AuthAuthorityEntity> findAll();
}
