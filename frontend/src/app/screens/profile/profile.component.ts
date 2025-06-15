import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserService } from '../../services/user.service';
import { UserProfile } from '../../models/user.interface';
import { MatDialog } from '@angular/material/dialog';
import { EditProfileDialogComponent } from '../../components/edit-profile-dialog/edit-profile-dialog.component';
import { EditPasswordDialogComponent } from '../../components/edit-password-dialog/edit-password-dialog.component';
import { EditAvatarDialogComponent } from '../../components/edit-avatar-dialog/edit-avatar-dialog.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  user: UserProfile | null = null;

  constructor(
    private userService: UserService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.userService.getUserProfile().subscribe({
      next: (profile: UserProfile) => {
        this.user = profile;
      },
      error: (err) => {
        console.error('Error fetching user profile:', err);
        // Handle error (e.g., show toast or redirect to login)
      }
    });
  }

  openEditProfileDialog() {
    const dialogRef = this.dialog.open(EditProfileDialogComponent, {
      width: '400px',
      data: {
        username: this.user?.username || '',
        // email: this.user?.email || ''
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.user = { ...this.user!, username: result.username}; // , email: result.email 
      }
    });
  }

  openEditPasswordDialog() {
    const dialogRef = this.dialog.open(EditPasswordDialogComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Password updated successfully');
        // No user data update needed for password
      }
    });
  }

  openEditAvatarDialog() {
    const dialogRef = this.dialog.open(EditAvatarDialogComponent, {
      width: '400px',
      data: {
        id: this.user?.id || '',
        photoUrl: this.user?.photoUrl
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.user = { ...this.user!, photoUrl: result.uri };
        localStorage.setItem('photoUrl', result.uri);
      }
    });
  }
}