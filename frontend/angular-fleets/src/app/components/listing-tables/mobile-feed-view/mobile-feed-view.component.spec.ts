import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileFeedViewComponent } from './mobile-feed-view.component';

describe('MobileFeedViewComponent', () => {
  let component: MobileFeedViewComponent;
  let fixture: ComponentFixture<MobileFeedViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MobileFeedViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MobileFeedViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
