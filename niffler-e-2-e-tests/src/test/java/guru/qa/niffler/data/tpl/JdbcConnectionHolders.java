package guru.qa.niffler.data.tpl;

import java.util.List;

public class JdbcConnectionHolders implements AutoCloseable {
//Этот класс делали что бы можно было работать с несколькими холдерами
//и закрыть все холдеры за один раз
  private final List<JdbcConnectionHolder> holders;

  public JdbcConnectionHolders(List<JdbcConnectionHolder> holders) {
    this.holders = holders;
  }

  @Override
  public void close() {
    holders.forEach(JdbcConnectionHolder::close);
  }
}
