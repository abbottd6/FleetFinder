import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModActionsTableComponent } from './mod-actions-table.component';

describe('ModActionsTableComponent', () => {
  let component: ModActionsTableComponent;
  let fixture: ComponentFixture<ModActionsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModActionsTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModActionsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
