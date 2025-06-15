import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogContent, MatDialogActions } from '@angular/material/dialog';
import { UserService } from '../../services/user.service';
import { PostAvatarRequest, PostAvatarResponse } from '../../models/user.interface';

@Component({
  selector: 'app-edit-avatar-dialog',
  standalone: true,
  imports: [MatDialogContent, MatDialogActions],
  templateUrl: './edit-avatar-dialog.component.html',
  styleUrl: './edit-avatar-dialog.component.css'
})
export class EditAvatarDialogComponent {
  file: File | null = null;
  previewUrl: string | null = null;

  constructor(
    private userService: UserService,
    public dialogRef: MatDialogRef<EditAvatarDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { id: string; photoUrl?: string }
  ) {}

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.file = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl = reader.result as string;
      };
      reader.readAsDataURL(this.file);
    }
  }

  onSave() {
    if (this.file) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64 = (reader.result as string).split(',')[1]; // Remove data:image/...;base64,
        const request: PostAvatarRequest = { file: base64 };
        this.userService.updateProfilePicture(this.data.id, request).subscribe({
          next: (response: PostAvatarResponse) => {
            this.dialogRef.close(response);
          },
          error: (err) => {
            console.error('Error updating avatar:', err);
          }
        });
      };
      reader.readAsDataURL(this.file);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}