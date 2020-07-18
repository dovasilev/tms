package org.tms.tms.dao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table()
@Entity()
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column
    private String title;

    @Column
    private String status;

    @Column
    private String description;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "suiteId", nullable = false)
    private Suite suiteId;

    @ManyToOne
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "projectId", nullable = false)
    @NotNull
    private Project projectId;

    @Column
    private String[] parentIds;

    @Column
    private Boolean automated;

    @Column
    private String steps;


}
