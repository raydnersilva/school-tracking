# SchoolTrack - Backend API (Spring Boot)

## Requisitos

- Java 17+
- Maven 3.8+

## Como executar

```bash
cd backend
./mvnw spring-boot:run
```

Ou no Windows:
```bash
cd backend
mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

## Banco de Dados

Por padrão, usa **H2 em memória** (desenvolvimento). O console H2 está disponível em:
`http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:schooltrack`
- Username: `sa`
- Password: (vazio)

Para produção, configure PostgreSQL no `application.properties`.

## Dados de Teste

O sistema cria automaticamente dados de teste ao iniciar:

| Usuário | Senha | Role |
|---------|-------|------|
| admin | admin123 | ADMIN |
| maria | 123456 | PARENT |
| jose | 123456 | DRIVER |

## Endpoints da API

### Auth (público)
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Cadastro
- `POST /api/auth/forgot-password` - Recuperação de senha

### Captcha (público)
- `GET /api/captcha` - Gerar CAPTCHA
- `POST /api/captcha/validate` - Validar CAPTCHA

### Buses (autenticado)
- `GET /api/buses` - Listar ônibus ativos
- `GET /api/buses/{id}` - Detalhes do ônibus
- `PUT /api/buses/{id}/location` - Atualizar localização

### Routes (autenticado)
- `GET /api/routes` - Listar rotas ativas
- `GET /api/routes/{id}` - Detalhes da rota

### Students (autenticado)
- `GET /api/students` - Listar alunos do responsável
- `GET /api/students/{id}` - Detalhes do aluno

### Notifications (autenticado)
- `GET /api/notifications` - Listar notificações
- `GET /api/notifications/unread` - Listar não lidas
- `PUT /api/notifications/{id}/read` - Marcar como lida

### Schedules (autenticado)
- `GET /api/schedules` - Listar horários
- `GET /api/schedules/student/{studentId}` - Por aluno
- `GET /api/schedules/route/{routeId}` - Por rota
