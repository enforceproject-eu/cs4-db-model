package org.n52.project.enforce.cs4.playas.db.model;

public class CS4PlayasObservedPropertyCountPK {

    private Integer cs4playas_data_id;

    private Integer cs4playas_observed_property_id;
    
    public CS4PlayasObservedPropertyCountPK() {}
    
    public CS4PlayasObservedPropertyCountPK(Integer dataId, Integer observedPropertyId) {
        super();
        this.cs4playas_data_id = dataId;
        this.cs4playas_observed_property_id = observedPropertyId;
    }

    public Integer getDataId() {
        return cs4playas_data_id;
    }

    public void setDataId(Integer dataId) {
        this.cs4playas_data_id = dataId;
    }

    public Integer getObservedPropertyId() {
        return cs4playas_observed_property_id;
    }

    public void setObservedPropertyId(Integer observedPropertyId) {
        this.cs4playas_observed_property_id = observedPropertyId;
    }
    
}
