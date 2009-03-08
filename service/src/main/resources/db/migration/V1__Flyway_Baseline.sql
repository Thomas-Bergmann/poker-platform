CREATE TABLE users (
	user_id bigint IDENTITY(1,1) NOT NULL,
	user_ref varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	active bit NOT NULL,
	email varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	name_full varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	name_first varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	name_last varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	name_nick varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	locale varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	time_zone varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	CONSTRAINT PK_users PRIMARY KEY (user_id),
	CONSTRAINT AK_users UNIQUE (user_ref)
);
CREATE UNIQUE NONCLUSTERED INDEX IDX_users ON users (user_ref);

CREATE TABLE oidc_id_provider (
	identityprovider_id bigint IDENTITY(1,1) NOT NULL,
	idp_ref varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	name varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	token_validity bigint NOT NULL,

	public_clientid varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	public_auth_uri varchar(4095) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	public_token_uri varchar(4095) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	public_token_issuer varchar(4095) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	public_userinfo_uri varchar(4095) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	access_token_secret varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	openid_config_uri varchar(4095) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NULL,
	private_clientid varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL DEFAULT 'empty',
	private_client_secret varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL DEFAULT ' ',
	CONSTRAINT PK_oidc_id_provider PRIMARY KEY (identityprovider_id),
	CONSTRAINT AK_oidc_id_provider UNIQUE (idp_ref)
);
CREATE NONCLUSTERED INDEX IDX_oidc_id_provider ON oidc_id_provider (idp_ref);

CREATE TABLE oidc_id_provider_map (
	identityprovider_map_id bigint IDENTITY(1,1) NOT NULL,
	identityprovider_id bigint NOT NULL,
	subject varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,
	user_ref varchar(255) COLLATE Latin1_General_100_CI_AI_SC_UTF8 NOT NULL,

	CONSTRAINT PK_oidc_id_provider_map PRIMARY KEY (identityprovider_map_id),
	CONSTRAINT AK_oidc_id_provider_map UNIQUE (identityprovider_id, subject)
);
CREATE NONCLUSTERED INDEX IDX_oidc_id_provider_map ON oidc_id_provider_map (identityprovider_id);
