package com.aimprosoft.i18n.common.model;

import javax.persistence.*;

@MappedSuperclass
public abstract class BaseModel implements HibernateModel{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
