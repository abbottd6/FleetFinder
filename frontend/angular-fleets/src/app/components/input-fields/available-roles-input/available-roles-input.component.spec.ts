import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvailableRolesInputComponent } from './available-roles-input.component';

describe('AvailableRolesInputComponent', () => {
  let component: AvailableRolesInputComponent;
  let fixture: ComponentFixture<AvailableRolesInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AvailableRolesInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AvailableRolesInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
