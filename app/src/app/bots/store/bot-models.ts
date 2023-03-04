export class Bot {
  globalRef: string = "";
  localRef: string = "";
  ownerRef: string = "";
  name: string = "";
  apiKey: string = "";

  initForEmpty(){}
  newBot(ownerRef : string, name : string)
  {
    this.localRef = name;
    this.ownerRef = ownerRef;
    this.name = name;
    return this;
  }
  init(globalRef: string, localRef: string, ownerRef: string, name: string, apiKey: string)
  {
    this.globalRef = globalRef;
    this.localRef = localRef;
    this.ownerRef = ownerRef;
    this.name = name;
    this.apiKey = apiKey;
    return this;
  }
}
