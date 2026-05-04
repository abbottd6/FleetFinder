import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrewSubgroupComponent } from './crew-subgroup.component';

describe('CrewSubgroupComponent', () => {
  let component: CrewSubgroupComponent;
  let fixture: ComponentFixture<CrewSubgroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrewSubgroupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CrewSubgroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
