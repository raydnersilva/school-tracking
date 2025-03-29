import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CaptchaService {
  private captchaUrl: string = 'http://localhost:3000/api/captcha';
  private captchaText: string = '';

  constructor(private http: HttpClient) {}

  generateCaptcha(): Observable<any> {
    return this.http.get(this.captchaUrl, {
      responseType: 'text',
    });
  }

  validateCaptcha(userInput: string): boolean {
    return userInput === this.captchaText;
  }
}
