import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { CaptchaService } from '../../../core/auth/captcha.service';
import { AuthService } from '../../../core/auth/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent implements OnInit, OnDestroy {
  captchaImage: SafeHtml = '';
  captchaInput: string = '';
  isCaptchaValid: boolean = false;
  username: string = '';
  password: string = '';
  private subs = new Subscription();

  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private captchaService: CaptchaService,
    private authService: AuthService,
    private sanitizer: DomSanitizer,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadCaptcha();
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  loadCaptcha() {
    this.subs.add(
      this.captchaService.generateCaptcha().subscribe({
        next: (response) => {
          this.captchaImage = this.sanitizer.bypassSecurityTrustHtml(response.svg);
          this.captchaService.setCaptchaId(response.captchaId);
        },
        error: () => {
          this.captchaImage = '';
        },
      })
    );
  }

  validateCaptcha() {
    this.isCaptchaValid = this.captchaInput.length >= 4;
  }

  onSubmit() {
    if (!this.username || !this.password || !this.captchaInput) return;

    this.isLoading = true;
    this.errorMessage = '';

    this.subs.add(
      this.authService.login({
        username: this.username,
        password: this.password,
        captchaId: this.captchaService.getCaptchaId(),
        captchaAnswer: this.captchaInput,
      }).subscribe({
        next: () => {
          this.isLoading = false;
          const role = this.authService.getUserRole();
          const routeMap: Record<string, string> = { admin: '/admin', driver: '/driver', parent: '/parent' };
          this.router.navigate([routeMap[role || 'parent'] || '/parent']);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = err.error?.message || 'Erro ao fazer login. Verifique suas credenciais.';
          this.loadCaptcha();
        },
      })
    );
  }
}
