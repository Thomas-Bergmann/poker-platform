export class Seat {
  resourceURI : string = "";
  position: number = 99;
  playerRef: string = "";
  tableResourceURI : string = "";
  coinsOnSeat: number = 0;
  coinsInPlay: number = 0;
  cards: string = "";
  isallin: boolean = false;
  onButton: boolean = false;
  hasAction: boolean = false;
  name: string = "";
  isOutSeat: boolean = false;
  isOutGame: boolean = false;
  rank: string = "(no)";

  initForEmtpy(){}
  initForAdd(tableResourceURI: string, playerRef : string, buyin : number)
  {
    this.playerRef = playerRef;
    this.tableResourceURI = tableResourceURI;
    this.coinsOnSeat = buyin;
    return this;
  }
  sitOut(sitout: boolean)
  {
    return new Seat().initForGame(this.resourceURI, this.tableResourceURI, this.position, this.playerRef, sitout, sitout, this.coinsOnSeat, this.coinsInPlay, this.name, this.cards, this.isallin, this.onButton, this.hasAction, this.rank);
  }
  setCoinsOnSeat(coinsOnSeat: number): Seat {
    return new Seat().initForGame(this.resourceURI, this.tableResourceURI, this.position, this.playerRef, this.isOutGame, this.isOutSeat, coinsOnSeat, this.coinsInPlay, this.name, this.cards, this.isallin, this.onButton, this.hasAction, this.rank);
  }
  initForTable(resourceURI: string, tableResourceURI: string, position: number, playerRef : string, isOutGame: boolean, isOutSeat: boolean, 
    coinsSeat:number, name: string)
  {
    this.resourceURI = resourceURI;
    this.tableResourceURI = tableResourceURI;
    this.position = position;
    this.playerRef = playerRef;
    this.coinsOnSeat = coinsSeat;
    this.coinsInPlay = NaN;
    this.isOutGame = isOutGame;
    this.isOutSeat = isOutSeat;
    this.name = name;
    this.cards = "";
    this.isallin = false;
    this.onButton = false;
    this.hasAction = false;
    this.rank = "(no)";
    return this;
  }
  initForGame(resourceURI: string, tableResourceURI: string, position: number, playerRef : string, isOutGame: boolean, isOutSeat: boolean, 
    coinsSeat:number, coinsInPlay:number,
    name: string, cards: string,
    allin:boolean, onButton: boolean, hasAction:boolean, rank: string
    )
  {
    this.resourceURI = resourceURI;
    this.tableResourceURI = tableResourceURI;
    this.position = position;
    this.playerRef = playerRef;
    this.coinsOnSeat = coinsSeat;
    this.coinsInPlay = coinsInPlay;
    this.isOutGame = isOutGame;
    this.isOutSeat = isOutSeat;
    this.name = name;
    this.cards = cards;
    this.isallin = allin;
    this.onButton = onButton;
    this.hasAction = hasAction;
    this.rank = rank;
    return this;
  }
}

export class Raise {
  seat : Seat = new Seat();
  betTo : number = 0;
  init(seat : Seat, betTo : number)
  {
    this.seat = seat;
    this.betTo = betTo;
    return this;
  }
}
