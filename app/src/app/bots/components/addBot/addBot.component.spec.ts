import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddBotComponent } from './addBot.component';

describe('AddBotComponent', () => {
  let component: AddBotComponent;
  let fixture: ComponentFixture<AddBotComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddBotComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddBotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
