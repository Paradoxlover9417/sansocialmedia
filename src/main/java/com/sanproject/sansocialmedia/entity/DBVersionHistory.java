package com.sanproject.sansocialmedia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Immutable
@Table(name = "flyway_schema_history")
public class DBVersionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "installed_rank")
    private int installedRank;

    @Column(name = "version", nullable = false, length = 50)
    private String version;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "script", length = 1000)
    private String script;

    @Column(name = "checksum", nullable = false)
    private int checksum;

    @Column(name = "installed_by", length = 100)
    private String installedBy;

    @Column(name = "installed_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime installedOn;

    @Column(name = "execution_time")
    private Integer executionTime;

    @Column(name = "success", nullable = false)
    private boolean success;
}
