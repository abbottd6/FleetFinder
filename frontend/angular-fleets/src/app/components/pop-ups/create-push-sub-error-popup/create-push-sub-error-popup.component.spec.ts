import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatePushSubErrorPopupComponent } from './create-push-sub-error-popup.component';

describe('CreatePushSubErrorPopupComponent', () => {
  let component: CreatePushSubErrorPopupComponent;
  let fixture: ComponentFixture<CreatePushSubErrorPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreatePushSubErrorPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatePushSubErrorPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
