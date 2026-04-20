import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AbstractNumStringDropdownComponent } from './abstract-num-string-dropdown.component';

describe('AbstractNumStringDropdownComponent', () => {
  let component: AbstractNumStringDropdownComponent;
  let fixture: ComponentFixture<AbstractNumStringDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AbstractNumStringDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AbstractNumStringDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
