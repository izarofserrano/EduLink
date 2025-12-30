# EduLink Frontend - Quick Start Guide

## 🚀 Get Started in 3 Steps

### Step 1: Install Dependencies

```bash
cd edulink-frontend
npm install
```

This will download all required packages (Vue, Vue Router, Axios, Vite).

### Step 2: Make Sure Backend is Running

The frontend needs the backend API to work. In another terminal:

```bash
cd edulink-backend
mvn spring-boot:run
```

Wait for: `Started EdulinkApplication in X seconds`

### Step 3: Start Frontend Development Server

```bash
npm run dev
```

You should see:
```
  VITE v4.4.9  ready in 500 ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

## 📱 Open the Application

Navigate to: **http://localhost:3000**

You should see the EduLink homepage with the navigation bar and search interface!

## ✅ What You Should See

1. **Navigation Bar** (dark blue) with links:
   - Home
   - Upload
   - Forums
   - Activities
   - Profile

2. **Home Page** with:
   - Search bar
   - Filter buttons (Course, Teacher, Semester, Tags)
   - List of documents from your database

## 🧪 Test the Features

### Search Materials
1. Go to **Home** (http://localhost:3000)
2. Type in the search box
3. Documents filter in real-time
4. Click filter buttons to change filter mode

### Upload Document
1. Click **Upload** in navigation
2. Click the upload area or drag a file
3. Fill in: Title, Description, Course, Type
4. Click "Upload Document"
5. Check the success message

### Course Forums
1. Click **Forums** in navigation
2. Select a course from dropdown
3. Click "+ Ask Question"
4. Fill in title and question
5. Click "Post Question"
6. See your question appear in the list

### Student Activities
1. Click **Activities** in navigation
2. Browse existing activities
3. Click "+ Post Activity"
4. Fill in title, description, type
5. Click "Post Activity"
6. See your activity appear

## 🎨 Customization

### Change Colors

Edit `src/style.css`:

```css
:root {
  --primary-blue: #2c3e50;    /* Navigation bar */
  --accent-blue: #3498db;     /* Buttons, links */
  --light-blue: #5dade2;      /* Hover states */
}
```

### Change Port

Edit `vite.config.js`:

```javascript
server: {
  port: 3001  // Change from 3000 to 3001
}
```

### Add New Pages

1. Create `src/pages/NewPage.vue`
2. Add route in `src/main.js`:
```javascript
{ path: '/newpage', component: NewPage, name: 'newpage' }
```
3. Add link in `src/App.vue`:
```html
<li><router-link to="/newpage">New Page</router-link></li>
```

## 🔧 Troubleshooting

### "Cannot GET /api/documents"
**Problem:** Backend not running
**Solution:** 
```bash
cd edulink-backend
mvn spring-boot:run
```

### "EADDRINUSE: port 3000 already in use"
**Problem:** Another app using port 3000
**Solution:** Vite will auto-try 3001, or kill the other process

### Documents not showing
**Problem:** Database is empty
**Solution:** Run the populate script:
```powershell
cd edulink-backend
.\populate-all-data.ps1
```

### Blank page / White screen
**Problem:** JavaScript error
**Solution:** 
1. Open browser console (F12)
2. Check for errors
3. Make sure all files are in correct locations

## 📦 Building for Production

When ready to deploy:

```bash
# Build optimized production files
npm run build

# Files will be in dist/ folder
# Upload dist/ folder to your web server
```

To test the production build locally:
```bash
npm run preview
```

## 🎯 Next Steps

Now that everything works:

1. ✅ Customize colors and styling
2. ✅ Add more features (user profiles, ratings, etc.)
3. ✅ Implement authentication
4. ✅ Add file upload to S3
5. ✅ Deploy to production (Netlify, Vercel, etc.)

## 💡 Tips

- **Hot Reload**: Changes to `.vue` files reload automatically
- **API Proxy**: `/api/*` requests automatically forward to backend
- **Vue DevTools**: Install browser extension for debugging
- **Component Inspector**: Right-click elements to see component

Enjoy building with EduLink! 🚀
