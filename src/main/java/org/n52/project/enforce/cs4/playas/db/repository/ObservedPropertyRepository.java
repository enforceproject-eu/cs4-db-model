package org.n52.project.enforce.cs4.playas.db.repository;

import java.util.Optional;

import org.n52.project.enforce.cs4.playas.db.model.ObservedProperty;
import org.n52.project.enforce.cs4.playas.db.model.ObservedPropertyPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * DataReferenceRepository interface.
 * </p>
 *
 * @author Benjamin Pross (b.pross@52north.org)
 * @since 1.0.0
 */
@Repository
public interface ObservedPropertyRepository extends JpaRepository<ObservedProperty, ObservedPropertyPK> {

    /**
     * <p>
     * findById.
     * </p>
     *
     * @param id
     *            a DatareferencePK
     * @return a
     *         {@link ObservedProperty.conterra.businesstools.nrtlageservice.db.model.DataReference}
     *         object
     */
    Optional<ObservedProperty> findById(ObservedPropertyPK id);
}
