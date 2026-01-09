package org.n52.project.enforce.cs4.api.impl.minka;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <p>
 * Data repository.
 * </p>
 *
 * @author Benjamin Pross (spross (at)muenster.de)
 * @since 1.0.0
 */
public interface Cs4MinkaDataRepository extends JpaRepository<Cs4MinkaData, UUID> {

    /**
     * <p>
     * getGeoJson.
     * </p>
     * 
     * @return a {@link String} object
     */
    @Query("select st_cs4_minka_datatogeojson()")
    String getGeoJson();

    /**
     * <p>
     * getGeoJsonWithLimit.
     * </p>
     * 
     * @return a {@link String} object
     */
    @Query("select st_cs4_minka_datatogeojsonwithlimit(cast(:limit as int))")
    String getGeoJsonWithLimit(@Param("limit") Integer limit);

}
