import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../services/user.service';
import { EditPasswordRequest, EditProfileResponse } from '../../models/user.interface';
import { MatFormFieldModule } from '@angular/material/form-field';


@Component({
  selector: 'app-edit-password-dialog',
  standalone: true,
  imports: [MatFormFieldModule, MatDialogContent, MatDialogActions],
  templateUrl: './edit-password-dialog.component.html',
  styleUrl: './edit-password-dialog.component.css'
})

export class EditPasswordDialogComponent {
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    public dialogRef: MatDialogRef<EditPasswordDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { oldPassword: string; newPassword: string }
  ) {
    this.form = this.fb.group({
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSave() {
    if (this.form.valid) {
      const request: EditPasswordRequest = this.form.value;
      this.userService.changePassword(request).subscribe({
        next: (response: EditProfileResponse) => {
          this.dialogRef.close(response);
        },
        error: (err) => {
          console.error('Error changing password:', err);
        }
      });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}