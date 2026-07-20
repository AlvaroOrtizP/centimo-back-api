package com.centimo.api.it.support;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.hibernate.stat.Statistics;

import java.util.HashMap;
import java.util.Map;

public class StatisticsAssert extends AbstractAssert<StatisticsAssert, Statistics> {

  private final Map<String, EntityExpectations> expectations = new HashMap<>();

  private StatisticsAssert(Statistics actual) {
    super(actual, StatisticsAssert.class);
  }

  public static StatisticsAssert assertThat(Statistics statistics) {
    return new StatisticsAssert(statistics);
  }

  public StatisticsAssert hasQueryExecutionCount(long count) {
    return this;
  }

  public EntityAssert forEntity(Class<?> entityClass) {
    return new EntityAssert(this, entityClass.getName());
  }

  public EntityAssert forEntity(String entityName) {
    return new EntityAssert(this, entityName);
  }

  public void verify() {
    isNotNull();
    var soft = new SoftAssertions();
    for (var name : actual.getEntityNames()) {
      var statistics = actual.getEntityStatistics(name);
      var exp = expectations.getOrDefault(name, EntityExpectations.allZero());
      assertOp(soft, name, "insertCount", statistics.getInsertCount(), exp.insert);
      assertOp(soft, name, "updateCount", statistics.getUpdateCount(), exp.update);
      assertOp(soft, name, "deleteCount", statistics.getDeleteCount(), exp.delete);
      assertOp(soft, name, "loadCount", statistics.getLoadCount(), exp.load);
      assertOp(soft, name, "fetchCount", statistics.getFetchCount(), exp.fetch);
    }
    soft.assertAll();
  }

  private void register(String entityName, EntityExpectations exp) {
    expectations.put(entityName, exp);
  }

  private void assertOp(SoftAssertions soft, String entity, String op, long actualValue, long expected) {
    soft.assertThat(actualValue)
        .as("%s.%s", entity, op)
        .isEqualTo(expected);
  }

  public static final class EntityAssert {

    private final StatisticsAssert parent;
    private final String entityName;
    private long insert;
    private long update;
    private long delete;
    private long load;
    private long fetch;

    private EntityAssert(StatisticsAssert parent, String entityName) {
      this.parent = parent;
      this.entityName = entityName;
    }

    public EntityAssert hasInsertCount(long count) { this.insert = count; return this; }
    public EntityAssert hasUpdateCount(long count) { this.update = count; return this; }
    public EntityAssert hasDeleteCount(long count) { this.delete = count; return this; }
    public EntityAssert hasLoadCount(long count)   { this.load   = count; return this; }
    public EntityAssert hasFetchCount(long count)  { this.fetch  = count; return this; }

    public EntityAssert and(Class<?> next) {
      flush();
      return parent.forEntity(next);
    }

    public EntityAssert and(String next) {
      flush();
      return parent.forEntity(next);
    }

    public void verify() {
      flush();
      parent.verify();
    }

    private void flush() {
      parent.register(entityName, new EntityExpectations(insert, update, delete, load, fetch));
    }
  }

  record EntityExpectations(long insert, long update, long delete, long load, long fetch) {
    static EntityExpectations allZero() {
      return new EntityExpectations(0L, 0L, 0L, 0L, 0L);
    }
  }
}
