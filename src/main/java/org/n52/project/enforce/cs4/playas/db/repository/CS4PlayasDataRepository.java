package org.n52.project.enforce.cs4.playas.db.repository;

import org.n52.project.enforce.cs4.playas.db.model.CS4PlayasData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p>
 * Data repository.
 * </p>
 *
 * @author Benjamin Pross 
 * @since 1.0.0
 */
public interface CS4PlayasDataRepository extends JpaRepository<CS4PlayasData, Integer> {
	
}
