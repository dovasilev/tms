package org.tms.tms.dao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table()
@Entity()
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type;

}
