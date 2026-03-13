package com.aguardientes.azarcafetero.lobby.infrastructure.persistence.entity;

import com.aguardientes.azarcafetero.lobby.domain.model.TableState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tables")
@Getter
@Setter
public class TableJpaEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew = true;

    @PostLoad
    @PostPersist
    void markNotNew() { this.isNew = false; }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private FloorJpaEntity floor;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal minBet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableState state;

    @Column(nullable = false)
    private Integer playerCount;
}
