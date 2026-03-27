import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmDelinkDiscordPopupComponent } from './confirm-delink-discord-popup.component';

describe('ConfirmDelinkDiscordPopupComponent', () => {
  let component: ConfirmDelinkDiscordPopupComponent;
  let fixture: ComponentFixture<ConfirmDelinkDiscordPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfirmDelinkDiscordPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmDelinkDiscordPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
