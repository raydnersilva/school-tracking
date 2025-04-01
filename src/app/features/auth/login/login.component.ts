import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CaptchaService } from '../../../core/auth/captcha.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  captchaImage: SafeHtml;
  captchaInput: WritableSignal<string> = signal<string>('');
  isCaptchaValid: WritableSignal<boolean> = signal<boolean>(false);

  constructor(
    private captchaService: CaptchaService,
    private sanitizer: DomSanitizer,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadCaptcha();
  }

  loadCaptcha() {
    this.captchaService.generateCaptcha().subscribe({
      next: (v) => {
        this.captchaImage = this.sanitizer.bypassSecurityTrustHtml(v);
      },
    });
  }

  validateCaptcha() {
    this.isCaptchaValid.set(
      this.captchaService.validateCaptcha(this.captchaInput())
    );
  }

  onSubmit() {
    this.router.navigate(['parent']);
  }
}
