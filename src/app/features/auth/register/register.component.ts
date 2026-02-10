import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-register',
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent implements OnDestroy {
  name: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;
  private subs = new Subscription();

  constructor(private authService: AuthService, private router: Router) {}

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  onSubmit() {
    if (!this.name || !this.email || !this.password || !this.confirmPassword) return;

    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'As senhas não coincidem.';
      return;
    }

    if (this.password.length < 6) {
      this.errorMessage = 'A senha deve ter no mínimo 6 caracteres.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.subs.add(
      this.authService.register({
        name: this.name,
        email: this.email,
        password: this.password,
      }).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/parent']);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = err.error?.message || 'Erro ao cadastrar. Tente novamente.';
        },
      })
    );
  }
}
