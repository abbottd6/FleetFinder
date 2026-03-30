import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NothingHerePageComponent } from './nothing-here-page.component';

describe('NothingHerePageComponent', () => {
  let component: NothingHerePageComponent;
  let fixture: ComponentFixture<NothingHerePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NothingHerePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NothingHerePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
