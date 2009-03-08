
export class Player {
  globalRef: string = "";
  localRef: string = "";
  ownerRef: string = "";
  name: string = "";

  initForEmpty(){}
  init(globalRef: string, localRef: string, ownerRef: string, name : string)
  {
    this.globalRef = globalRef;
    this.localRef = localRef;
    this.ownerRef = ownerRef;
    this.name = name;
    return this;
  }
}
