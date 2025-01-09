import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlaystyleDropdownComponent } from './playstyle-dropdown.component';

describe('PlaystyleDropdownComponent', () => {
  let component: PlaystyleDropdownComponent;
  let fixture: ComponentFixture<PlaystyleDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PlaystyleDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlaystyleDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
