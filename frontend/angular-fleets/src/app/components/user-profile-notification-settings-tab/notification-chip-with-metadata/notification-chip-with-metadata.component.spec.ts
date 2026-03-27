import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationChipWithMetadataComponent } from './notification-chip-with-metadata.component';

describe('NotificationChipWithLinkContentComponent', () => {
  let component: NotificationChipWithMetadataComponent;
  let fixture: ComponentFixture<NotificationChipWithMetadataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationChipWithMetadataComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationChipWithMetadataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
