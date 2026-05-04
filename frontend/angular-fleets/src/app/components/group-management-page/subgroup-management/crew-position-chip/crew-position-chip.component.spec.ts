import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrewPositionChipComponent } from './crew-position-chip.component';

describe('CrewPositionChipComponent', () => {
  let component: CrewPositionChipComponent;
  let fixture: ComponentFixture<CrewPositionChipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrewPositionChipComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CrewPositionChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
