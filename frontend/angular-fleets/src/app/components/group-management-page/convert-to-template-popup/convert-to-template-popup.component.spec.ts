import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvertToTemplatePopupComponent } from './convert-to-template-popup.component';

describe('ConvertToTemplatePopupComponent', () => {
  let component: ConvertToTemplatePopupComponent;
  let fixture: ComponentFixture<ConvertToTemplatePopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConvertToTemplatePopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConvertToTemplatePopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
