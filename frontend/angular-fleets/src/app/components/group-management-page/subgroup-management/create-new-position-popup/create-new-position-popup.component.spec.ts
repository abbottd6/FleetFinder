import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateNewPositionPopupComponent } from './create-new-position-popup.component';

describe('CreateNewPositionPopupComponent', () => {
  let component: CreateNewPositionPopupComponent;
  let fixture: ComponentFixture<CreateNewPositionPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateNewPositionPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateNewPositionPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
