import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CaptchaResponse {
  captchaId: string;
  svg: string;
}

export interface CaptchaValidationResponse {
  valid: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class CaptchaService {
  private apiUrl = environment.apiUrl;
  private captchaId: string = '';

  constructor(private http: HttpClient) {}

  generateCaptcha(): Observable<CaptchaResponse> {
    return this.http.get<CaptchaResponse>(`${this.apiUrl}/captcha`);
  }

  setCaptchaId(id: string): void {
    this.captchaId = id;
  }

  getCaptchaId(): string {
    return this.captchaId;
  }

  validateCaptcha(userInput: string): Observable<CaptchaValidationResponse> {
    return this.http.post<CaptchaValidationResponse>(`${this.apiUrl}/captcha/validate`, {
      captchaId: this.captchaId,
      answer: userInput,
    });
  }
}
