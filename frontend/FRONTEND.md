# Frontend Learning Guide

This document explains the key concepts used in this frontend alongside a map of every file.

---

## Running the app

You need **two terminals**:

```bash
# Terminal 1 — backend
cd /path/to/payment-service
mvn spring-boot:run

# Terminal 2 — frontend
cd frontend
npm run dev
```

Open `http://localhost:5173` in the browser.

---

## Core concepts

### Component
A function that returns JSX (HTML-like syntax in JavaScript). Every button, table, modal, and page is a component.

```tsx
function MyButton() {
  return <button>Click me</button>;
}
```

### Hook
A function starting with `use` that adds behaviour to a component.

- **`useState`** — remembers a value across re-renders (like an instance variable in Java):
  ```tsx
  const [count, setCount] = useState(0); // current value, setter
  setCount(count + 1);                   // triggers re-render
  ```

- **`useEffect`** — runs code *after* the component renders. Used to fetch data on page load:
  ```tsx
  useEffect(() => {
    fetchWorkspaces().then(setWorkspaces);
  }, []); // [] means "run once on mount", like @PostConstruct in Spring
  ```

### Context
Shares state across the entire component tree without passing it as props through every level (no "prop drilling").

`AuthContext.tsx` stores the JWT token and role. Any component can read them with:
```tsx
const { token, role, login, logout } = useAuth();
```
Think of it like a Spring-managed singleton bean that any component can inject.

### Axios interceptors
Middleware that runs on **every** HTTP request or response — like a Spring `OncePerRequestFilter`.

- **Request interceptor** in `api/client.ts`: reads the token from `localStorage` and attaches `Authorization: Bearer <token>` to every outgoing request.
- **Response interceptor**: if the server replies with `401 Unauthorized`, clears the stored token and redirects to `/login`.

### Vite proxy
Browsers refuse to send requests from one origin (`localhost:5173`) to another (`localhost:8080`) — this is CORS.

The Vite dev server works around this: the browser only ever talks to `localhost:5173`. Vite forwards any request starting with `/api` to `localhost:8080` on the server side (same machine, no CORS restriction).

Configured in `vite.config.ts`:
```ts
proxy: { '/api': { target: 'http://localhost:8080', changeOrigin: true } }
```

This proxy is **development only**. In production you would configure the same forwarding in Nginx/Caddy.

### JWT (JSON Web Token)
Three Base64-encoded parts joined by dots: `header.payload.signature`.

- The **payload** contains claims — you can decode it in the browser (it is not encrypted, just encoded).
- The **signature** is created by the server with a secret key. It proves the payload was not tampered with.
- The frontend stores the raw token string in `localStorage` and sends it in the `Authorization` header.
- The backend (`JwtService`) validates the signature on every request.

Because our JWT payload contains no `role` claim, the `/api/auth/signin` endpoint also returns the role directly in the JSON body (`AuthResponse`).

### TypeScript interfaces
Describe the *shape* of an object at compile time. If you access a field that does not exist, the TypeScript compiler errors before the code even runs.

```ts
interface Workspace {
  id?: number;   // optional — may not be present when creating
  host: string;  // required
}
```
This mirrors the Java DTOs and catches typos like `workspace.Host` at build time instead of at runtime.

### React Router v6
Handles navigation inside a single-page application (SPA) without full page reloads.

Key pieces:
- **`BrowserRouter`** — wraps the whole app, enables routing.
- **`Routes` / `Route`** — declares which component to show for which URL path.
- **`<Outlet />`** — a placeholder in a layout component where child routes render. `AppLayout` uses this so the sidebar/header render once and only the content area swaps.
- **`useNavigate()`** — programmatic navigation: `navigate('/workspaces')`.
- **`useParams()`** — reads URL parameters: `const { id } = useParams()` for `/workspaces/:id`.

---

## File map

```
frontend/
├── vite.config.ts          Vite dev server config + proxy
├── src/
│   ├── main.tsx            App entry point — mounts <App /> into index.html
│   ├── App.tsx             Wraps everything: ConfigProvider + AuthProvider + BrowserRouter + AppRouter
│   │
│   ├── types/              TypeScript interfaces mirroring Java DTOs
│   │   ├── auth.ts         AuthRequest, AuthResponse
│   │   ├── workspace.ts    Workspace + enums (Currency, SyncStatus, PricingStrategyType)
│   │   ├── snapshot.ts     Snapshot + WorkspaceEntityType enum
│   │   ├── price.ts        Price
│   │   └── billing.ts      BillingRecord, TimeInterval
│   │
│   ├── api/                All HTTP calls — import these in pages, never raw axios
│   │   ├── client.ts       Axios instance with JWT + 401 interceptors
│   │   ├── auth.ts         signin(), signup()
│   │   ├── workspaces.ts   getAll, getById, create, update, remove, syncAll, takeSnapshot
│   │   ├── snapshots.ts    getAll(workspaceId?), create, update, remove
│   │   ├── prices.ts       getAll(workspaceId?), create, update, remove
│   │   └── billing.ts      getAll(workspaceId?), generate(id, interval), remove
│   │
│   ├── context/
│   │   └── AuthContext.tsx token + role state; login() / logout(); useAuth() hook
│   │
│   ├── routes/
│   │   └── AppRouter.tsx   Route declarations + ProtectedRoute wrapper
│   │
│   ├── components/
│   │   ├── layout/
│   │   │   └── AppLayout.tsx       Sidebar + header + <Outlet />
│   │   ├── SyncStatusBadge.tsx     Ant Design Badge coloured by ACTIVE/PENDING/INACTIVE
│   │   └── ConfirmDeleteButton.tsx Popconfirm + trash button
│   │
│   └── pages/
│       ├── LoginPage.tsx                   Sign-in form
│       ├── workspaces/
│       │   ├── WorkspacesPage.tsx          Table + CRUD + admin buttons
│       │   ├── WorkspaceFormModal.tsx      Create/edit modal form
│       │   └── WorkspaceDetailPage.tsx     Descriptions + Snapshots/Prices tabs
│       ├── snapshots/
│       │   └── SnapshotsTab.tsx            Snapshot table + add/delete
│       ├── prices/
│       │   └── PricesTab.tsx               Price table + full CRUD
│       └── billing/
│           ├── BillingPage.tsx             Billing records table + Generate button (admin)
│           └── GenerateBillingModal.tsx    Workspace selector + date range picker
```
