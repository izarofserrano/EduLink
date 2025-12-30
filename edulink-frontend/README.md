# EduLink Frontend

Vue 3 frontend application for EduLink - A student-centered platform for sharing study materials and campus activities.

## Features

✅ **Search Materials** - Browse and search study documents by course, teacher, semester
✅ **Upload Documents** - Share notes, exams, summaries, and lab reports
✅ **Course Forums** - Ask and answer questions in course-specific forums
✅ **Student Activities** - Post and discover off-campus activities, events, and housing

## Prerequisites

- Node.js 16+ 
- npm or yarn
- EduLink Backend running on http://localhost:8080

## Installation

```bash
# Install dependencies
npm install
```

## Development

```bash
# Start development server
npm run dev
```

The application will be available at http://localhost:3000

## Building for Production

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## Project Structure

```
edulink-frontend/
├── src/
│   ├── pages/
│   │   ├── SearchMaterials.vue    # Home/Search page
│   │   ├── UploadDocument.vue     # Document upload
│   │   ├── CourseForum.vue        # Q&A Forums
│   │   └── StudentActivities.vue  # Campus activities
│   ├── App.vue                    # Main app component
│   ├── main.js                    # Entry point
│   └── style.css                  # Global styles
├── index.html
├── vite.config.js
└── package.json
```

## API Integration

The frontend connects to the backend API running on `http://localhost:8080/api`

### Main Endpoints Used:
- `GET /api/documents` - Fetch all documents
- `POST /api/documents` - Upload new document
- `GET /api/courses` - Get all courses
- `GET /api/activities` - Get all activities
- `POST /api/activities` - Post new activity

## Configuration

The Vite proxy is configured to forward `/api` requests to the backend:

```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

## Pages

### 1. Search Materials (Home)
- Search bar with filters (Course, Teacher, Semester, Tags)
- Document cards showing title, course info, ratings, downloads
- Real-time filtering

### 2. Upload Document
- File upload area (drag & drop or click)
- Form fields: Title, Description, Course, Document Type
- File validation (PDF, DOC, DOCX, PPT - Max 50MB)

### 3. Course Forums
- Course selection dropdown
- Question threads with post information
- Ask question form
- Resolved/unresolved status indicators

### 4. Student Activities
- Activity cards by type (Clubs, Sports, Study Sessions, Events)
- Post new activity form
- Activity type badges with color coding
- Interest counter

## Styling

- Custom CSS with CSS variables for theming
- Responsive design for mobile and desktop
- Clean, modern UI matching the design specifications
- Color scheme:
  - Primary Blue: #2c3e50
  - Accent Blue: #3498db
  - Light backgrounds and card-based layouts

## Technologies

- **Vue 3** - Progressive JavaScript framework
- **Vue Router 4** - Official router for Vue
- **Axios** - HTTP client for API requests
- **Vite** - Next-generation frontend tooling

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Troubleshooting

### Backend Connection Issues
If you see errors connecting to the API:
1. Make sure the backend is running on http://localhost:8080
2. Check `mvn spring-boot:run` is active
3. Verify CORS is configured properly in the backend

### Port Already in Use
If port 3000 is taken, Vite will automatically try 3001, 3002, etc.

Or change the port in `vite.config.js`:
```javascript
server: {
  port: 3001
}
```

## Future Enhancements

- [ ] User authentication and login
- [ ] Real-time chat for forum discussions
- [ ] Document preview
- [ ] Advanced search with filters
- [ ] User profiles and reputation system
- [ ] File upload to Amazon S3
- [ ] Email notifications
- [ ] Dark mode toggle

## License

Created by Izaro Serrano for Interactive Web Technologies course.
