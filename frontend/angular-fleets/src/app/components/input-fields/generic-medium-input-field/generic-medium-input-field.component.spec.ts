import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericMediumInputFieldComponent } from './generic-medium-input-field.component';

describe('GenericMediumInputFieldComponent', () => {
  let component: GenericMediumInputFieldComponent;
  let fixture: ComponentFixture<GenericMediumInputFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GenericMediumInputFieldComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenericMediumInputFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
