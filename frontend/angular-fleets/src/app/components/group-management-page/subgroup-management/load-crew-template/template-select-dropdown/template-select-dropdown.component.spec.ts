import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TemplateSelectDropdownComponent } from './template-select-dropdown.component';

describe('TemplateSelectDropdownComponent', () => {
  let component: TemplateSelectDropdownComponent;
  let fixture: ComponentFixture<TemplateSelectDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TemplateSelectDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TemplateSelectDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
