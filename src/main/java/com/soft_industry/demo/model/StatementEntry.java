package com.soft_industry.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class StatementEntry {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Client client;

    private OperationType operationType;

    private Long value;

    private Date date;

    public StatementEntry(Client client, OperationType operationType, Long value) {
        this.client = client;
        this.operationType = operationType;
        this.value = value;
        this.date = new Date();
    }
}
