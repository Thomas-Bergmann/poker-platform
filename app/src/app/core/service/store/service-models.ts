export interface ServiceState {
  serviceEndpoint: ServiceEndpoint,
  serviceErrors : ServiceError[]
}

export class ServiceEndpoint {
  uri: String = "null://";
  empty()
  {
    return this;
  }
  init(uri : String)
  {
    this.uri = uri;
    return this;
  }
}

export class ServiceError {
  status: Number = 0;
  message? : String;
}
