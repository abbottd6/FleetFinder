import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanetDropdownComponent } from './planet-dropdown.component';

describe('PlanetDropdownComponent', () => {
  let component: PlanetDropdownComponent;
  let fixture: ComponentFixture<PlanetDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PlanetDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlanetDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
