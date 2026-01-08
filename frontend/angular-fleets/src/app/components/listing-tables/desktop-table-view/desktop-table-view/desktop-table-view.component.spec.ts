import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DesktopTableViewComponent } from './desktop-table-view.component';

describe('DesktopTableViewComponent', () => {
  let component: DesktopTableViewComponent;
  let fixture: ComponentFixture<DesktopTableViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DesktopTableViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DesktopTableViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
