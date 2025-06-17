import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddShoppingCartDialogComponent } from './add-shopping-cart-dialog.component';

describe('AddShoppingCartDialogComponent', () => {
  let component: AddShoppingCartDialogComponent;
  let fixture: ComponentFixture<AddShoppingCartDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddShoppingCartDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddShoppingCartDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
