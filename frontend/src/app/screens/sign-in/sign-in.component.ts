import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { UserService } from '../../services/user.service';
import { filter } from 'rxjs';
import { RegisterRequest, RegisterResponse } from '../../models/user.interface';
import { HttpErrorResponse } from '@angular/common/http';
import { confirmPasswordValidator } from '../../utils/confirm-password.validator';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './sign-in.component.html',
  styleUrl: './sign-in.component.css'
})
export class SignInComponent {
private readonly formBuilder = inject(FormBuilder);
  private readonly userService = inject(UserService);
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

      const credentials: RegisterRequest = this.loginForm.getRawValue() as RegisterRequest;

      this.userService.userRegister(credentials).subscribe({
        next: (response: RegisterResponse) => {
          console.log('Login successful, response:', response); // Debug log
          this.isSubmitting.set(false);
          this.router.navigateByUrl('/login').then((success) => {
            console.log('Navigation to /login:', success ? 'Success' : 'Failed'); // Debug log
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
    email: ['', [Validators.required]],
    password: ['', [Validators.required]],
    verifyPassword: ['', [Validators.required]], 
    confirmPasswordValidator,
  });
}
