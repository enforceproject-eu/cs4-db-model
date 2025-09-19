package org.n52.project.enforce.cs4.playas.db.repository;

import java.util.Optional;

import org.n52.project.enforce.cs4.playas.db.model.CS4PlayasObservedPropertyCount;
import org.n52.project.enforce.cs4.playas.db.model.CS4PlayasObservedPropertyCountPK;
import org.n52.project.enforce.cs4.playas.db.model.CS4PlayasObservedProperty;
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
public interface CS4PlayasObservedPropertyCountRepository extends JpaRepository<CS4PlayasObservedPropertyCount, CS4PlayasObservedPropertyCountPK> {

    /**
     * <p>
     * findById.
     * </p>
     *
     * @param id
     *            an CS4PlayasObservedPropertyCountPK
     * @return a
     *         {@link CS4PlayasObservedProperty}
     *         object
     */
    Optional<CS4PlayasObservedPropertyCount> findById(CS4PlayasObservedPropertyCountPK id);
}
