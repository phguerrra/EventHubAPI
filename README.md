BackGrupo1
==========

Descrição
---------
BackGrupo1 é uma API REST em Spring Boot para gerenciar eventos e participantes. Permite registro e login de usuários, criação/edição/exclusão de eventos por administradores e inscrição de participantes em eventos.

Principais recursos
-------------------
- Registro/login de usuários (gera JWT e popula HttpSession)
- CRUD de eventos
- Inscrição e listagem de participantes por evento
- Validação de entrada com mensagens claras (400 com mapa campo->erro)

Pré-requisitos
--------------
- Java 21
- Maven (ou usar o wrapper ./mvnw)
- PostgreSQL (ou ajustar datasource em src/main/resources/application.properties)

Configuração
------------
As configurações padrão estão em src/main/resources/application.properties. Valores importantes:
- spring.datasource.url, username, password — ajuste para seu banco
- jwt.secret — segredo usado para geração de tokens
- jwt.expiration-ms — tempo de expiração do token em ms

Como compilar
------------
Usando o wrapper (recomendado):

./mvnw -DskipTests package

Como executar
-------------
1) JAR gerado:

java -jar target/BackGrupo1-0.0.1-SNAPSHOT.jar

ou

2) direto com Maven:

./mvnw spring-boot:run

Endpoints principais
--------------------
- POST /users/register  -> registra usuário (corpo: UserDTO)
- POST /users/login     -> realiza login (retorna token e popula sessão)
- POST /users/logout    -> encerra sessão
- GET  /users/me        -> retorna usuário logado (usa sessão)
- POST /users/admin/dashboard -> área protegida para admins (usa sessão)

- GET  /events          -> lista eventos
- GET  /events/{id}     -> obtém evento
- POST /events          -> cria evento (admin)
- PUT  /events/{id}     -> atualiza evento (admin)
- DELETE /events/{id}   -> exclui evento (admin)

- GET  /events/{eventId}/participants            -> lista participantes
- POST /events/{eventId}/participants?userId=X   -> inscreve participante (body ParticipantDTO)
- DELETE /events/{eventId}/participants/{id}     -> remove participante
- GET  /events/{eventId}/participants/check-email?email=... -> check

Validação de campos (resumo)
----------------------------
- UserDTO: name (obrigatório), email (obrigatório, válido), password (>=6), cpf (obrigatório), dataNascimento (deve ser passada)
- LoginDTO: email e password obrigatórios (email válido)
- EventDTO: title (obrigatório), date (obrigatório), maxParticipants (obrigatório, >=1)
- ParticipantDTO: name, email (válido), cpf — obrigatórios

Comportamento de autenticação
-----------------------------
- No login é gerado um JWT (JwtService) e a sessão HTTP é populada (userId, userRole, isAdmin).
- Atualmente o projeto emite tokens mas não valida automaticamente todas as requisições (autorização por sessão é a abordagem atual). Rotas administrativas verificam sessão/role.

Recomendações
-------------
- Em produção, altere jwt.secret e credenciais do DB.
- Reforçar proteção JWT adicionando filtro que valide Authorization: Bearer <token> e configurando SecurityFilterChain.

Testes
------
Há alguns testes na pasta src/test. Rodar com:

./mvnw test

