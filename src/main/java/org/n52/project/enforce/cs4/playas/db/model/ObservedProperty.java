package org.n52.project.enforce.cs4.playas.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * <p>
 * DataReference class.
 * </p>
 *
 * @author Benjamin Pross (b.pross@52north.org)
 * @since 1.0.0
 */
@Entity
@Table(
        name = "cs4_playas_observed_property")
public class ObservedProperty {

    @EmbeddedId
    ObservedPropertyPK datareferencePK;

    @Column(
            name = "count")
    Integer count;

    /**
     * <p>
     * Constructor for DataReference.
     * </p>
     */
    public ObservedProperty() {

    }

    /**
     * <p>
     * Constructor for DataReference.
     * </p>
     *
     * @param datareferencePK
     *            a
     *            {@link ObservedPropertyPK.conterra.businesstools.nrtlageservice.db.model.DatareferencePK}
     *            object
     */
    public ObservedProperty(ObservedPropertyPK datareferencePK) {
        this.datareferencePK = datareferencePK;
    }

    /**
     * <p>
     * Constructor for DataReference.
     * </p>
     *
     * @param datareferencePK
     *            a
     *            {@link ObservedPropertyPK.conterra.businesstools.nrtlageservice.db.model.DatareferencePK}
     *            object
     * @param bucket
     * @param name
     * @param checksum
     * @param sizeInByte
     */
    public ObservedProperty(ObservedPropertyPK datareferencePK, Integer count) {
        this.datareferencePK = datareferencePK;
        this.count = count;
    }

    /**
     * <p>
     * Getter for the field <code>datareferencePK</code>.
     * </p>
     *
     * @return a
     *         {@link ObservedPropertyPK.conterra.businesstools.nrtlageservice.db.model.DatareferencePK}
     *         object
     */
    public ObservedPropertyPK getDatareferencePK() {
        return datareferencePK;
    }

    /**
     * <p>
     * Setter for the field <code>datareferencePK</code>.
     * </p>
     *
     * @param datareferencePK
     *            a
     *            {@link ObservedPropertyPK.conterra.businesstools.nrtlageservice.db.model.DatareferencePK}
     *            object
     */
    public void setDatareferencePK(ObservedPropertyPK datareferencePK) {
        this.datareferencePK = datareferencePK;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
