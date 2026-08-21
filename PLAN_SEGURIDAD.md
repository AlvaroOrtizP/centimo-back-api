# Plan de seguridad — Centimo Back API

Objetivo: proteger la API con **2FA (TOTP) dentro de la app** + JWT + Spring Security.
Usuario único.

> **Capa 1 (Cloudflare Access) — NO APLICABLE**: el dominio no pasa por Cloudflare
> (solo Render/hosting). Toda la protección se implementa en la app (Capa 2). El código
> de verificación de Cloudflare se eliminó; la dependencia `jjwt` se reutiliza para
> firmar los JWT propios de la app.

Estado actual: el proyecto **no tiene ninguna seguridad** (sin modelo de usuario, sin
Spring Security, sin JWT). Todos los endpoints están abiertos. Añadir seguridad en el
backend obliga a cambiar también el frontend (`centimo-web`, repo aparte).

---

## Capa 2 — 2FA (JWT + Spring Security + TOTP) en la app

Enfoque pragmático para usuario único: **un único usuario** sembrado en BD.

---

## Capa 2 — 2FA (JWT + Spring Security + TOTP) en la app

Enfoque pragmático para usuario único: **un único usuario** sembrado en BD (no auth
multi-usuario).

### Dependencias añadidas (`boot/pom.xml`) ✅

- `spring-boot-starter-security`
- `io.jsonwebtoken:jjwt-api` + `jjwt-impl` + `jjwt-jackson` (JWT, HS256)
- `dev.samstevens.totp:totp-spring-boot-starter:1.7.1` (TOTP)
- `org.springframework.security:spring-security-crypto` (en `postgres-repository`)
- `org.springframework.security:spring-security-core` (en `driving/api-rest`)

### Archivos creados ✅ (respetando la arquitectura hexagonal)

#### Migración (Flyway)

- `V13__create_usuarios.sql` — tabla `usuarios` (id, username, password_hash, totp_secret,
  totp_enabled, backup_codes, created_at). El usuario se siembra por código, no por SQL.

#### Módulo `application`

- `domain/models/Usuario.java`, `LoginResult.java`, `TotpSetupResult.java`
- `domain/exceptions/AuthException.java`
- `ports/driving/AuthDrivingPort.java` — `login`, `verify2fa`, `setup2fa`, `confirm2fa`, `disable2fa`.
- `ports/driven/UsuarioDrivenPort.java`, `JwtPort.java`, `TotpPort.java`, `PasswordPort.java`
- `usecases/AuthUseCase.java` — orquesta login/2FA.

#### Módulo `driving/api-rest`

- `swagger.yaml`: tag `Auth` + paths `/auth/login`, `/auth/verify-2fa`, `/auth/2fa/setup`,
  `/auth/2fa/confirm`, `/auth/2fa/disable` + schemas generados.
- `AuthController.java` (implementa `AuthApi` generada).
- `mappers/AuthApiMapper.java`.
- `GlobalExceptionHandler.java`: `AuthException` → 401.

#### Módulo `driven/postgres-repository`

- `models/UsuarioMO.java`, `repositories/UsuarioRepository.java`,
  `mappers/UsuarioDatasourceMapper.java`, `adapters/UsuarioDatasourceAdapter.java`,
  `adapters/CryptoService.java` (cifra/descifra `totp_secret` con AES).

#### Módulo `boot` (infraestructura)

- `security/SecurityConfig.java` — `SecurityFilterChain` stateless; abre `/auth/**` y
  swagger; el resto requiere JWT.
- `security/JwtProvider.java` (`JwtPort`) — HS256, tokens de sesión y pre-auth.
- `security/JwtAuthenticationFilter.java` — valida `Authorization: Bearer` (sesión).
- `security/PasswordService.java` (`PasswordPort`, BCrypt).
- `security/TotpService.java` (`TotpPort`, samstevens).
- `security/DataInitializer.java` — crea el usuario inicial si la tabla está vacía.

### Variables de entorno / propiedades

| Propiedad | Por defecto | Uso |
|---|---|---|
| `app.user.username` | `admin` | Usuario único |
| `app.user.password` | (vacío → se genera) | Fijar en producción |
| `jwt.secret` | clave dev insegura | **Obligatorio** en prod (≥32 chars) |
| `totp.master-key` | `change-me-in-prod-...` | Cifrado del secreto TOTP en BD |
| `totp.salt` | `deadbeefdeadbeef` | Sal del cifrado (16 hex) |

### Flujo de uso

1. Arranca: se crea `admin` (contraseña generada en logs si no fijas `app.user.password`).
2. Login `POST /auth/login` `{username,password}` → devuelve JWT (2FA desactivado).
3. Para activar 2FA: `POST /auth/2fa/setup` (con JWT) → `otpauthUrl` + `backupCodes`.
   El front pinta el QR desde `otpauthUrl`. `POST /auth/2fa/confirm` `{code}` activa.
4. Con 2FA activo, login devuelve `preAuthToken` + `requires2fa:true`;
   `POST /auth/verify-2fa` `{preAuthToken,code}` → JWT de sesión.

- `config/SecurityConfig.java` — `SecurityFilterChain` stateless:
  - Públicos: `/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health`.
  - Todo `/api/v1/**` requiere JWT.
  - `PasswordEncoder` BCrypt bean.
- `security/JwtProvider.java` (impl `JwtPort`) — firma/valida con `JWT_SECRET` de env (HS256).
- `security/JwtAuthenticationFilter.java` — valida `Authorization: Bearer <token>`.
- `security/TotpService.java` (impl `TotpPort`) — usa samstevens: genera secreto, valida
  con ventana ±1 intervalo, genera QR (PNG base64).
- `security/CryptoService.java` — cifra/descifra `totp_secret` con AES-GCM y
  `TOTP_MASTER_KEY` de env.

### Flujo de autenticación

1. **Login** `POST /auth/login`: valida user+password (BCrypt).
   - Si `totp_enabled=false` ⇒ emite JWT de sesión directo.
   - Si `totp_enabled=true` ⇒ devuelve `preAuthToken` (JWT corto, sin 2FA completo) y
     `requires2fa:true`.
2. **Verify 2FA** `POST /auth/verify-2fa`: valida código TOTP contra el secreto
   descifrado (ventana ±1) ⇒ emite JWT de sesión.
3. **Setup 2FA** (una vez) `POST /auth/2fa/setup`: genera secreto, devuelve
   `otpauthUrl` + QR + 8-10 backup codes (mostrados 1 sola vez). Se activa tras
   `POST /auth/2fa/confirm` con un código correcto.

### Notas de seguridad

- El secreto TOTP se guarda **cifrado** en BD (AES-GCM), no en texto plano.
- Backup codes de un solo uso por si se pierde el móvil.
- JWT firmado con clave de entorno (`JWT_SECRET`); no se usa la misma que la de cifrado.

---

## Impacto en el frontend (`centimo-web`, otro repo) — IMPLEMENTADO ✅

Repo aparte: `F:\Programacion\Proyectos\centimo\centimo-web` (Angular 17.3 standalone).

### Archivos creados/modificados

- `src/app/models/auth.ts` — interfaces del contrato auth (`LoginRequest`, `LoginResponse`,
  `Verify2faRequest`, `TotpSetupResponse`, `Confirm2faRequest`, `Disable2faRequest`).
- `src/app/core/services/auth.service.ts` — `login` / `verify2fa` / `setup2fa` / `confirm2fa`
  / `disable2fa`; token en `localStorage` + signal `isAuthenticated`.
- `src/app/core/interceptors/auth.interceptor.ts` — inyecta `Authorization: Bearer` y, ante
  401 con token, hace logout + redirige a `/login`.
- `src/app/core/guards/auth.guard.ts` — protege rutas; redirige a `/login` si no autenticado.
- `src/app/features/login/login.component.ts` — paso credenciales → (si `requires2fa`) paso
  código TOTP de 6 dígitos.
- `src/app/features/setup-2fa/setup-2fa.component.ts` — muestra secreto + `otpauthUrl` +
  backup codes (sin lib de QR para no romper build offline); confirm/disable.
- `src/app/app.routes.ts` — rutas `login` (pública) y `setup-2fa` (con `authGuard`); resto
  bajo `canActivate: [authGuard]`.
- `src/app/app.config.ts` — `withInterceptors([errorInterceptor, authInterceptor])`.
- `src/app/app.component.ts` / `.html` — shell oculta sidebar en `/login`; topbar con enlace
  a "Configurar 2FA" y botón "Cerrar sesión".

Para probar en local: arrancar backend (`mvnw` en `boot`) y frontend (`npm start`, servido
en `http://localhost:4200` apuntando a `http://localhost:8080`). El primer arranque siembra
`admin` (contraseña en `app.user.password` o generada en logs).

## Impacto en los tests de integración

Los IT actuales usan `@AutoConfigureMockMvc(addFilters = false)`, por lo que la
seguridad **no los rompe**. Los nuevos IT de auth se harían con filtros activos y un
usuario de test.

---

## Qué se necesita del usuario para implementar

1. **Usuario** a usar (p.ej. tu email o `admin`).
2. **Contraseña inicial** (o la genero y se muestra en consola en el primer arranque).
3. **Secreto TOTP**: ¿guardado cifrado en BD (recomendado) o variable de entorno simple?
4. **Claves de entorno**: `JWT_SECRET` y `TOTP_MASTER_KEY` (las puedo generar y documentar).
5. Confirmar que el dominio ya pasa por Cloudflare (para la Capa 1).

## Orden de ejecución propuesto

1. Dependencias + `V13` + `UsuarioMO` / repository / adapter.
2. `JwtPort` / `TotpPort` + implementaciones en `boot`, `SecurityConfig`, filtro JWT.
3. `AuthDrivingPort` / `AuthUseCase` + swagger + `AuthController` + mapper.
4. IT de auth.
5. Documentar Cloudflare Access (Capa 1) paso a paso.
