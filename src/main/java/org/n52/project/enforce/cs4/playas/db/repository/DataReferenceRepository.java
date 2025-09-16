package org.n52.project.enforce.cs4.playas.db.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.n52.project.enforce.cs4.playas.db.model.ObservedProperty;
import org.n52.project.enforce.cs4.playas.db.model.ObservedPropertyPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <p>
 * DataReferenceRepository interface.
 * </p>
 *
 * @author Benjamin Pross (b.pross@52north.org)
 * @since 1.0.0
 */
public interface DataReferenceRepository extends JpaRepository<ObservedProperty, ObservedPropertyPK> {

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
