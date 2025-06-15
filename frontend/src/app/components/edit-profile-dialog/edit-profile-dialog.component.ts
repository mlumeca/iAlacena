import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogContent, MatDialogActions, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserService } from '../../services/user.service';
import { EditProfileRequest, EditProfileResponse } from '../../models/user.interface';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-edit-profile-dialog',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, MatDialogContent, MatDialogActions, CommonModule],
  templateUrl: './edit-profile-dialog.component.html',
  styleUrl: './edit-profile-dialog.component.css'
})
export class EditProfileDialogComponent {
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    public dialogRef: MatDialogRef<EditProfileDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { username: string; email: string }
  ) {
    this.form = this.fb.group({
      username: [data.username, [Validators.required, Validators.minLength(3)]],
      email: [data.email, [Validators.required, Validators.email]]
    });
  }

  onSave() {
    if (this.form.valid) {
      const request: EditProfileRequest = this.form.value;
      this.userService.editUserProfile(request).subscribe({
        next: (response: EditProfileResponse) => {
          this.dialogRef.close(response);
        },
        error: (err) => {
          console.error('Error updating profile:', err);
        }
      });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}