import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnvironmentDropdownComponent } from './environment-dropdown.component';

describe('EnvironmentDropdownComponent', () => {
  let component: EnvironmentDropdownComponent;
  let fixture: ComponentFixture<EnvironmentDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EnvironmentDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnvironmentDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
