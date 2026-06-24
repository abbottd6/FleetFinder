import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateOrEditPositionPopupComponent } from './create-or-edit-position-popup.component';

describe('CreateOrEditPositionPopupComponent', () => {
  let component: CreateOrEditPositionPopupComponent;
  let fixture: ComponentFixture<CreateOrEditPositionPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateOrEditPositionPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateOrEditPositionPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
