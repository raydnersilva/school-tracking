import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-forgot-password',
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ForgotPasswordComponent implements OnDestroy {
  email: string = '';
  successMessage: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;
  private subs = new Subscription();

  constructor(private authService: AuthService) {}

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  onSubmit() {
    if (!this.email) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.subs.add(
      this.authService.forgotPassword({ email: this.email }).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = response.message;
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = err.error?.message || 'Erro ao enviar email. Tente novamente.';
        },
      })
    );
  }
}
