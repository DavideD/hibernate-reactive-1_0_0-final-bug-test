package io.vertx.hibernate;

import java.util.List;
import java.util.concurrent.CompletionStage;
import javax.persistence.Persistence;

import org.hibernate.reactive.stage.Stage;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Promise;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MainVerticle extends AbstractVerticle {

  Stage.SessionFactory factory;

  private Completable prepareData(Stage.SessionFactory factory) {
    return Completable.fromCompletionStage( persist( factory ) );
  }

  private CompletionStage<Void> persist(Stage.SessionFactory factory) {
    System.out.println( "Preparing data" );
    var bossEntity = Employee.builder().name( "boss" ).tags( List.of( "tag2", "tag4" ) ).build();
    var employeeEntity =
      Employee.builder()
        .name( "employee" )
        .boss( bossEntity )
        .tags( List.of( "tag1", "tag2" ) )
        .build();
    var employee2Entity =
      Employee.builder()
        .name( "employee 2" )
        .boss( employeeEntity )
        .tags( List.of( "tag3", "tag4" ) )
        .build();
    bossEntity.boss( employee2Entity );
    return factory.withTransaction( (session, t) -> session.persist( employeeEntity, employee2Entity, bossEntity ) )
      .thenApply( v -> List.of( bossEntity, employeeEntity, employee2Entity ) )
      .thenAccept( e -> {
        log.info( "Employee: {} - {}", e.get( 1 ).id(), e.get( 1 ).name() );
        log.info( "Employee2: {} - {}", e.get( 2 ).id(), e.get( 2 ).name() );
        log.info( "Boss: {} - {}", e.get( 0 ).id(), e.get( 0 ).name() );
      } );
  }

  private void startHibernate(Promise<Stage.SessionFactory> promise) {
    try {
      this.factory = Persistence
        .createEntityManagerFactory( "postgresql" )
        .unwrap( Stage.SessionFactory.class );
      promise.complete( factory );
    }
    catch (Exception e) {
      promise.fail( e );
    }
  }

  @Override
  public Completable rxStart() {
    System.out.println( "Starting" );
    return vertx
      .executeBlocking( this::startHibernate )
      .concatMapCompletable( this::prepareData );
  }

  @Override
  public Completable rxStop() {
    factory.close();
    return Completable.complete();
  }
}
