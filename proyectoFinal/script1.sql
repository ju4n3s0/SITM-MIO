    -- -- =============================================================================
    -- -- Esquema SITM-MIO: líneas, paradas y paradas-por-línea (linestops)
    -- -- Compatible con los CSV mostrados:
    -- --   lines-241.csv:   "LINEID","PLANVERSIONID","SHORTNAME","DESCRIPTION","PLANVERSIONID","ACTIVATIONDATE","CREATIONDATE"
    -- --   linestops-241.csv:"LINESTOPID","STOPSEQUENCE","ORIENTATION","LINEID","STOPID","PLANVERSIONID","LINEVARIANT","REGISTERDATE","LINEVARIANTTYPE","PLANVERSIONID","ACTIVATIONDATE","CREATIONDATE"
    -- --   stops-241.csv:   "STOPID","PLANVERSIONID","SHORTNAME","LONGNAME","GPS_X","GPS_Y","DECIMALLONGITUDE","DECIMALLATITUDE","PLANVERSIONID","ACTIVATIONDATE","CREATIONDATE"
    -- -- Nota: los CSV traen columnas duplicadas llamadas "PLANVERSIONID"; aquí las mapeamos
    -- --       con *_DUP para mantener la alineación posicional y poder hacer COPY sin problemas.
    -- -- =============================================================================

    -- -- Opcional: crea el esquema dedicado
    -- CREATE SCHEMA IF NOT EXISTS mio;
    -- SET search_path TO mio;

    -- -- ============================================================================
    -- -- 1) Tablas RAW (staging) con tipos genéricos y todas las columnas tal cual CSV
    -- --    Sugerencia: mantener datos exactamente como llegan; luego normalizamos.
    -- -- ============================================================================

    -- DROP TABLE IF EXISTS raw_lines       CASCADE;
    -- DROP TABLE IF EXISTS raw_linestops   CASCADE;
    -- DROP TABLE IF EXISTS raw_stops       CASCADE;

    -- CREATE TABLE raw_lines (
    -- lineid             BIGINT,
    -- planversionid      BIGINT,
    -- shortname          TEXT,
    -- description        TEXT,
    -- planversionid_dup  BIGINT,                  -- columna duplicada en el CSV
    -- activationdate     TIMESTAMP(3),
    -- creationdate       TIMESTAMP(3)
    -- );

    -- CREATE TABLE raw_linestops (
    -- linestopid         BIGINT,
    -- stopsequence       INTEGER,
    -- orientation        SMALLINT,                -- suele ser 0/1
    -- lineid             BIGINT,
    -- stopid             BIGINT,
    -- planversionid      BIGINT,
    -- linevariant        INTEGER,
    -- registerdate       TIMESTAMP(3),
    -- linevarianttype    SMALLINT,
    -- planversionid_dup  BIGINT,                  -- columna duplicada en el CSV
    -- activationdate     TIMESTAMP(3),
    -- creationdate       TIMESTAMP(3)
    -- );

    -- CREATE TABLE raw_stops (
    -- stopid             BIGINT,
    -- planversionid      BIGINT,
    -- shortname          TEXT,
    -- longname           TEXT,
    -- gps_x              BIGINT,                  -- entero crudo (p. ej. coordenada proyectada)
    -- gps_y              BIGINT,
    -- decimallongitude   DOUBLE PRECISION,
    -- decimallatitude    DOUBLE PRECISION,
    -- planversionid_dup  BIGINT,                  -- columna duplicada en el CSV
    -- activationdate     TIMESTAMP(3),
    -- creationdate       TIMESTAMP(3)
    -- );

    -- -- ============================================================================
    -- -- 2) Tablas NORMALIZADAS
    -- -- ============================================================================

    -- DROP TABLE IF EXISTS linestops CASCADE;
    -- DROP TABLE IF EXISTS lines     CASCADE;
    -- DROP TABLE IF EXISTS stops     CASCADE;

    -- -- Líneas
    -- CREATE TABLE lines (
    -- lineid         BIGINT PRIMARY KEY,
    -- planversionid  BIGINT NOT NULL,
    -- shortname      TEXT   NOT NULL,
    -- description    TEXT,
    -- activationdate TIMESTAMP(3),
    -- creationdate   TIMESTAMP(3)
    -- );

    -- -- Paradas
    -- CREATE TABLE stops (
    -- stopid             BIGINT PRIMARY KEY,
    -- planversionid      BIGINT NOT NULL,
    -- shortname          TEXT   NOT NULL,
    -- longname           TEXT,
    -- gps_x              BIGINT,               -- crudo por si se requiere
    -- gps_y              BIGINT,
    -- decimallongitude   DOUBLE PRECISION,     -- WGS84 aprox.
    -- decimallatitude    DOUBLE PRECISION,
    -- activationdate     TIMESTAMP(3),
    -- creationdate       TIMESTAMP(3)
    -- );

    -- -- Paradas por línea (ordenadas)
    -- CREATE TABLE linestops (
    -- linestopid      BIGINT PRIMARY KEY,
    -- lineid          BIGINT NOT NULL REFERENCES lines(lineid) ON DELETE CASCADE,
    -- stopid          BIGINT NOT NULL REFERENCES stops(stopid) ON DELETE CASCADE,
    -- stopsequence    INTEGER NOT NULL,        -- 1..N en la variante
    -- orientation     SMALLINT NOT NULL,       -- 0 ida, 1 regreso (asumido)
    -- planversionid   BIGINT NOT NULL,
    -- linevariant     INTEGER NOT NULL,        -- variante de línea
    -- registerdate    TIMESTAMP(3),
    -- linevarianttype SMALLINT,
    -- activationdate  TIMESTAMP(3),
    -- creationdate    TIMESTAMP(3),
    -- -- Evita duplicados de posición en una variante-orientación
    -- CONSTRAINT linestops_uq UNIQUE (lineid, linevariant, orientation, stopsequence)
    -- );

    -- -- ============================================================================
    -- -- 3) Cargas desde RAW a NORMALIZADO
    -- --    (puedes aplicar filtros por planversionid si deseas aislar la 241)
    -- -- ============================================================================

    -- -- Líneas
    -- INSERT INTO lines (lineid, planversionid, shortname, description, activationdate, creationdate)
    -- SELECT DISTINCT
    -- rl.lineid,
    -- COALESCE(rl.planversionid, rl.planversionid_dup) AS planversionid,
    -- NULLIF(TRIM(rl.shortname), '')                   AS shortname,
    -- NULLIF(TRIM(rl.description), '')                 AS description,
    -- rl.activationdate,
    -- rl.creationdate
    -- FROM raw_lines rl
    -- WHERE rl.lineid IS NOT NULL;

    -- -- Paradas
    -- INSERT INTO stops (stopid, planversionid, shortname, longname, gps_x, gps_y,
    --                 decimallongitude, decimallatitude, activationdate, creationdate)
    -- SELECT DISTINCT
    -- rs.stopid,
    -- COALESCE(rs.planversionid, rs.planversionid_dup) AS planversionid,
    -- NULLIF(TRIM(rs.shortname), '')                   AS shortname,
    -- NULLIF(TRIM(rs.longname), '')                    AS longname,
    -- rs.gps_x,
    -- rs.gps_y,
    -- rs.decimallongitude,
    -- rs.decimallatitude,
    -- rs.activationdate,
    -- rs.creationdate
    -- FROM raw_stops rs
    -- WHERE rs.stopid IS NOT NULL;

    -- -- Linestops
    -- INSERT INTO linestops (linestopid, lineid, stopid, stopsequence, orientation,
    --                     planversionid, linevariant, registerdate, linevarianttype,
    --                     activationdate, creationdate)
    -- SELECT
    -- rls.linestopid,
    -- rls.lineid,
    -- rls.stopid,
    -- rls.stopsequence,
    -- rls.orientation,
    -- COALESCE(rls.planversionid, rls.planversionid_dup) AS planversionid,
    -- rls.linevariant,
    -- rls.registerdate,
    -- rls.linevarianttype,
    -- rls.activationdate,
    -- rls.creationdate
    -- FROM raw_linestops rls
    -- WHERE rls.linestopid IS NOT NULL;

    -- -- ============================================================================
    -- -- 4) Índices útiles
    -- -- ============================================================================
    -- CREATE INDEX IF NOT EXISTS idx_lines_shortname             ON lines  (shortname);
    -- CREATE INDEX IF NOT EXISTS idx_stops_shortname             ON stops  (shortname);
    -- CREATE INDEX IF NOT EXISTS idx_linestops_line_variant_seq  ON linestops (lineid, linevariant, orientation, stopsequence);
    -- CREATE INDEX IF NOT EXISTS idx_linestops_stopid            ON linestops (stopid);

    -- -- ============================================================================
    -- -- 5) Vista de ARCO entre paradas consecutivas por (lineid, linevariant, orientation)
    -- --    from_stop -> to_stop con el número de secuencia
    -- -- ============================================================================
    -- DROP VIEW IF EXISTS v_line_arcs;
    -- CREATE VIEW v_line_arcs AS
    -- WITH ordered AS (
    -- SELECT
    --     ls.lineid,
    --     ls.linevariant,
    --     ls.orientation,
    --     ls.stopsequence,
    --     ls.stopid,
    --     LEAD(ls.stopid) OVER (
    --     PARTITION BY ls.lineid, ls.linevariant, ls.orientation
    --     ORDER BY ls.stopsequence
    --     ) AS next_stopid
    -- FROM linestops ls
    -- )
    -- SELECT
    -- o.lineid,
    -- o.linevariant,
    -- o.orientation,
    -- o.stopsequence       AS from_sequence,
    -- o.stopid             AS from_stopid,
    -- o.next_stopid        AS to_stopid
    -- FROM ordered o
    -- WHERE o.next_stopid IS NOT NULL;

    -- -- ============================================================================
    -- -- 6) Ejemplos de carga desde CSV a tablas RAW (ajusta rutas)
    -- --    Nota: COPY con HEADER ignora los nombres del encabezado; usa posiciones.
    -- --    Por eso funciona aun con columnas repetidas "PLANVERSIONID".
    -- --    Si usas psql local al servidor: COPY ... FROM '/ruta/abs/...'
    -- --    Si estás en cliente psql: usa \copy ... FROM 'ruta'
    -- -- ============================================================================

    -- -- Opción A: usando \copy (cliente), edita las rutas:
    -- -- \copy mio.raw_lines      FROM '/ruta/a/lines-241.csv'      CSV HEADER
    -- -- \copy mio.raw_linestops  FROM '/ruta/a/linestops-241.csv'  CSV HEADER
    -- -- \copy mio.raw_stops      FROM '/ruta/a/stops-241.csv'      CSV HEADER

    -- -- Opción B: si estás dentro del servidor y el archivo es accesible:
    -- -- COPY mio.raw_lines      FROM '/ruta/a/lines-241.csv'      CSV HEADER;
    -- -- COPY mio.raw_linestops  FROM '/ruta/a/linestops-241.csv'  CSV HEADER;
    -- -- COPY mio.raw_stops      FROM '/ruta/a/stops-241.csv'      CSV HEADER;

    -- -- Tras cargar en RAW, ejecuta otra vez únicamente la sección 3) para poblar normalizadas
    -- -- o reemplaza los INSERT por INSERT ... ON CONFLICT si harás cargas incrementales.

    -- -- ============================================================================
    -- -- 7) Checks suaves (opcionales)
    -- -- ============================================================================
    -- -- Orientación 0/1
    -- ALTER TABLE linestops
    -- ADD CONSTRAINT linestops_orientation_chk CHECK (orientation IN (0,1));

    -- -- Secuencia positiva
    -- ALTER TABLE linestops
    -- ADD CONSTRAINT linestops_seq_chk CHECK (stopsequence > 0);

    -- -- ============================================================================
    -- -- Fin del script
    -- -- ============================================================================