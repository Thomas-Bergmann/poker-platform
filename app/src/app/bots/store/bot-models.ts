
export class Bot {
  globalRef: string = "";
  localRef: string = "";
  ownerRef: string = "";
  name: string = "";
  type: string = "";

  initForEmpty(){}
  newBot(ownerRef : string, name : string)
  {
    this.localRef = name;
    this.ownerRef = ownerRef;
    this.name = name;
    this.type = "COMPUTE";
    return this;
  }
  init(globalRef: string, localRef: string, ownerRef: string, name : string, type: string)
  {
    this.globalRef = globalRef;
    this.localRef = localRef;
    this.ownerRef = ownerRef;
    this.name = name;
    this.type = type;
    return this;
  }
}
