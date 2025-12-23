import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmClearIssueComponent } from './confirm-clear-issue.component';

describe('ConfirmClearIssueComponent', () => {
  let component: ConfirmClearIssueComponent;
  let fixture: ComponentFixture<ConfirmClearIssueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfirmClearIssueComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmClearIssueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
