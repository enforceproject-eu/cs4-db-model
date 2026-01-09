CREATE TABLE IF NOT EXISTS public.cs4_minka_data
(
    id uuid NOT NULL,
    location geometry(Point,4326),
    user_id smallint,
    species_name character varying(255),
    observed_datetime timestamp with time zone,
    updated_datetime timestamp with time zone,
    status character varying(255),
    media_url character varying(255),
    CONSTRAINT data_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.cs4playas_data
(
    id int NOT NULL,
    location_name character varying(100),
    entity character varying(100),
    country character varying(100),
    community character varying(100),
    province character varying(100),
    municipality character varying(100),
    collection_type character varying(255),
    project character varying(255),
    monitoring_length_meters smallint,
    initial_coordinates geometry,
    final_coordinates geometry,
    cleaning_date date,
    total_items int,
    remarks character varying(1024),
    CONSTRAINT cs4playas_data_pkey PRIMARY KEY (id)
);
    
CREATE TABLE IF NOT EXISTS public.cs4playas_observed_property
(
    id int NOT NULL,
    name character varying(255),
    CONSTRAINT cs4playas_observed_property_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.cs4playas_data_observed_property_count
(
    cs4playas_data_id integer NOT NULL,
    observed_property_count integer,
    observed_property_count_key integer NOT NULL,
    CONSTRAINT cs4playas_data_observed_property_count_pkey PRIMARY KEY (cs4playas_data_id, observed_property_count_key),
    CONSTRAINT cs4playas_data_fkey FOREIGN KEY (cs4playas_data_id)
        REFERENCES public.cs4playas_data (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT cs4playas_observed_property_fkex FOREIGN KEY (observed_property_count_key)
        REFERENCES public.cs4playas_observed_property (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE OR REPLACE FUNCTION ST_CS4_Minka_DataToGeoJson()
RETURNS jsonb AS
$BODY$
    SELECT jsonb_build_object(
        'type',     'FeatureCollection',
        'features', jsonb_agg(feature)
    )
    FROM (
      SELECT jsonb_build_object(
        'type',       'Feature',
        'id',         row.id,
        'geometry',   ST_AsGeoJSON(location)::jsonb,
        'properties', to_jsonb(row) - 'id' - 'location'
      ) AS feature
      FROM (SELECT * FROM public.cs4_minka_data) row) features;
$BODY$
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION ST_CS4_Minka_DataToGeoJsonWithLimit(lmt int)
RETURNS jsonb AS
$BODY$
    SELECT jsonb_build_object(
        'type',     'FeatureCollection',
        'features', jsonb_agg(feature)
    )
    FROM (
      SELECT jsonb_build_object(
        'type',       'Feature',
        'id',         row.id,
        'geometry',   ST_AsGeoJSON(location)::jsonb,
        'properties', to_jsonb(row) - 'id' - 'location'
      ) AS feature
      FROM (SELECT * FROM public.cs4_minka_data LIMIT $1) row) features;
$BODY$
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION ST_CS4ObservedPropertiesToJson(data_id integer)
RETURNS jsonb AS
$BODY$
      SELECT jsonb_build_object(
	  'observed_properties', jsonb_agg(property)
	  )
      FROM (
	  SELECT to_jsonb(row) as property
      FROM (SELECT o.name, c.observed_property_count FROM public.cs4playas_data_observed_property_count as c join cs4playas_observed_property as o on c.observed_property_count_key = o.id where c.cs4playas_data_id = $1) row) properties;
$BODY$
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION ST_CS4DataToGeoJson()
RETURNS jsonb AS
$BODY$
    SELECT jsonb_build_object(
        'type',     'FeatureCollection',
        'features', jsonb_agg(feature)
    )
    FROM (
      SELECT jsonb_build_object(
        'type',       'Feature',
        'id',         row.id,
        'geometry',   ST_AsGeoJSON(ST_Multi(ST_Union(initial_coordinates, final_coordinates)))::jsonb,
        'properties', to_jsonb(row) - 'id' || to_jsonb(ST_CS4ObservedPropertiesToJson(row.id))
      ) AS feature
      FROM (SELECT * FROM public.cs4playas_data) row) features;
$BODY$
LANGUAGE SQL;