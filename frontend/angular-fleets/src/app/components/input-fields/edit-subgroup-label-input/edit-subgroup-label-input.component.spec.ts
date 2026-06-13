import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSubgroupLabelInputComponent } from './edit-subgroup-label-input.component';

describe('EditSubgroupLabelInputComponent', () => {
  let component: EditSubgroupLabelInputComponent;
  let fixture: ComponentFixture<EditSubgroupLabelInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditSubgroupLabelInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditSubgroupLabelInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
