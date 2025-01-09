import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExperienceDropdownComponent } from './experience-dropdown.component';

describe('ExperienceDropdownComponent', () => {
  let component: ExperienceDropdownComponent;
  let fixture: ComponentFixture<ExperienceDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExperienceDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExperienceDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
