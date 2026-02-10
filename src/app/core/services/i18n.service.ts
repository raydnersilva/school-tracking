import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type Locale = 'pt-BR' | 'en';

const translations: Record<Locale, Record<string, string>> = {
  'pt-BR': {
    'app.title': 'SchoolTrack',
    'nav.home': 'Início',
    'nav.schedule': 'Horários',
    'nav.notifications': 'Notificações',
    'nav.chat': 'Chat',
    'nav.settings': 'Configurações',
    'nav.admin': 'Administração',
    'nav.driver': 'Painel Motorista',
    'nav.routes': 'Rotas',
    'nav.buses': 'Ônibus',
    'nav.students': 'Alunos',
    'nav.logout': 'Sair',
    'login.title': 'Entrar',
    'login.username': 'Usuário',
    'login.password': 'Senha',
    'login.submit': 'Entrar',
    'login.register': 'Criar conta',
    'login.forgot': 'Esqueceu a senha?',
    'register.title': 'Criar Conta',
    'register.name': 'Nome',
    'register.email': 'Email',
    'register.password': 'Senha',
    'register.confirm': 'Confirmar Senha',
    'register.submit': 'Cadastrar',
    'dashboard.tracking': 'Rastreamento do Ônibus',
    'dashboard.schedule': 'Horário Escolar',
    'dashboard.notifications': 'Notificações',
    'dashboard.actions': 'Ações Rápidas',
    'dashboard.report': 'Reportar',
    'dashboard.contact': 'Contatar',
    'driver.title': 'Painel do Motorista',
    'driver.start': 'Iniciar Viagem',
    'driver.end': 'Encerrar Viagem',
    'driver.trip_active': 'Viagem em Andamento',
    'driver.students': 'Alunos',
    'driver.board': 'Embarcar',
    'driver.drop': 'Desembarcar',
    'driver.history': 'Histórico Recente',
    'admin.title': 'Painel Administrativo',
    'admin.stats': 'Dashboard',
    'admin.users': 'Usuários',
    'admin.create': 'Criar',
    'admin.delete': 'Excluir',
    'admin.broadcast': 'Enviar para Todos',
    'chat.title': 'Chat',
    'chat.placeholder': 'Digite sua mensagem...',
    'chat.empty': 'Nenhuma mensagem ainda. Comece a conversa!',
    'rating.title': 'Avaliar Motorista',
    'rating.submit': 'Enviar Avaliação',
    'rating.thanks': 'Obrigado pela avaliação!',
    'common.loading': 'Carregando...',
    'common.error': 'Erro',
    'common.save': 'Salvar',
    'common.cancel': 'Cancelar',
    'common.back': 'Voltar',
    'common.empty': 'Nenhum item encontrado.',
  },
  'en': {
    'app.title': 'SchoolTrack',
    'nav.home': 'Home',
    'nav.schedule': 'Schedule',
    'nav.notifications': 'Notifications',
    'nav.chat': 'Chat',
    'nav.settings': 'Settings',
    'nav.admin': 'Administration',
    'nav.driver': 'Driver Panel',
    'nav.routes': 'Routes',
    'nav.buses': 'Buses',
    'nav.students': 'Students',
    'nav.logout': 'Logout',
    'login.title': 'Sign In',
    'login.username': 'Username',
    'login.password': 'Password',
    'login.submit': 'Sign In',
    'login.register': 'Create account',
    'login.forgot': 'Forgot password?',
    'register.title': 'Create Account',
    'register.name': 'Name',
    'register.email': 'Email',
    'register.password': 'Password',
    'register.confirm': 'Confirm Password',
    'register.submit': 'Register',
    'dashboard.tracking': 'Bus Tracking',
    'dashboard.schedule': 'School Schedule',
    'dashboard.notifications': 'Notifications',
    'dashboard.actions': 'Quick Actions',
    'dashboard.report': 'Report',
    'dashboard.contact': 'Contact',
    'driver.title': 'Driver Panel',
    'driver.start': 'Start Trip',
    'driver.end': 'End Trip',
    'driver.trip_active': 'Trip In Progress',
    'driver.students': 'Students',
    'driver.board': 'Board',
    'driver.drop': 'Drop Off',
    'driver.history': 'Recent History',
    'admin.title': 'Admin Panel',
    'admin.stats': 'Dashboard',
    'admin.users': 'Users',
    'admin.create': 'Create',
    'admin.delete': 'Delete',
    'admin.broadcast': 'Send to All',
    'chat.title': 'Chat',
    'chat.placeholder': 'Type your message...',
    'chat.empty': 'No messages yet. Start the conversation!',
    'rating.title': 'Rate Driver',
    'rating.submit': 'Submit Rating',
    'rating.thanks': 'Thank you for your rating!',
    'common.loading': 'Loading...',
    'common.error': 'Error',
    'common.save': 'Save',
    'common.cancel': 'Cancel',
    'common.back': 'Back',
    'common.empty': 'No items found.',
  },
};

@Injectable({ providedIn: 'root' })
export class I18nService {
  private currentLocale$ = new BehaviorSubject<Locale>(this.getStoredLocale());

  get locale$() {
    return this.currentLocale$.asObservable();
  }

  get locale(): Locale {
    return this.currentLocale$.value;
  }

  setLocale(locale: Locale) {
    this.currentLocale$.next(locale);
    localStorage.setItem('locale', locale);
  }

  t(key: string): string {
    return translations[this.locale]?.[key] || key;
  }

  private getStoredLocale(): Locale {
    const stored = localStorage.getItem('locale');
    if (stored === 'en' || stored === 'pt-BR') return stored;
    return 'pt-BR';
  }
}
