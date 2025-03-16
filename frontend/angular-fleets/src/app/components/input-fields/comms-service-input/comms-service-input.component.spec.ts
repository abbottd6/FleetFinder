import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommsServiceInputComponent } from './comms-service-input.component';

describe('CommsServiceInputComponent', () => {
  let component: CommsServiceInputComponent;
  let fixture: ComponentFixture<CommsServiceInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommsServiceInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommsServiceInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
