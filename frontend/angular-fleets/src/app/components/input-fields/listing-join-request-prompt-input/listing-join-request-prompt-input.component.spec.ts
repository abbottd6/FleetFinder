import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListingJoinRequestPromptInputComponent } from './listing-join-request-prompt-input.component';

describe('ListingJoinRequestPromptInputComponent', () => {
  let component: ListingJoinRequestPromptInputComponent;
  let fixture: ComponentFixture<ListingJoinRequestPromptInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListingJoinRequestPromptInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListingJoinRequestPromptInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
