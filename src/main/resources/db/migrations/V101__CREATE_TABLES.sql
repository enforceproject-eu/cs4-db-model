CREATE TABLE IF NOT EXISTS public.cs4_playas_data
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
    CONSTRAINT cs4_playas_data_pkey PRIMARY KEY (id)
);
    
CREATE TABLE IF NOT EXISTS public.cs4_playas_observed_property
(
    id int NOT NULL,
    name character varying(255),
    CONSTRAINT datareference_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.cs4_playas_observed_property_count
(
    cs4playas_data_id integer NOT NULL,
    cs4playas_observed_property_id integer NOT NULL,
    count integer,
    CONSTRAINT cs4_playas_observed_property_count_pkey PRIMARY KEY (cs4playas_data_id, cs4playas_observed_property_id)
);

CREATE TABLE IF NOT EXISTS public.cs4_playas_data_observed_property_count
(
    cs4playas_data_id integer NOT NULL,
    observed_property_count_cs4playas_data_id integer NOT NULL,
    observed_property_count_cs4playas_observed_property_id integer NOT NULL,
    CONSTRAINT cs4_playas_data_observed_property_count_pkey PRIMARY KEY (cs4playas_data_id, observed_property_count_cs4playas_data_id, observed_property_count_cs4playas_observed_property_id),
    CONSTRAINT uk4082an539eed81dpt6orqumed UNIQUE (observed_property_count_cs4playas_data_id, observed_property_count_cs4playas_observed_property_id),
    CONSTRAINT fk9vxlukxonwq7s86cqwew5j6sv FOREIGN KEY (cs4playas_data_id)
        REFERENCES public.cs4_playas_data (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkfqqrke0upl41tn5mpwro034j4 FOREIGN KEY (observed_property_count_cs4playas_data_id, observed_property_count_cs4playas_observed_property_id)
        REFERENCES public.cs4_playas_observed_property_count (cs4playas_data_id, cs4playas_observed_property_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);