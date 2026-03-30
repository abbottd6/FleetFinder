import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatePushSubResponsePopupComponent } from './create-push-sub-response-popup.component';

describe('CreatePushSubSuccessPopupComponent', () => {
  let component: CreatePushSubResponsePopupComponent;
  let fixture: ComponentFixture<CreatePushSubResponsePopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreatePushSubResponsePopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatePushSubResponsePopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
