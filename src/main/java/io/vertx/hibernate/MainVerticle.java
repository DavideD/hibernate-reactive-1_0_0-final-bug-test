package io.vertx.hibernate;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.AbstractVerticle;
import lombok.extern.log4j.Log4j2;
import org.hibernate.reactive.stage.Stage;

import javax.persistence.Persistence;
import java.util.List;

@Log4j2
public class MainVerticle extends AbstractVerticle {
  private Completable prepareData(final Stage.SessionFactory factory) {
    return Single.fromCompletionStage(factory.withSession(
        session -> {
          var bossEntity = Employee.builder().name("boss").tags(List.of("tag2", "tag4")).build();
          var employeeEntity =
            Employee.builder()
              .name("employee")
              .boss(bossEntity)
              .tags(List.of("tag1", "tag2"))
              .build();
          var employee2Entity =
            Employee.builder()
              .name("employee 2")
              .boss(employeeEntity)
              .tags(List.of("tag3", "tag4"))
              .build();
          bossEntity.boss(employee2Entity);
          return session
            .persist(employeeEntity, employee2Entity, bossEntity)
            .thenApply((v) -> List.of(bossEntity, employeeEntity, employee2Entity))
            ;
        }))
      .doOnSuccess(
        (e) -> {
          log.info("Employee: {} - {}", e.get(1).id(), e.get(1).name());
          log.info("Employee2: {} - {}", e.get(2).id(), e.get(2).name());
          log.info("Boss: {} - {}", e.get(0).id(), e.get(0).name());
        })
      .ignoreElement();
  }

  @Override
  public Completable rxStart() {
    return Single.fromCallable(() -> Persistence.createEntityManagerFactory("postgresql")
        .unwrap(Stage.SessionFactory.class))
      .flatMapCompletable(this::prepareData);
  }
}
