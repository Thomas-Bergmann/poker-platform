-- players
CREATE TABLE players (
	player_id bigint IDENTITY(1,1) NOT NULL,
	name_nick varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	owner_ref varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	type varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	balance int NOT NULL,
	CONSTRAINT PK_player PRIMARY KEY (player_id),
	CONSTRAINT AK_player UNIQUE (name_nick)
);
CREATE NONCLUSTERED INDEX IDX_player ON players (name_nick);
CREATE NONCLUSTERED INDEX IDX_player_owner ON players (owner_ref);

-- tables
CREATE TABLE tables (
	table_id bigint IDENTITY(1,1) NOT NULL,
	name varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	poker_variant varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	poker_limit varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	
	buyin_max int NOT NULL,
	last_gameno bigint NOT NULL,
	blind_small int NOT NULL,
	blind_big int NOT NULL,

	CONSTRAINT PK_table PRIMARY KEY (table_id),
	CONSTRAINT AK_table UNIQUE (name)
);
CREATE NONCLUSTERED INDEX IDX_table ON tables (name);

-- seats
CREATE TABLE seats (
	seat_id bigint IDENTITY(1,1) NOT NULL,
	table_id bigint NOT NULL,
	position int NOT NULL,
	player_id bigint,
	coins_on_seat int NOT NULL,
	is_out bit NOT NULL,
	CONSTRAINT PK_seat PRIMARY KEY (seat_id),
	CONSTRAINT AK_seat UNIQUE (table_id, position),
	CONSTRAINT FK_seat_player FOREIGN KEY (player_id) REFERENCES players(player_id),
	CONSTRAINT FK_seat_table FOREIGN KEY (table_id) REFERENCES tables(table_id)
);
CREATE NONCLUSTERED INDEX IDX_seat ON seats (table_id, position);

-- game events
CREATE TABLE game_events (
	game_event_id bigint IDENTITY(1,1) NOT NULL,
	table_id bigint NOT NULL,
	game_no bigint NOT NULL,
	event_no int NOT NULL,
	event_type varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	event_data varchar(4048) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	CONSTRAINT PK_game_event PRIMARY KEY (game_event_id),
	CONSTRAINT AK_game_event UNIQUE (table_id, game_no, event_no),
	CONSTRAINT FK_game_event_table FOREIGN KEY (table_id) REFERENCES tables(table_id)
);
CREATE NONCLUSTERED INDEX IDX_game_event ON game_events (table_id, game_no);
