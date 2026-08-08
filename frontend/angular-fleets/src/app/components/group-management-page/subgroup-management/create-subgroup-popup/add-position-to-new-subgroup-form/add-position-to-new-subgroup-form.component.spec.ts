import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPositionToNewSubgroupFormComponent } from './add-position-to-new-subgroup-form.component';

describe('AddPositionToNewSubgroupFormComponent', () => {
  let component: AddPositionToNewSubgroupFormComponent;
  let fixture: ComponentFixture<AddPositionToNewSubgroupFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddPositionToNewSubgroupFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddPositionToNewSubgroupFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
