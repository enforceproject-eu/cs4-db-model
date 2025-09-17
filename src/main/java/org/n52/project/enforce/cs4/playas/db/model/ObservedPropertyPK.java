package org.n52.project.enforce.cs4.playas.db.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

/**
 * <p>
 * ObservedPropertyPK class.
 * </p>
 *
 * @author Benjamin Pross (b.pross@52north.org)
 * @since 1.0.0
 */
@Embeddable
public class ObservedPropertyPK implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6515448823447579875L;

    private Integer id;

    private String name;

    /**
     * <p>
     * Constructor for ObservedPropertyPK.
     * </p>
     */
    public ObservedPropertyPK() {
    }

    /**
     * <p>
     * Constructor for ObservedPropertyPK.
     * </p>
     *
     * @param date
     *            a {@link java.util.Date} object
     * @param path
     *            a {@link java.lang.String} object
     */
    public ObservedPropertyPK(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * <p>
     * Getter for the field <code>id</code>.
     * </p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getId() {
        return id;
    }

    /**
     * <p>
     * Setter for the field <code>id</code>.
     * </p>
     *
     * @param date
     *            a {@link java.lang.Integer} object
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * <p>
     * Getter for the field <code>name</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Setter for the field <code>name</code>.
     * </p>
     *
     * @param path
     *            a {@link java.lang.String} object
     */
    public void setName(String path) {
        this.name = path;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObservedPropertyPK datareference = (ObservedPropertyPK) o;
        return Objects.equals(this.id, datareference.id) && Objects.equals(this.name, datareference.name);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
