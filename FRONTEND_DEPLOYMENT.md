# Frontend Deployment Guide

## Prerequisites
- Node.js 16+
- npm or yarn
- Git
- Netlify account

## Local Development

```bash
cd frontend

# Install dependencies
npm install

# Configure API endpoint in .env
REACT_APP_API_URL=http://localhost:8080/api

# Start development server
npm start
```

## Build for Production

```bash
# Build optimized production bundle
npm run build

# Test production build locally
npm install -g serve
serve -s build
```

## Deploy to Netlify

### Method 1: GitHub Integration (Recommended)

1. Push code to GitHub
2. Visit https://app.netlify.com
3. Click "New site from Git"
4. Connect GitHub account
5. Select repository
6. Configure build settings:
   - Base directory: `frontend`
   - Build command: `npm run build`
   - Publish directory: `frontend/build`

7. Add environment variables:
   - `REACT_APP_API_URL` = `https://your-backend-api.com/api`

8. Click "Deploy site"

### Method 2: Manual Deployment

```bash
# Install Netlify CLI
npm install -g netlify-cli

# Login to Netlify
netlify login

# Deploy from build directory
cd frontend
npm run build
netlify deploy --prod --dir=build
```

### Method 3: Drag & Drop

1. Build project: `npm run build`
2. Go to https://app.netlify.com
3. Drag and drop the `build` folder
4. Configure custom domain

## Environment Variables

Set in Netlify dashboard:
```
REACT_APP_API_URL=https://your-production-backend-api.com/api
```

Or in `.env.production`:
```
REACT_APP_API_URL=https://your-production-backend-api.com/api
```

## Custom Domain

1. In Netlify dashboard, go to Domain settings
2. Add custom domain
3. Point DNS records to Netlify nameservers
4. SSL certificate auto-generated

## Performance Optimization

```bash
# Analyze bundle size
npm install -g webpack-bundle-analyzer

# Build and analyze
npm run build -- --analyze
```

### Optimization Tips
- Code splitting (already configured in create-react-app)
- Lazy loading routes
- Image optimization
- CSS minification
- JavaScript minification

## Caching & CDN

Netlify provides:
- Global CDN
- Automatic gzip compression
- Asset optimization
- HTTP/2 support
- Cache busting for updated files

## Build Hooks

For CI/CD, use Netlify Build Hooks:

1. Go to Site settings → Build & Deploy → Build Hooks
2. Create new hook
3. Get webhook URL
4. Use in your CI/CD pipeline:

```bash
curl -X POST <webhook-url>
```

## Environment-Specific Builds

### Staging
```bash
REACT_APP_API_URL=https://staging-api.com/api npm run build
```

### Production
```bash
REACT_APP_API_URL=https://api.com/api npm run build
```

## Security Headers

Configure in `netlify.toml`:
```toml
[[headers]]
  for = "/*"
  [headers.values]
    X-Frame-Options = "SAMEORIGIN"
    X-Content-Type-Options = "nosniff"
    X-XSS-Protection = "1; mode=block"
    Referrer-Policy = "strict-origin-when-cross-origin"
```

## Redirects & Rewrites

For SPA routing, add to `netlify.toml`:
```toml
[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

## Monitoring

### Netlify Analytics
- View in Netlify dashboard
- Track build times
- Monitor deploy errors
- Check bandwidth usage

### Application Monitoring
- Use tools like Sentry
- Monitor JavaScript errors
- Track user sessions
- Performance monitoring

### Sentry Integration

```javascript
// src/index.js
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: process.env.REACT_APP_SENTRY_DSN,
  environment: process.env.NODE_ENV,
});
```

## Troubleshooting

### Build Fails
- Check build logs in Netlify dashboard
- Verify environment variables are set
- Check Node.js version compatibility

### White Screen / 404 Errors
- Ensure `[[redirects]]` rule in netlify.toml
- Check browser console for errors
- Verify API_URL is correct

### CORS Errors
- Configure backend CORS headers
- Check if backend API is running
- Verify API_URL environment variable

### Slow Loading
- Analyze bundle size: `npm run build -- --analyze`
- Enable gzip compression
- Use lazy loading for routes
- Optimize images

## Advanced: Custom Build Scripts

Create `netlify/functions/` for serverless functions:

```javascript
// netlify/functions/api.js
exports.handler = async (event, context) => {
  return {
    statusCode: 200,
    body: JSON.stringify({ message: "Hello" })
  };
};
```

## Rollback to Previous Deploy

1. Go to Netlify dashboard
2. Click "Deploy log"
3. Find previous successful deploy
4. Click three dots → "Publish deploy"

## Free vs Paid Plans

| Feature | Free | Pro |
|---------|------|-----|
| Bandwidth | 100GB/month | Unlimited |
| Build minutes | 300/month | 3000/month |
| Custom domains | 1 | Unlimited |
| Team members | Limited | Unlimited |
| Forms | 100 | Unlimited |

## CI/CD Pipeline Example (GitHub Actions)

```yaml
name: Deploy to Netlify
on:
  push:
    branches: [main]
    paths: ['frontend/**']

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-node@v2
        with:
          node-version: '18'
      - run: cd frontend && npm install && npm run build
      - uses: nwtgck/actions-netlify@v2
        with:
          publish-dir: './frontend/build'
          production-deploy: true
        env:
          NETLIFY_AUTH_TOKEN: ${{ secrets.NETLIFY_AUTH_TOKEN }}
          NETLIFY_SITE_ID: ${{ secrets.NETLIFY_SITE_ID }}
```

---

For issues, check Netlify documentation: https://docs.netlify.com
