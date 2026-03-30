import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatePushSubSuccessPopupComponent } from './create-push-sub-success-popup.component';

describe('CreatePushSubSuccessPopupComponent', () => {
  let component: CreatePushSubSuccessPopupComponent;
  let fixture: ComponentFixture<CreatePushSubSuccessPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreatePushSubSuccessPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatePushSubSuccessPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
