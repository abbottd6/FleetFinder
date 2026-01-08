import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModIssuesViewComponent } from './mod-issues-view.component';

describe('ModIssuesViewComponent', () => {
  let component: ModIssuesViewComponent;
  let fixture: ComponentFixture<ModIssuesViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModIssuesViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModIssuesViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
