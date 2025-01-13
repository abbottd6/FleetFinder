import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentPartySizeDropdownComponent } from './current-party-size-dropdown.component';

describe('PartySizeDropdownComponent', () => {
  let component: CurrentPartySizeDropdownComponent;
  let fixture: ComponentFixture<CurrentPartySizeDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CurrentPartySizeDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentPartySizeDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
