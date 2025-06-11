import { Component, inject, OnInit, signal } from '@angular/core';
import {
    FormArray,
    FormBuilder,
    FormGroup,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse } from '../../models/auth.interface';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  readonly formBuilder = inject(FormBuilder);
  readonly authService = inject(AuthService);
  readonly router = inject(Router);
  usernameSignal = signal<string>;
  passwordSignal = signal<string>;
  remindMeSignal = signal<boolean>;
  loginSignal = signal<LoginResponse>;

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  })

  // Getters



  onSubmit() {
    if (this.loginForm.valid) {
      const formValue = this.loginForm.getRawValue(); 

      this.authService.login(credentials as LoginRequest).subscribe({
        next: login => {
          this.loginSignal.update(logins => [...logins, login]);
          this.loginForm.reset();
        },
        error: err => console.error('Error al crear coche:', err),
      });
      this.router.navigateByUrl('/home');
    } 
  }
}