import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchInputAutoCompleteComponent } from './search-input-auto-complete.component';

describe('SearchInputAutoCompleteComponent', () => {
  let component: SearchInputAutoCompleteComponent;
  let fixture: ComponentFixture<SearchInputAutoCompleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearchInputAutoCompleteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchInputAutoCompleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
