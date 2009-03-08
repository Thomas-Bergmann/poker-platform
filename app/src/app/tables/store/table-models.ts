
export class Table {
  resourceURI: string = ""; // used for service
  localRef: string = ""; // used for route
  name: string = "";
  variant: string = "";
  limit: string = "";
  maxRebuy: number = 0;

  initForEmtpy(){}
  init(resourceURI: string, name : string, variant : string, limit: string, maxRebuy: number)
  {
    this.resourceURI = resourceURI;
    this.localRef = name;
    this.name = name;
    this.variant = variant;
    this.limit = limit;
    this.maxRebuy = maxRebuy;
    return this;
  }
}
