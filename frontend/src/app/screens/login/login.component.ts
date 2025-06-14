import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { LoginRequest, LoginResponse } from '../../models/auth.interface';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);
  private isNavigating = false;

  constructor() {
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.isNavigating = false;
    });
  }

  onSubmit() {
    if (this.loginForm.valid && !this.isNavigating) {
      console.log('Submitting login form'); // Debug log
      this.isSubmitting.set(true);
      this.errorMessage.set(null);
      this.isNavigating = true;

      const credentials: LoginRequest = this.loginForm.getRawValue() as LoginRequest;

      this.authService.login(credentials).subscribe({
        next: (response: LoginResponse) => {
          console.log('Login successful, response:', response); // Debug log
          this.isSubmitting.set(false);
          this.router.navigateByUrl('/home').then((success) => {
            console.log('Navigation to /home:', success ? 'Success' : 'Failed'); // Debug log
            if (!success) {
              console.error('Navigation failed, current token:', localStorage.getItem('token'));
              this.isNavigating = false;
            }
          });
        },
        error: (err: HttpErrorResponse) => {
          this.isSubmitting.set(false);
          this.isNavigating = false;
          let message = 'Error al iniciar sesión. Verifica tus credenciales.';
          if (err.status === 0) {
            message = 'No se pudo conectar al servidor. Verifica que el servidor esté en ejecución.';
          } else if (err.status === 401) {
            message = 'Credenciales incorrectas.';
          } else if (err.error?.message) {
            message = err.error.message;
          }
          this.errorMessage.set(message);
          console.error('Login error:', err);
        },
      });
    } else {
      console.log('Form invalid or navigating:', this.loginForm.errors, this.isNavigating); 
    }
  }

  loginForm = this.formBuilder.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });
}
