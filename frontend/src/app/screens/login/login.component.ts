import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { LoginRequest, LoginResponse } from '../../models/auth.interface';
import { CommonModule, JsonPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, JsonPipe],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  // Signals for state management
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  // Reactive form
  loginForm = this.formBuilder.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
    // rememberMe: [false],
  });

  onSubmit() {
    if (this.loginForm.valid) {
      this.isSubmitting.set(true);
      this.errorMessage.set(null);

      const credentials: LoginRequest = this.loginForm.getRawValue() as LoginRequest;

      this.authService.login(credentials).subscribe({
        next: (response: LoginResponse) => {
          this.isSubmitting.set(false);
          this.router.navigateByUrl('/home');
        },
        error: (err: HttpErrorResponse) => {
          this.isSubmitting.set(false);
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
        }
      });
    }
  }
}