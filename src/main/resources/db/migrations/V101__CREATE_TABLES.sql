CREATE TABLE IF NOT EXISTS public.cs4_playas_data
(
    id uuid NOT NULL,
    location_name character varying(255),
    entity character varying(255),
    country character varying(255),
    community character varying(255),
    province character varying(255),
    municipality character varying(255),
    collection_type character varying(255),
    project character varying(255),
    monitoring_length_meters smallint,
    initial_coordinates geometry,
    final_coordinates geometry,
    cleaning_date date,
    total_items smallint,
    remarks character varying(1024),
    CONSTRAINT cs4_playas_data_pkey PRIMARY KEY (id)
);

-- Table: public.datareference

-- DROP TABLE IF EXISTS public.datareference;
    
CREATE TABLE IF NOT EXISTS public.observed_property
(
    id smallint NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    count smallint,
    CONSTRAINT datareference_pkey PRIMARY KEY (id, name)
);

-- Table: public.lagen_aggregates

-- DROP TABLE IF EXISTS public.lagen_aggregates;

CREATE TABLE IF NOT EXISTS public.data_observed_properties
(
    id uuid NOT NULL,
    observed_properties_id smallint NOT NULL,
    observed_properties_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT lagen_aggregates_pkey PRIMARY KEY (id, observed_properties_id, observed_properties_name),
    CONSTRAINT fkb4qjqlgn2mhb01j7mq54fw71o FOREIGN KEY (observed_properties_id, observed_properties_name)
        REFERENCES public.observed_property (id, name) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkpymm9tvlesk0xdfxiq6wh0qeg FOREIGN KEY (id)
        REFERENCES public.cs4_playas_data (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
