import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DontShowMeAgainPopup } from './dont-show-me-again-popup';

describe('HideHowToPopupComponent', () => {
  let component: DontShowMeAgainPopup;
  let fixture: ComponentFixture<DontShowMeAgainPopup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DontShowMeAgainPopup]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DontShowMeAgainPopup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
