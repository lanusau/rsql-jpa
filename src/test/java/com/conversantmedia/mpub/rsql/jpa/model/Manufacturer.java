package com.conversantmedia.mpub.rsql.jpa.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "manufacturer")
@Getter
@Setter
@ToString
public class Manufacturer {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
}
