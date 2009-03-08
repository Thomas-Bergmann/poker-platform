import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemPlayerComponent } from './itemPlayer.component';

describe('ItemPlayerComponent', () => {
  let component: ItemPlayerComponent;
  let fixture: ComponentFixture<ItemPlayerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ItemPlayerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ItemPlayerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
