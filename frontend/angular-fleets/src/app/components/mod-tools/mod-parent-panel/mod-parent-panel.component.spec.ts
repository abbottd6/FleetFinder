import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModParentPanelComponent } from './mod-parent-panel.component';

describe('ModParentPanelComponent', () => {
  let component: ModParentPanelComponent;
  let fixture: ComponentFixture<ModParentPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModParentPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModParentPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
