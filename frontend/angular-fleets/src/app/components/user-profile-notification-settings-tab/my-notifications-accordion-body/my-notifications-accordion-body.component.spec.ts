import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyNotificationsAccordionBodyComponent } from './my-notifications-accordion-body.component';

describe('NotificationAccordionBodyComponent', () => {
  let component: MyNotificationsAccordionBodyComponent;
  let fixture: ComponentFixture<MyNotificationsAccordionBodyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MyNotificationsAccordionBodyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyNotificationsAccordionBodyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
