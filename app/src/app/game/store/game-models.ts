
export class Game {
  resourceURI : string = "";
  tableResourceURI : string = "";
  gameno: string = "";
  boardCards : string = "";
  coinsPot: number = 0;
  coinsBigBlind :  number = 0;

  initForEmtpy(){ return this; }
  init(resourceURI: string, tableResourceURI: string, gameno: string, boardCards: string, coinsPot: number, coinsBigBlind : number)
  {
    this.resourceURI = resourceURI;
    this.tableResourceURI = tableResourceURI;
    this.gameno = gameno;
    this.boardCards = boardCards;
    this.coinsPot = coinsPot;
    this.coinsBigBlind = coinsBigBlind;
    return this;
  }
  isEmpty(): boolean {
    return this.gameno == "";
  }
}
