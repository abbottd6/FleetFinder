import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServerDropdownComponent } from './server-dropdown.component';

describe('ServerDropdownComponent', () => {
  let component: ServerDropdownComponent;
  let fixture: ComponentFixture<ServerDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServerDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServerDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
