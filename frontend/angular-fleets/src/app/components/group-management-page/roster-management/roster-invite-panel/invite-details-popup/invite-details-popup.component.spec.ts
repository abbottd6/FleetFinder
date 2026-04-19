import { ComponentFixture, TestBed } from '@angular/core/testing';

import InviteDetailsPopupComponent from './invite-details-popup.component';

describe('InviteDetailsPopupComponent', () => {
  let component: InviteDetailsPopupComponent;
  let fixture: ComponentFixture<InviteDetailsPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InviteDetailsPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InviteDetailsPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
