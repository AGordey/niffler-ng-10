package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();


    private final SpendRepository spendRepository = SpendRepository.getInstance();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Nonnull
    @Override
    @Step("Создаём новую трату: {spend}")
    public SpendJson createSpend(SpendJson spend) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(spendRepository.create(spendEntity));
        }));
    }

    @Nonnull
    @Override
    @Step("Создаём новую категорию: {category}")
    public CategoryJson createCategory(CategoryJson category) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            return CategoryJson.fromEntity(
                    spendRepository.createCategory(CategoryEntity.fromJson(category))
            );
        }));
    }

    @Nonnull
    @Override
    @Step("Обновляем категорию: {category}")
    public CategoryJson updateCategory(CategoryJson category) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            return CategoryJson.fromEntity(
                    spendRepository.updateCategory(CategoryEntity.fromJson(category))
            );
        }));
    }
}