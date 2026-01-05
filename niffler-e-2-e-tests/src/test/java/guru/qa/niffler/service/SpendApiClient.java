package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(CFG.spendUrl()).addConverterFactory(JacksonConverterFactory.create()).build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @SneakyThrows
    @Override
    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        response = spendApi.addSpend(spend).execute();
        assertEquals(201, response.code());
        return response.body();
    }

    @SneakyThrows
    public SpendJson editSpend(SpendJson spend) {
        final Response<SpendJson> response;
        response = spendApi.editSpend(spend).execute();
        assertEquals(200, response.code());
        return response.body();
    }

    @SneakyThrows
    public SpendJson getSpendById(String id) {
        final Response<SpendJson> response;
        response = spendApi.getSpendById(id).execute();
        assertEquals(200, response.code());
        return response.body();
    }

    @SneakyThrows
    public List<SpendJson> getAllSpends(String username, CurrencyValues currency, String from, String to) {
        final Response<List<SpendJson>> response;
        response = spendApi.allSpends(username, currency, from, to).execute();
        assertEquals(200, response.code());
        return response.body();

    }

    @SneakyThrows
    public void deleteSpend(String username, List<String> ids) {
        final Response<Void> response;
        response = spendApi.deleteSpend(username, ids).execute();
        assertEquals(202, response.code());
    }

    @SneakyThrows
    @Override
    public CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        response = spendApi.createCategory(category).execute();
        assertEquals(200, response.code());
        return response.body();
    }

    @SneakyThrows
    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        response = spendApi.updateCategory(category).execute();
        assertEquals(200, response.code());
        return response.body();

    }

    @SneakyThrows
    public List<CategoryJson> getAllCategoriesByUsername(String username) {
        final Response<List<CategoryJson>> response;
        response = spendApi.getAllCategories(username).execute();
        assertEquals(200, response.code());
        assert response.body() != null;
        return response.body();
    }

    public void deleteCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }
}
