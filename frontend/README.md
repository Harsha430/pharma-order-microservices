# PharmaOrder Frontend

Modern, responsive React application built with TanStack Router, TanStack Query, and Tailwind CSS for the PharmaOrder pharmacy e-commerce platform.

## 🎨 Tech Stack

- **Framework**: React 19.2.0
- **Routing**: TanStack Router 1.168.0 (File-based routing)
- **State Management**: 
  - Zustand 5.0.12 (Global state)
  - TanStack Query 5.99.2 (Server state)
- **UI Components**: Radix UI primitives
- **Styling**: Tailwind CSS 4.2.1
- **Forms**: React Hook Form 7.71.2 + Zod 3.24.2
- **HTTP Client**: Axios 1.15.1
- **Build Tool**: Vite 7.3.1
- **Language**: TypeScript 5.8.3

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── ui/             # Base UI components (buttons, inputs, etc.)
│   │   ├── layout/         # Layout components (header, footer, sidebar)
│   │   ├── product/        # Product-related components
│   │   ├── cart/           # Shopping cart components
│   │   └── prescription/   # Prescription upload components
│   ├── routes/             # TanStack Router file-based routes
│   │   ├── __root.tsx      # Root layout
│   │   ├── index.tsx       # Home page
│   │   ├── products/       # Product pages
│   │   ├── cart/           # Cart page
│   │   ├── orders/         # Order pages
│   │   └── auth/           # Authentication pages
│   ├── lib/                # Utilities and helpers
│   │   ├── api/            # API client and endpoints
│   │   ├── utils/          # Helper functions
│   │   └── constants/      # Constants and enums
│   ├── hooks/              # Custom React hooks
│   │   ├── useAuth.ts      # Authentication hook
│   │   ├── useCart.ts      # Cart management hook
│   │   └── useProducts.ts  # Product queries hook
│   ├── stores/             # Zustand stores
│   │   ├── authStore.ts    # Authentication state
│   │   ├── cartStore.ts    # Cart state
│   │   └── uiStore.ts      # UI state (modals, toasts)
│   ├── types/              # TypeScript type definitions
│   ├── styles/             # Global styles
│   └── main.tsx            # Application entry point
├── public/                 # Static assets
├── .env.example            # Environment variables template
├── package.json
├── tsconfig.json
├── vite.config.ts
├── tailwind.config.js
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ and npm/yarn
- Backend services running (see main README)

### Installation

```bash
# Install dependencies
npm install

# or with yarn
yarn install
```

### Environment Variables

Create a `.env` file in the frontend directory:

```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080
VITE_API_TIMEOUT=30000

# File Upload
VITE_MAX_FILE_SIZE=5242880
VITE_ALLOWED_FILE_TYPES=application/pdf,image/jpeg,image/png

# Feature Flags
VITE_ENABLE_LOYALTY=true
VITE_ENABLE_HEALTH_PACKAGES=true

# Analytics (optional)
VITE_GA_TRACKING_ID=
```

### Development

```bash
# Start development server
npm run dev

# The app will be available at http://localhost:3000
```

### Build

```bash
# Production build
npm run build

# Preview production build
npm run preview

# Development build (with source maps)
npm run build:dev
```

### Linting and Formatting

```bash
# Run ESLint
npm run lint

# Format code with Prettier
npm run format
```

## 🎯 Key Features

### Authentication
- User registration and login
- JWT token management with automatic refresh
- Protected routes with role-based access
- Persistent authentication state

### Product Browsing
- Advanced search with filters
- Category-based navigation
- Product details with image gallery
- Prescription requirement indicators
- Price and availability display

### Shopping Cart
- Add/remove/update items
- Real-time price calculation
- Prescription validation
- Persistent cart across sessions

### Prescription Management
- Drag-and-drop file upload
- Preview uploaded prescriptions
- Track prescription status
- Pharmacist approval workflow

### Order Management
- Checkout with address selection
- Order history and tracking
- Quick reorder functionality
- Order status updates

### Loyalty Program
- Points balance display
- Points redemption at checkout
- Transaction history
- Available offers and discounts

## 🔧 Configuration

### API Client

The API client is configured in `src/lib/api/client.ts`:

```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: import.meta.env.VITE_API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for JWT token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for token refresh
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Handle token refresh
    }
    return Promise.reject(error);
  }
);
```

### TanStack Router

Routes are file-based in the `src/routes/` directory:

```
routes/
├── __root.tsx              # Root layout with header/footer
├── index.tsx               # Home page (/)
├── products/
│   ├── index.tsx           # Product list (/products)
│   └── $productId.tsx      # Product details (/products/:productId)
├── cart.tsx                # Cart page (/cart)
├── checkout.tsx            # Checkout page (/checkout)
├── orders/
│   ├── index.tsx           # Order list (/orders)
│   └── $orderId.tsx        # Order details (/orders/:orderId)
├── prescriptions/
│   ├── index.tsx           # Prescription list (/prescriptions)
│   └── upload.tsx          # Upload prescription (/prescriptions/upload)
└── auth/
    ├── login.tsx           # Login page (/auth/login)
    └── register.tsx        # Register page (/auth/register)
```

### TanStack Query

Query configuration in `src/main.tsx`:

```typescript
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});
```

## 🎨 UI Components

### Base Components (Radix UI)

All base components are in `src/components/ui/`:

- `Button` - Various button styles and sizes
- `Input` - Text input with validation
- `Select` - Dropdown select
- `Dialog` - Modal dialogs
- `Dropdown` - Dropdown menus
- `Tabs` - Tab navigation
- `Card` - Content cards
- `Badge` - Status badges
- `Avatar` - User avatars
- `Toast` - Notification toasts

### Custom Components

- `ProductCard` - Product display card
- `CartItem` - Shopping cart item
- `PrescriptionUpload` - File upload component
- `OrderTimeline` - Order status timeline
- `AddressForm` - Address input form
- `SearchBar` - Product search with autocomplete

## 🔐 Authentication Flow

```typescript
// Login
const { mutate: login } = useMutation({
  mutationFn: (credentials) => authApi.login(credentials),
  onSuccess: (data) => {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    useAuthStore.getState().setUser(data.user);
    navigate('/');
  },
});

// Protected Route
export const Route = createFileRoute('/orders')({
  beforeLoad: ({ context }) => {
    if (!context.auth.isAuthenticated) {
      throw redirect({ to: '/auth/login' });
    }
  },
});
```

## 📱 Responsive Design

The application is fully responsive with breakpoints:

- **Mobile**: < 640px
- **Tablet**: 640px - 1024px
- **Desktop**: > 1024px

Tailwind CSS utilities are used for responsive design:

```tsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
  {/* Responsive grid */}
</div>
```

## 🧪 Testing

```bash
# Run tests (when configured)
npm run test

# Run tests in watch mode
npm run test:watch

# Generate coverage report
npm run test:coverage
```

## 🚢 Deployment

### Build for Production

```bash
npm run build
```

The build output will be in the `dist/` directory.

### Deploy to Vercel

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel
```

### Deploy to Netlify

```bash
# Install Netlify CLI
npm i -g netlify-cli

# Deploy
netlify deploy --prod
```

### Docker Deployment

```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 🔍 Performance Optimization

- **Code Splitting**: Automatic route-based code splitting
- **Lazy Loading**: Components loaded on demand
- **Image Optimization**: Lazy loading images with placeholders
- **Caching**: TanStack Query caching strategy
- **Bundle Analysis**: Use `vite-bundle-visualizer`

```bash
npm run build -- --mode analyze
```

## 🐛 Debugging

### React DevTools

Install React DevTools browser extension for component inspection.

### TanStack Query DevTools

Add to your app for query debugging:

```typescript
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

<QueryClientProvider client={queryClient}>
  <App />
  <ReactQueryDevtools initialIsOpen={false} />
</QueryClientProvider>
```

### TanStack Router DevTools

```typescript
import { TanStackRouterDevtools } from '@tanstack/router-devtools';

<RouterProvider router={router}>
  <TanStackRouterDevtools router={router} />
</RouterProvider>
```

## 📚 Resources

- [React Documentation](https://react.dev/)
- [TanStack Router](https://tanstack.com/router)
- [TanStack Query](https://tanstack.com/query)
- [Tailwind CSS](https://tailwindcss.com/)
- [Radix UI](https://www.radix-ui.com/)
- [Vite](https://vitejs.dev/)

## 🤝 Contributing

1. Follow the component structure
2. Use TypeScript for type safety
3. Follow Tailwind CSS conventions
4. Write meaningful commit messages
5. Test responsive design on multiple devices

## 📄 License

MIT License - see main project README

---

**Frontend Team** - Building beautiful, performant user experiences
