import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardBotComponent } from './cardBot.component';

describe('CardBotComponent', () => {
  let component: CardBotComponent;
  let fixture: ComponentFixture<CardBotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardBotComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CardBotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
