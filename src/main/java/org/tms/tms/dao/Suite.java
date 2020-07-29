package org.tms.tms.dao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Table()
@Entity()
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Suite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;


    @ManyToOne()
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "projectId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project projectId;

    @ManyToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "parentId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Suite parentId;


}
