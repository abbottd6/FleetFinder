import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadCrewTemplateFormComponent } from './load-crew-template-form.component';

describe('LoadCrewTemplateFormComponent', () => {
  let component: LoadCrewTemplateFormComponent;
  let fixture: ComponentFixture<LoadCrewTemplateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadCrewTemplateFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoadCrewTemplateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
