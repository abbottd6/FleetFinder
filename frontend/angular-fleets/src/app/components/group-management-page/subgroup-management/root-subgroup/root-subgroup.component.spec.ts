import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RootSubgroupComponent } from './root-subgroup.component';

describe('RootSubgroupComponent', () => {
  let component: RootSubgroupComponent;
  let fixture: ComponentFixture<RootSubgroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RootSubgroupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RootSubgroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
