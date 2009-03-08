import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardSeatComponent } from './cardSeat.component';

describe('CardSeatComponent', () => {
  let component: CardSeatComponent;
  let fixture: ComponentFixture<CardSeatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardSeatComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CardSeatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
