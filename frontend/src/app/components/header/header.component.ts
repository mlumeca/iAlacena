import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { RouterModule } from '@angular/router';
import { LogoutRequest } from '../../models/auth.interface';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, NgbDropdownModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  private readonly authService = inject(AuthService);
  userName = localStorage.getItem('username') ?? '';
  userPhoto = localStorage.getItem('photoUrl') ?? '/assets/img/default-avatar.png';

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  logout(): void {
    const refreshToken = localStorage.getItem('refreshToken') || '';
    const logoutRequest: LogoutRequest = { refreshToken };
    this.authService.logout(logoutRequest).subscribe({
      next: () => console.log('Logout successful'),
      error: (err) => console.error('Logout error:', err),
    });
  }
}
