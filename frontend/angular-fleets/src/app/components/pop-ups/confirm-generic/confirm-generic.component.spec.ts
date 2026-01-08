import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmGenericComponent } from './confirm-generic.component';

describe('ConfirmGenericComponent', () => {
  let component: ConfirmGenericComponent;
  let fixture: ComponentFixture<ConfirmGenericComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfirmGenericComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmGenericComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
