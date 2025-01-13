import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DesiredPartySizeDropdownComponent } from './desired-party-size-dropdown.component';

describe('DesiredPartySizeDropdownComponent', () => {
  let component: DesiredPartySizeDropdownComponent;
  let fixture: ComponentFixture<DesiredPartySizeDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DesiredPartySizeDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DesiredPartySizeDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
