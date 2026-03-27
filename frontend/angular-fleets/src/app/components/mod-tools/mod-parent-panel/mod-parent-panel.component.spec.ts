import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { ModParentPanelComponent } from './mod-parent-panel.component';

describe('ModParentPanelComponent', () => {
  let component: ModParentPanelComponent;
  let fixture: ComponentFixture<ModParentPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModParentPanelComponent],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ModParentPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('listingForModal EventEmitter should be defined', () => {
    expect(component.listingForModal).toBeDefined();
  });
});
