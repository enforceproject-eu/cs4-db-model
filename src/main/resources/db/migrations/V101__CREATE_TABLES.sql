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
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    count int,
    CONSTRAINT datareference_pkey PRIMARY KEY (id, name)
);


CREATE TABLE IF NOT EXISTS public.cs4_playas_data_observed_properties
(
    id int NOT NULL,
    observed_properties_id int NOT NULL,
    observed_properties_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT lagen_aggregates_pkey PRIMARY KEY (id, observed_properties_id, observed_properties_name),
    CONSTRAINT fkb4qjqlgn2mhb01j7mq54fw71o FOREIGN KEY (observed_properties_id, observed_properties_name)
        REFERENCES public.cs4_playas_observed_property (id, name) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkpymm9tvlesk0xdfxiq6wh0qeg FOREIGN KEY (id)
        REFERENCES public.cs4_playas_data (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
