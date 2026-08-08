import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSubgroupPopupComponent } from './create-subgroup-popup.component';

describe('CreateSubgroupPopupComponent', () => {
  let component: CreateSubgroupPopupComponent;
  let fixture: ComponentFixture<CreateSubgroupPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateSubgroupPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateSubgroupPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
