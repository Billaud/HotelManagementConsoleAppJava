-- ============================================================
-- Το script γράφτηκε με τρόπο ώστε να είναι idempotent και αποτελεί το σχήμα της βάσης
-- Επίσης υπάρχουν και indexes για performance και κασκοδικές εντολές

-- DROP first (reverse order — child tables before parents)
-- because you cannot drop a table that another FK references
-- ============================================================
DROP TABLE IF EXISTS RESERVATION;
DROP TABLE IF EXISTS ROOM;
DROP TABLE IF EXISTS CUSTOMER;

-- ============================================================
-- ΠΙΝΑΚΑΣ 1: ROOM
-- ============================================================
CREATE TABLE IF NOT EXISTS ROOM (
    roomId        INT           NOT NULL AUTO_INCREMENT,
    roomNumber    INT           NOT NULL,
    Τype          VARCHAR(20)   NOT NULL,
    pricePerNight DECIMAL(10,2) NOT NULL,
    available     BOOLEAN       NOT NULL DEFAULT TRUE,

    PRIMARY KEY (roomId),
    CONSTRAINT uq_room_number      UNIQUE  (roomNumber),
    CONSTRAINT chk_room_type       CHECK   (Type IN ('SINGLE','DOUBLE','FAMILY','SUITE')),
    CONSTRAINT chk_price_positive  CHECK   (pricePerNight > 0)
    );

-- ============================================================
-- ΠΙΝΑΚΑΣ 2: CUSTOMER
-- ============================================================
CREATE TABLE IF NOT EXISTS CUSTOMER (
    customerId  INT           NOT NULL AUTO_INCREMENT,
    fullName    VARCHAR(100)  NOT NULL,
    email       VARCHAR(100)  NOT NULL,
    phone       VARCHAR(20)   NOT NULL,

    PRIMARY KEY (customerId),
    CONSTRAINT uq_customer_email UNIQUE (email));

-- ============================================================
-- ΠΙΝΑΚΑΣ 3: RESERVATION
-- ============================================================

CREATE TABLE IF NOT EXISTS RESERVATION (
    reservationId  INT         NOT NULL AUTO_INCREMENT,
    customerId     INT         NOT NULL,
    roomId         INT         NOT NULL,
    checkIn        DATE        NOT NULL,
    checkOut       DATE        NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    PRIMARY KEY (reservationId),

    CONSTRAINT fk_reservation_customer
    FOREIGN KEY (customerId) REFERENCES CUSTOMER(customerId)
    ON DELETE RESTRICT,

    CONSTRAINT fk_reservation_room
    FOREIGN KEY (roomId) REFERENCES ROOM(roomId)
    ON DELETE RESTRICT,

    CONSTRAINT chk_dates   CHECK (checkOut > checkIn),
    CONSTRAINT chk_status  CHECK (status IN ('ACTIVE','CANCELLED'));

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_reservation_customer ON RESERVATION(customerId);
CREATE INDEX IF NOT EXISTS idx_reservation_dates    ON RESERVATION(checkIn, checkOut);