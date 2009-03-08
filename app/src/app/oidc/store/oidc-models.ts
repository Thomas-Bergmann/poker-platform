
export class OIDCProvider {
  /**
   * registration reference of identity provider at resource server (service)
   */
  localRef: string = "";
   /**
   * client identifier of identity provider
   */
  clientId: string = "";
  /**
   * name of identity provider to give user a choice
   */
  name: string = "";
  /**
   * identity provider URI -- get identity code (is also issuer of code)
   */
   authenticationURI: string = "";
  /**
   * identity provider URI -- get identity token
   */
   tokenURI: string = "";
  /**
   * identity provider URI -- issuer of token (could be different to tokenURI) if not set use tokenURI
   */
   tokenIssuer?: string;
  /**
   * identity provider URI -- user info () if not set don't user info
   */
   userinfoURI?: string;
   /**
   * authorization (service) URI - get access token (for resource)
   */
   authorizationURI: string = "";
}

export const EMPTY : OIDCProvider = {
  localRef: "EMPTY",
  clientId: "EMPTY",
  name:  "EMPTY",
  authenticationURI:  "",
  tokenURI: "",
  authorizationURI: "",
}
