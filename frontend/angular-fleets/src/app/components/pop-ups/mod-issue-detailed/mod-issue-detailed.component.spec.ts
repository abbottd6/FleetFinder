import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModIssueDetailedComponent } from './mod-issue-detailed.component';

describe('ModIssueDetailedComponent', () => {
  let component: ModIssueDetailedComponent;
  let fixture: ComponentFixture<ModIssueDetailedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModIssueDetailedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModIssueDetailedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
