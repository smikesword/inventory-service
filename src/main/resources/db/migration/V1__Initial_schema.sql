CREATE TABLE Tag(
    id      BIGSERIAL PRIMARY KEY NOT NULL,
    key_tag VARCHAR(255) NOT NULL,
    value   VARCHAR(255),
    UNIQUE(key_tag, value)
);

CREATE TABLE Item(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    price           float8 NOT NULL,
    CONSTRAINT positive_price check (price > 0),
    date_bought     date DEFAULT CURRENT_DATE,
    expiration_date date,
    CONSTRAINT item_not_expired check (expiration_date > CURRENT_DATE),
    quantity        smallint DEFAULT 1,
    quantity_unit   VARCHAR(2),
    UNIQUE(name, expiration_date, quantity_unit)
);

CREATE TABLE item_tags (
   item_id BIGINT NOT NULL REFERENCES Item(id) ON DELETE CASCADE,
   tag_id  BIGINT NOT NULL REFERENCES Tag(id) ON DELETE CASCADE,
   PRIMARY KEY (item_id, tag_id)
);
