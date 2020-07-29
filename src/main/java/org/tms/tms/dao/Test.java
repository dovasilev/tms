package org.tms.tms.dao;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table()
@Entity()
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
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
    @JoinColumn(name = "suiteId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Suite suiteId;

    @ManyToOne
    @NotFound(action = NotFoundAction.EXCEPTION)
    @JoinColumn(name = "projectId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private Project projectId;

    @Column
    private Boolean automated;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private Steps steps;


}
