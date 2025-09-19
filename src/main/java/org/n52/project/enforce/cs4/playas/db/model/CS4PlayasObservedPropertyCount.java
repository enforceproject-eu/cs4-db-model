package org.n52.project.enforce.cs4.playas.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "cs4_playas_observed_property_count")
public class CS4PlayasObservedPropertyCount {

    @EmbeddedId
    private CS4PlayasObservedPropertyCountPK cs4_PlayasObservedPropertyCountPK;
    
    @Column(name="count")
    private Integer count;

    public CS4PlayasObservedPropertyCount(){}
    
    public CS4PlayasObservedPropertyCount(CS4PlayasObservedPropertyCountPK cs4ObservedPropertyCountPK) {
        super();
        this.cs4_PlayasObservedPropertyCountPK = cs4ObservedPropertyCountPK;
    }

    public CS4PlayasObservedPropertyCountPK getCs4ObservedPropertyCountPK() {
        return cs4_PlayasObservedPropertyCountPK;
    }

    public void setCs4ObservedPropertyCountPK(CS4PlayasObservedPropertyCountPK cs4ObservedPropertyCountPK) {
        this.cs4_PlayasObservedPropertyCountPK = cs4ObservedPropertyCountPK;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    
}
