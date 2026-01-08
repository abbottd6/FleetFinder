import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmReportComponent } from './confirm-report.component';

describe('ConfirmReportComponent', () => {
  let component: ConfirmReportComponent;
  let fixture: ComponentFixture<ConfirmReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfirmReportComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
