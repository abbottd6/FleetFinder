import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationOptionsAccordionComponent } from './notification-options-accordion.component';

describe('NotificationOptionsAccordionComponent', () => {
  let component: NotificationOptionsAccordionComponent;
  let fixture: ComponentFixture<NotificationOptionsAccordionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationOptionsAccordionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationOptionsAccordionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
