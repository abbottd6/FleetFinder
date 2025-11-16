import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModListingsTableComponent } from './mod-listings-table.component';

describe('ModListingsTableComponent', () => {
  let component: ModListingsTableComponent;
  let fixture: ComponentFixture<ModListingsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModListingsTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModListingsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
