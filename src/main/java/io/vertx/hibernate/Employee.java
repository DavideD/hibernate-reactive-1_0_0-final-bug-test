package io.vertx.hibernate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE employee SET deleted = true WHERE id = $1")
@Where(clause = "deleted = false")
public class Employee {
  @Id @GeneratedValue private UUID id;

  private String name;

  @ManyToOne private Employee boss;

  @ElementCollection private List<String> tags;

  @CreationTimestamp private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;

  @Builder.Default private boolean deleted = false;
}
