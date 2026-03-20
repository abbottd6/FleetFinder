import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericSmallInputFieldComponent } from './generic-small-input-field.component';

describe('GenericSmallInputFieldComponent', () => {
  let component: GenericSmallInputFieldComponent;
  let fixture: ComponentFixture<GenericSmallInputFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GenericSmallInputFieldComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenericSmallInputFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
