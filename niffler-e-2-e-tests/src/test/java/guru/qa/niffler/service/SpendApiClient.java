package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient{

  private static final Config CFG = Config.getInstance();

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  @Override
  public SpendJson createSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try{
      response = spendApi.createSpend(spend)
              .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(201, response.code());
    return response.body();
  }
  @Override
  public SpendJson editSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try{
      response = spendApi.editSpend(spend)
              .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Override
  public SpendJson getSpendById(String id) {
    final Response<SpendJson> response;
    try{
      response = spendApi.getSpendById(id)
              .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Override
  public  List<SpendJson> getAllSpends(String username) {
    final Response<SpendJson[]> response;
    try {
      response = spendApi.getAllSpends(username)
              .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return List.of(Objects.requireNonNullElseGet(response.body(), () -> new SpendJson[0]));

  }


  @Override
  public void deleteSpend(String username,List<String> ids) {
    final Response<Void> response;
    try{
      response = spendApi.deleteSpend(username,ids)
              .execute();
   } catch (IOException e) {
     throw new AssertionError(e);
   }
    assertEquals(202, response.code());

  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Not implemented :(");
  }

  @Override
  public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
    throw new UnsupportedOperationException("Not implemented :(");
  }
}
