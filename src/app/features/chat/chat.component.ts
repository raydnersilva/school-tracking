import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { HttpClient } from '@angular/common/http';
import { Subscription, filter, take } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from '../../core/auth/auth.service';

interface ChatMessage {
  id: number;
  roomId: string;
  senderId: number;
  senderName: string;
  content: string;
  sentAt: string;
  readByRecipient: boolean;
}

@Component({
  selector: 'app-chat',
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChatComponent implements OnInit, OnDestroy {
  messages: ChatMessage[] = [];
  newMessage = '';
  roomId = 'general';
  currentUserName: string;
  private stompClient: Client | null = null;
  private subs = new Subscription();

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {
    this.currentUserName = this.authService.getUserName() || '';
  }

  ngOnInit() {
    this.loadMessages();
    this.connectWebSocket();
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
    this.stompClient?.deactivate();
  }

  private loadMessages() {
    this.subs.add(
      this.http.get<ChatMessage[]>(`${environment.apiUrl}/chat/${this.roomId}`).subscribe({
        next: (msgs) => {
          this.messages = msgs;
          this.cdr.markForCheck();
          setTimeout(() => this.scrollToBottom(), 100);
        },
      })
    );
  }

  private connectWebSocket() {
    const wsUrl = environment.apiUrl.replace('/api', '/ws');
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      reconnectDelay: 3000,
      debug: () => {},
      onConnect: () => {
        this.stompClient?.subscribe(`/topic/chat/${this.roomId}`, (message: IMessage) => {
          const msg = JSON.parse(message.body) as ChatMessage;
          this.messages = [...this.messages, msg];
          this.cdr.markForCheck();
          setTimeout(() => this.scrollToBottom(), 50);
        });
      },
    });
    this.stompClient.activate();
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.stompClient?.active) return;

    this.stompClient.publish({
      destination: '/app/chat/send',
      body: JSON.stringify({ roomId: this.roomId, content: this.newMessage }),
    });
    this.newMessage = '';
  }

  private scrollToBottom() {
    const el = document.getElementById('chat-messages');
    if (el) el.scrollTop = el.scrollHeight;
  }

  isOwnMessage(msg: ChatMessage): boolean {
    return msg.senderName === this.currentUserName;
  }
}
